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
package com.teller.pixeldungeonofteller.plants;

import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.actors.Actor;
import com.teller.pixeldungeonofteller.actors.Char;
import com.teller.pixeldungeonofteller.actors.Damage;
import com.teller.pixeldungeonofteller.actors.buffs.Buff;
import com.teller.pixeldungeonofteller.actors.hero.Hero;
import com.teller.pixeldungeonofteller.effects.CellEmitter;
import com.teller.pixeldungeonofteller.effects.Speck;
import com.teller.pixeldungeonofteller.effects.particles.ShaftParticle;
import com.teller.pixeldungeonofteller.items.potions.PotionOfHealing;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.sprites.ItemSpriteSheet;
import com.teller.pixeldungeonofteller.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class Sungrass extends Plant {

    {
        image = 4;
    }

    @Override
    public void activate() {
        Char ch = Actor.findChar(pos);

        if (ch == Dungeon.hero) {
            Buff.affect(ch, Health.class).boost(ch.HT);
        }

        if (Dungeon.visible[pos]) {
            CellEmitter.get(pos).start(ShaftParticle.FACTORY, 0.2f, 3);
        }
    }

    public static class Seed extends Plant.Seed {
        {
            image = ItemSpriteSheet.SEED_SUNGRASS;

            plantClass = Sungrass.class;
            alchemyClass = PotionOfHealing.class;

            bones = true;
        }
    }

    public static class Health extends Buff {

        private static final float STEP = 1f;
        private static final String POS = "pos";
        private static final String HEALCURR = "healCurr";
        private static final String COUNT = "count";
        private static final String LEVEL = "level";
        private int pos;
        private int healCurr = 1;
        private int count = 0;
        private int level;

        {
            type = buffType.POSITIVE;
        }

        @Override
        public boolean act() {
            if (target.pos != pos) {
                detach();
            }
            if (count == 5) {
                if (level <= healCurr * .025 * target.HT) {
                    target.HP = Math.min(target.HT, target.HP + level);
                    target.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
                    detach();
                } else {
                    target.HP = Math.min(target.HT, target.HP + (int) (healCurr * .025 * target.HT));
                    level -= (healCurr * .025 * target.HT);
                    if (healCurr < 6)
                        healCurr++;
                    target.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
                }
                if (target.HP == target.HT && target instanceof Hero) {
                    ((Hero) target).resting = false;
                }
                count = 1;
            } else {
                count++;
            }
            if (level <= 0)
                detach();
            spend(STEP);
            return true;
        }

        public Damage absorb(Damage damage) {
            level -= damage.sum();
            if (level <= 0)
                detach();
            return damage;
        }

        public void boost(int amount) {
            level += amount;
            pos = target.pos;
        }

        @Override
        public int icon() {
            return BuffIndicator.HEALING;
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", level);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(POS, pos);
            bundle.put(HEALCURR, healCurr);
            bundle.put(COUNT, count);
            bundle.put(LEVEL, level);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            pos = bundle.getInt(POS);
            healCurr = bundle.getInt(HEALCURR);
            count = bundle.getInt(COUNT);
            level = bundle.getInt(LEVEL);

        }
    }
}
