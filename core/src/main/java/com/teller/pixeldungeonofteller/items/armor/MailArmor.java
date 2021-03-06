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
package com.teller.pixeldungeonofteller.items.armor;

import com.teller.pixeldungeonofteller.sprites.ItemSpriteSheet;

public class MailArmor extends Armor {

    {
        image = ItemSpriteSheet.ARMOR_MAIL;
    }

    public MailArmor() {
        super(3);
    }

    @Override
    public int STRReq() {
        return 3;
    }

    @Override
    public int DRMin(int lvl) {
        return 0;
    }

    @Override
    public int DRMax(int lvl) {
        return 3;
    }

    @Override
    public int GETMAXARMOR(int lvl) {
        return 12 + lvl * 3;
    }

    @Override
    public int stealth() { return 2; }
    @Override
    public int GETMAXSHLD(int lvl) {
        return 10 + lvl * 3;
    }
}
