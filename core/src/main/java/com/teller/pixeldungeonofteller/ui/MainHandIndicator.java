package com.teller.pixeldungeonofteller.ui;

import com.teller.pixeldungeonofteller.Chrome;
import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.actors.Actor;
import com.teller.pixeldungeonofteller.actors.Char;
import com.teller.pixeldungeonofteller.actors.hero.Hero;
import com.teller.pixeldungeonofteller.items.Item;
import com.teller.pixeldungeonofteller.items.KindOfWeapon;
import com.teller.pixeldungeonofteller.items.weapon.Weapon;
import com.teller.pixeldungeonofteller.items.weapon.weapons.FireArm.Flintlock;
import com.teller.pixeldungeonofteller.items.weapon.weapons.MainHandWeapon.WornShortsword;
import com.teller.pixeldungeonofteller.scenes.GameScene;
import com.teller.pixeldungeonofteller.scenes.PixelScene;
import com.teller.pixeldungeonofteller.tiles.DungeonTilemap;
import com.teller.pixeldungeonofteller.utils.BArray;
import com.teller.pixeldungeonofteller.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Button;
import com.watabou.utils.PathFinder;

public class MainHandIndicator extends Button {

    public static Char lastTarget = null;

    protected NinePatch bg;
    protected float lightness = 0;
    private float r;
    private float g;
    private float b;

    public void flash() {
        lightness = 1f;
    }//although it sounds damn stupid,but extends tag means unable to auto-target,so I have had to copy its visual and add the auto-targeting feature,may create a father class for these two later

    public void flip(boolean value) {
        bg.flipHorizontal(value);
    }

    Image icon;

    private static Image crossB;
    private static Image crossM;
    private static boolean targeting = false;
    //private static Image crossM=Icons.TARGET.get();;

    private ItemSlot slot;

    private static KindOfWeapon weapon = null;

    public MainHandIndicator() {

        int color=0x7C8072;
        this.r = (color >> 16) / 255f;
        this.g = ((color >> 8) & 0xFF) / 255f;
        this.b = (color & 0xFF) / 255f;

        bg.ra = bg.ga = bg.ba = 0;
        bg.rm = (0x7C8072 >> 16) / 255f;
        bg.gm = ((0x7C8072 >> 8) & 0xFF) / 255f;
        bg.bm = (0x7C8072 & 0xFF) / 255f;

        setSize( 24, 22 );

        visible=false;
    }

    @Override
    protected void createChildren() {

        super.createChildren();

        bg = Chrome.get(Chrome.Type.TAG);
        bg.hardlight(r, g, b);
        add(bg);

        slot = new ItemSlot() {

            protected void onClick() {
                if (targeting) {
                    int cell = autoAim(lastTarget);

                    if (cell != -1) {
                        GameScene.handleCell(cell);
                    } else {
                        //couldn't auto-aim, just target the position and hope for the best.
                        GameScene.handleCell(lastTarget.pos);
                    }
                } else {
                    if (weapon.usesTargeting)
                        useTargeting();

                    if(weapon instanceof Flintlock) {
                        item.execute(Dungeon.hero, ((Flintlock) weapon).AC_SHOOT);
                    }
                    else if(weapon.defaultAction!=null) {
                        item.execute( Dungeon.hero, weapon.defaultAction);
                    }
                }
                flash();
            };

            protected boolean onLongClick() {
                if (targeting) {
                    int cell = autoAim(lastTarget);

                    if (cell != -1) {
                        GameScene.handleCell(cell);
                    } else {
                        //couldn't auto-aim, just target the position and hope for the best.
                        GameScene.handleCell(lastTarget.pos);
                    }
                } else {
                    if (weapon.usesTargeting)
                        useTargeting();

                    if(weapon instanceof Flintlock) {
                        item.execute(Dungeon.hero, ((Flintlock) weapon).AC_DOUBLESHOOT);
                    }
                    else if(weapon.defaultAction!=null) {
                        item.execute( Dungeon.hero, weapon.defaultAction);
                    }
                }
                flash();
                return true;
            };
        };

        slot.setScale(0.8f);
        add( slot );

        crossB = Icons.TARGET.get();
        crossB.visible = false;
        add(crossB);

        crossM = new Image();
        crossM.copy(crossB);
    }

    @Override
    protected void layout() {
        super.layout();

        bg.x = x;
        bg.y = y;
        bg.size(width, height);

        bg.scale.x = -1.0f;
        bg.x += bg.width;
        bg.y=y;

        slot.setRect( x + 2, y + 2, width - 5, height - 4 );

        //crossB.x = x + (width - crossB.width) / 2;
        //crossB.y = y + (height - crossB.height) / 2;
        //PixelScene.align(crossB);

    }

    @Override
    public void update() {
        super.update();

        if (visible && lightness > 0.5) {
            if ((lightness -= Game.elapsed) > 0.5) {
                bg.ra = bg.ga = bg.ba = 2 * lightness - 1;
                bg.rm = 2 * r * (1 - lightness);
                bg.gm = 2 * g * (1 - lightness);
                bg.bm = 2 * b * (1 - lightness);
            } else {
                bg.hardlight(r, g, b);
            }
        }

        slot.item(weapon);
        slot.enable(Dungeon.hero.isAlive() && Dungeon.hero.ready);
        if (weapon == null) {
            visible = false;
        }
        else visible=true;
    }
    public void update(Weapon Weapon) {
        weapon=Weapon;
        update();
    }

    public void initialization(Hero hero)
    {
       update((Weapon)hero.belongings.mainhandweapon);
    }

    public static int autoAim(Char target) {

        //first try to directly target
        if (weapon.throwPos(Dungeon.hero, target.pos) == target.pos) {
            return target.pos;
        }

        //Otherwise pick nearby tiles to try and 'angle' the shot, auto-aim basically.
        PathFinder.buildDistanceMap(target.pos, BArray.not(new boolean[Dungeon.level.length()], null), 2);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE
                    && weapon.throwPos(Dungeon.hero, i) == target.pos)
                return i;
        }

        //couldn't find a cell, give up.
        return -1;
    }

    public static void target(Char target) {
        if (target != Dungeon.hero) {
            lastTarget = target;
            HealthIndicator.instance.target(target);
        }
    }

    private void useTargeting() {

        if (lastTarget != null &&
                Actor.chars().contains(lastTarget) &&
                lastTarget.isAlive() &&
                Dungeon.visible[lastTarget.pos]) {

            targeting = true;
            lastTarget.sprite.parent.add(crossM);
            //crossM.point(DungeonTilemap.tileToWorld(lastTarget.pos));

            crossM.point(lastTarget.sprite.center(crossM));

            //crossB.x = x + (width - crossB.width) / 2;
            //crossB.y = y + (height - crossB.height) / 2;
            //crossB.visible = true;

        } else {

            lastTarget = null;
            targeting = false;

        }

    }

    public static boolean targeting()
    {
        return targeting;
    }

    public static void cancel() {
        if (targeting) {
            crossB.visible = false;
            crossM.remove();
            targeting = false;
        }
    }
}
