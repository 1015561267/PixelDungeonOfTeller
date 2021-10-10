/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2016 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.teller.pixeldungeonofteller.ui;

import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.tiles.DungeonTilemap;
import com.teller.pixeldungeonofteller.actors.Actor;
import com.teller.pixeldungeonofteller.actors.Char;
import com.teller.pixeldungeonofteller.items.Item;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.scenes.GameScene;
import com.teller.pixeldungeonofteller.scenes.PixelScene;
import com.teller.pixeldungeonofteller.utils.BArray;
import com.teller.pixeldungeonofteller.windows.WndBag;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Button;
import com.watabou.utils.PathFinder;

public class QuickSlotButton extends Button implements WndBag.Listener {

    public static Char lastTarget = null;
    private static QuickSlotButton[] instance = new QuickSlotButton[4];
    private static Image crossB;
    private static Image crossM;
    private static boolean targeting = false;
    private int slotNum;
    private ItemSlot slot;

    public QuickSlotButton(int slotNum) {
        super();
        this.slotNum = slotNum;
        item(select(slotNum));

        instance[slotNum] = this;
    }

    public QuickSlotButton()
    {
        super();

    }

    public static void reset() {
        instance = new QuickSlotButton[4];

        lastTarget = null;
    }

    private static Item select(int slotNum) {
        return Dungeon.quickslot.getItem(slotNum);
    }

    public static int autoAim(Char target) {
        //will use generic projectile logic if no item is specified
        return autoAim(target, new Item());
    }

    //FIXME: this is currently very expensive, should either optimize ballistica or this, or both
    public static int autoAim(Char target, Item item) {

        //first try to directly target
        if (item.throwPos(Dungeon.hero, target.pos) == target.pos) {
            return target.pos;
        }

        //Otherwise pick nearby tiles to try and 'angle' the shot, auto-aim basically.
        PathFinder.buildDistanceMap(target.pos, BArray.not(new boolean[Dungeon.level.length()], null), 2);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE
                    && item.throwPos(Dungeon.hero, i) == target.pos)
                return i;
        }

        //couldn't find a cell, give up.
        return -1;
    }

    public static void refresh() {
        for (int i = 0; i < instance.length; i++) {
            if (instance[i] != null) {
                instance[i].item(select(i));
            }
        }
    }

    public static void target(Char target) {
        if (target != Dungeon.hero) {
            lastTarget = target;

            HealthIndicator.instance.target(target);
        }
    }

    public static void cancel() {
        if (targeting) {
            crossB.visible = false;
            crossM.remove();
            targeting = false;
        }
    }

    @Override
    public void destroy() {
        super.destroy();

        reset();
    }

    @Override
    protected void createChildren() {
        super.createChildren();

        slot = new ItemSlot() {
            @Override
            protected void onClick() {
                if (targeting) {
                    int cell = autoAim(lastTarget, select(slotNum));

                    if (cell != -1) {
                        GameScene.handleCell(cell);
                    } else {
                        //couldn't auto-aim, just target the position and hope for the best.
                        GameScene.handleCell(lastTarget.pos);
                    }
                } else {
                    Item item = select(slotNum);
                    if (item.usesTargeting)
                        useTargeting();
                    item.execute(Dungeon.hero);
                }
            }

            @Override
            protected boolean onLongClick() {
                return QuickSlotButton.this.onLongClick();
            }

            @Override
            protected void onPointerDown() {
                icon.lightness( 0.7f );
            }
            @Override
            protected void onPointerUp() {
                icon.resetColor();
            }
        };
        slot.showParams(true, false, true);
        add(slot);

        crossB = Icons.TARGET.get();
        crossB.visible = false;
        add(crossB);

        crossM = new Image();
        crossM.copy(crossB);
    }

    @Override
    protected void layout() {
        super.layout();

        slot.fill(this);

        crossB.x = x + (width - crossB.width) / 2;
        crossB.y = y + (height - crossB.height) / 2;
        PixelScene.align(crossB);
    }

    @Override
    protected void onClick() {
        GameScene.selectItem(this, WndBag.Mode.QUICKSLOT, Messages.get(this, "select_item"));
    }

    @Override
    protected boolean onLongClick() {
        GameScene.selectItem(this, WndBag.Mode.QUICKSLOT, Messages.get(this, "select_item"));
        return true;
    }

    @Override
    public void onSelect(Item item) {
        if (item != null) {
            Dungeon.quickslot.setSlot(slotNum, item);
            refresh();
        }
    }

    public void item(Item item) {
        slot.item(item);
        enableSlot();
    }

    public void enable(boolean value) {
        active = value;
        if (value) {
            enableSlot();
        } else {
            slot.enable(false);
        }
    }

    private void enableSlot() {
        slot.enable(Dungeon.quickslot.isNonePlaceholder(slotNum));
    }

    private void useTargeting() {

        if (lastTarget != null &&
                Actor.chars().contains(lastTarget) &&
                lastTarget.isAlive() &&
                Dungeon.visible[lastTarget.pos]) {
            targeting = true;
            lastTarget.sprite.parent.add(crossM);
            crossM.point(lastTarget.sprite.center(crossM));
            crossB.x = x + (width - crossB.width) / 2;
            crossB.y = y + (height - crossB.height) / 2;
            crossB.visible = true;

        } else {

            lastTarget = null;
            targeting = false;

        }

    }

    public static boolean targeting()
    {
        return targeting;
    }

}