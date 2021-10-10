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
package com.teller.pixeldungeonofteller.levels.features;

import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.actors.hero.Hero;
import com.teller.pixeldungeonofteller.items.Heap;
import com.teller.pixeldungeonofteller.items.Item;
import com.teller.pixeldungeonofteller.items.food.Blandfruit;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.scenes.GameScene;
import com.teller.pixeldungeonofteller.windows.WndBag;
import com.teller.pixeldungeonofteller.windows.WndOptions;

import java.util.Iterator;

public class AlchemyPot {

    public static Hero hero;
    public static int pos;
    private static final WndBag.Listener itemSelector = new WndBag.Listener() {
        @Override
        public void onSelect(Item item) {
            if (item != null) {
                item.cast(hero, pos);
            }
        }
    };
    public static boolean foundFruit;
    public static Item curItem = null;

    public static void operate(Hero hero, int pos) {

        AlchemyPot.hero = hero;
        AlchemyPot.pos = pos;

        Iterator<Item> items = hero.belongings.iterator();
        foundFruit = false;
        Heap heap = Dungeon.level.heaps.get(pos);

        if (heap == null)
            while (items.hasNext() && !foundFruit) {
                curItem = items.next();
                if (curItem instanceof Blandfruit && ((Blandfruit) curItem).potionAttrib == null) {
                    GameScene.show(
                            new WndOptions(Messages.get(AlchemyPot.class, "pot"),
                                    Messages.get(AlchemyPot.class, "options"),
                                    Messages.get(AlchemyPot.class, "fruit"),
                                    Messages.get(AlchemyPot.class, "potion")) {
                                @Override
                                protected void onSelect(int index) {
                                    if (index == 0) {
                                        curItem.cast(AlchemyPot.hero, AlchemyPot.pos);
                                    } else
                                        GameScene.selectItem(itemSelector, WndBag.Mode.SEED, Messages.get(AlchemyPot.class, "select_seed"));
                                }
                            }
                    );
                    foundFruit = true;
                }
            }

        if (!foundFruit)
            GameScene.selectItem(itemSelector, WndBag.Mode.SEED, Messages.get(AlchemyPot.class, "select_seed"));
    }
}
