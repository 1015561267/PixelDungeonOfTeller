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
package com.teller.pixeldungeonofteller.actors.buffs;

import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.actors.Char;
import com.teller.pixeldungeonofteller.actors.hero.Hero;
import com.teller.pixeldungeonofteller.actors.mobs.Thief;
import com.teller.pixeldungeonofteller.items.Item;
import com.teller.pixeldungeonofteller.items.food.FrozenCarpaccio;
import com.teller.pixeldungeonofteller.items.food.MysteryMeat;
import com.teller.pixeldungeonofteller.items.potions.Potion;
import com.teller.pixeldungeonofteller.items.potions.PotionOfMight;
import com.teller.pixeldungeonofteller.items.potions.PotionOfStrength;
import com.teller.pixeldungeonofteller.items.rings.RingOfElements.Resistance;
import com.teller.pixeldungeonofteller.levels.Level;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.sprites.CharSprite;
import com.teller.pixeldungeonofteller.ui.BuffIndicator;
import com.teller.pixeldungeonofteller.utils.GLog;

public class Frost extends FlavourBuff {

    private static final float DURATION = 5f;

    {
        type = buffType.NEGATIVE;
    }

    public static float duration(Char ch) {
        Resistance r = ch.buff(Resistance.class);
        return r != null ? r.durationFactor() * DURATION : DURATION;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {

            target.paralysed++;
            Buff.detach(target, Burning.class);
            Buff.detach(target, Chill.class);

            if (target instanceof Hero) {

                Hero hero = (Hero) target;
                Item item = hero.belongings.randomUnequipped();
                if (item instanceof Potion
                        && !(item instanceof PotionOfStrength || item instanceof PotionOfMight)) {

                    item = item.detach(hero.belongings.backpack);
                    GLog.w(Messages.get(this, "freezes", item.toString()));
                    ((Potion) item).shatter(hero.pos);

                } else if (item instanceof MysteryMeat) {

                    item = item.detach(hero.belongings.backpack);
                    FrozenCarpaccio carpaccio = new FrozenCarpaccio();
                    if (!carpaccio.collect(hero.belongings.backpack)) {
                        Dungeon.level.drop(carpaccio, target.pos).sprite.drop();
                    }
                    GLog.w(Messages.get(this, "freezes", item.toString()));

                }
            } else if (target instanceof Thief) {

                Item item = ((Thief) target).item;

                if (item instanceof Potion && !(item instanceof PotionOfStrength || item instanceof PotionOfMight)) {
                    ((Potion) ((Thief) target).item).shatter(target.pos);
                    ((Thief) target).item = null;
                }

            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void detach() {
        super.detach();
        if (target.paralysed > 0)
            target.paralysed--;
        if (Dungeon.level.water[target.pos])
            Buff.prolong(target, Chill.class, 4f);
    }

    @Override
    public int icon() {
        return BuffIndicator.FROST;
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.FROZEN);
        else target.sprite.remove(CharSprite.State.FROZEN);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }
}
