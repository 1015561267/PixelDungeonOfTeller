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
package com.teller.pixeldungeonofteller.items.scrolls;

import com.teller.pixeldungeonofteller.Assets;
import com.teller.pixeldungeonofteller.actors.buffs.Buff;
import com.teller.pixeldungeonofteller.actors.buffs.Invisibility;
import com.teller.pixeldungeonofteller.actors.buffs.Recharging;
import com.teller.pixeldungeonofteller.actors.hero.Hero;
import com.teller.pixeldungeonofteller.effects.SpellSprite;
import com.teller.pixeldungeonofteller.effects.particles.EnergyParticle;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class ScrollOfRecharging extends Scroll {

    public static final float BUFF_DURATION = 30f;

    {
        initials = 7;
    }

    public static void charge(Hero hero) {
        hero.sprite.centerEmitter().burst(EnergyParticle.FACTORY, 15);
    }

    @Override
    protected void doRead() {

        Buff.affect(curUser, Recharging.class, BUFF_DURATION);
        charge(curUser);

        Sample.INSTANCE.play(Assets.SND_READ);
        Invisibility.dispel();

        GLog.i(Messages.get(this, "surge"));
        SpellSprite.show(curUser, SpellSprite.CHARGE);
        setKnown();

        readAnimation();
    }

    @Override
    public int price() {
        return isKnown() ? 40 * quantity : super.price();
    }
}
