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
package com.teller.pixeldungeonofteller.actors.mobs;

import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.actors.buffs.Buff;
import com.teller.pixeldungeonofteller.actors.buffs.Chill;
import com.teller.pixeldungeonofteller.actors.buffs.Frost;
import com.teller.pixeldungeonofteller.items.quest.Embers;
import com.teller.pixeldungeonofteller.sprites.NewbornElementalSprite;

public class NewbornElemental extends Elemental {

    {
        spriteClass = NewbornElementalSprite.class;

        //HT = 65;
        //HP = HT/2; //32
        HP = 20;
        HT = 40;
        ARMOR = 0;
        SlashThreshold=0;
        SHLD = 30;
        MAXSHLD = 60;

        defenseSkill = 12;

        EXP = 7;

        properties.add(Property.MINIBOSS);
    }

    @Override
    public void add(Buff buff) {
        if (buff instanceof Frost || buff instanceof Chill) {
            die(buff);
        } else {
            super.add(buff);
        }
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        Dungeon.level.drop(new Embers(), pos).sprite.drop();
    }
}
