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
package com.teller.pixeldungeonofteller.levels.traps;

import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.actors.AbsoluteDamage;
import com.teller.pixeldungeonofteller.actors.Actor;
import com.teller.pixeldungeonofteller.actors.Char;
import com.teller.pixeldungeonofteller.effects.CellEmitter;
import com.teller.pixeldungeonofteller.effects.Lightning;
import com.teller.pixeldungeonofteller.effects.particles.SparkParticle;
import com.teller.pixeldungeonofteller.items.Heap;
import com.teller.pixeldungeonofteller.items.Item;
import com.teller.pixeldungeonofteller.items.wands.Wand;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class LightningTrap extends Trap {

    //FIXME: this is bad, handle when you rework resistances, make into a category
    public static final Electricity LIGHTNING = new Electricity();

    {
        color = TEAL;
        shape = CROSSHAIR;
    }

    @Override
    public void activate() {

        Char ch = Actor.findChar(pos);

        if (ch != null) {
            ch.damage(new AbsoluteDamage(Math.max(1, Random.Int(ch.HP / 3, 2 * ch.HP / 3)), this, ch), LIGHTNING);
            if (ch == Dungeon.hero) {

                Camera.main.shake(2, 0.3f);

                if (!ch.isAlive()) {
                    Dungeon.fail(getClass());
                    GLog.n(Messages.get(this, "ondeath"));
                }
            }

            ArrayList<Lightning.Arc> arcs = new ArrayList<>();
            arcs.add(new Lightning.Arc(pos - Dungeon.level.width(), pos + Dungeon.level.width()));
            arcs.add(new Lightning.Arc(pos - 1, pos + 1));

            ch.sprite.parent.add(new Lightning(arcs, null));
        }

        Heap heap = Dungeon.level.heaps.get(pos);
        if (heap != null) {
            //TODO: this should probably charge staffs too
            Item item = heap.items.peek();
            if (item instanceof Wand) {
                Wand wand = (Wand) item;
                ((Wand) item).curCharges += (int) Math.ceil((wand.maxCharges - wand.curCharges) / 2f);
            }
        }

        CellEmitter.center(pos).burst(SparkParticle.FACTORY, Random.IntRange(3, 4));
    }

    public static class Electricity {
    }
}
