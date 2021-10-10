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
import com.teller.pixeldungeonofteller.actors.MagicalDamage;
import com.teller.pixeldungeonofteller.actors.hero.Hero;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class Venom extends Poison implements Hero.Doom {

    private static final String DAMAGE = "damage";
    private int damage = 1;

    {
        type = buffType.NEGATIVE;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DAMAGE, damage);

    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        damage = bundle.getInt(DAMAGE);
    }

    public void set(float duration, int damage) {
        set(duration);
        if (this.damage < damage) this.damage = damage;
    }

    @Override
    public int icon() {
        return BuffIndicator.POISON;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns(left), damage);
    }

    @Override
    public boolean act() {
        if (target.isAlive()) {
            MagicalDamage magicalDamage = new MagicalDamage();
            magicalDamage.AddShadow(damage);
            target.damage(magicalDamage, this);
            if (damage < ((Dungeon.depth + 1) / 2) + 1)
                damage++;
            //want it to act after the cloud of venom it came from.
            spend(TICK + 0.1f);
            if ((left -= TICK) <= 0) {
                detach();
            }
        } else {
            detach();
        }
        return true;
    }
}