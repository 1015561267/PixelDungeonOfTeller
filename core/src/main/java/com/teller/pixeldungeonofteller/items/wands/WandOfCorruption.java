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
package com.teller.pixeldungeonofteller.items.wands;

import com.teller.pixeldungeonofteller.Assets;
import com.teller.pixeldungeonofteller.actors.Actor;
import com.teller.pixeldungeonofteller.actors.Char;
import com.teller.pixeldungeonofteller.actors.Damage;
import com.teller.pixeldungeonofteller.actors.buffs.Amok;
import com.teller.pixeldungeonofteller.actors.buffs.Buff;
import com.teller.pixeldungeonofteller.actors.buffs.Corruption;
import com.teller.pixeldungeonofteller.effects.MagicMissile;
import com.teller.pixeldungeonofteller.items.weapon.weapons.MainHandWeapon.MagesStaff;
import com.teller.pixeldungeonofteller.mechanics.Ballistica;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.sprites.ItemSpriteSheet;
import com.teller.pixeldungeonofteller.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class WandOfCorruption extends Wand {

    {
        image = ItemSpriteSheet.WAND_CORRUPTION;
    }

    @Override
    protected void onZap(Ballistica bolt) {
        Char ch = Actor.findChar(bolt.collisionPos);

        if (ch != null) {

            if (ch.buff(Corruption.class) != null) {
                GLog.w(Messages.get(this, "already_corrupted"));
                return;
            }

            if (ch.properties().contains(Char.Property.BOSS) || ch.properties().contains(Char.Property.MINIBOSS)) {
                GLog.w(Messages.get(this, "boss"));
                return;
            }

            int basePower = 10 + 2 * level();
            int mobPower = Random.IntRange(0, ch.HT + ch.SHLD) + ch.HP * 2 + ch.MAXSHLD * 2;
            for (Buff buff : ch.buffs()) {
                if (buff.type == Buff.buffType.NEGATIVE) {
                    mobPower *= 0.67;
                    break;
                }
            }

            int extraCharges = 0;
            //try to use extra charges to overpower the mob
            while (basePower <= mobPower) {
                extraCharges++;
                basePower += 5 + level();
            }

            //if we fail, lose all charges, remember we have 1 left to lose from using the wand.
            if (extraCharges >= curCharges) {
                curCharges = 1;
                GLog.w(Messages.get(this, "fail"));
                return;
            }

            //otherwise corrupt the mob & spend charges
            Buff.append(ch, Corruption.class);
            ch.HP = ch.HT;
            curCharges -= extraCharges;
            usagesToKnow -= extraCharges;

            processSoulMark(ch, extraCharges + chargesPerCast());
        }
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, Damage damage) {
        // lvl 0 - 25%
        // lvl 1 - 40%
        // lvl 2 - 50%
        if (Random.Int(level() + 4) >= 3) {
            Buff.prolong(defender, Amok.class, 3 + level());
        }
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        MagicMissile.shadow(curUser.sprite.parent, bolt.sourcePos, bolt.collisionPos, callback);
        Sample.INSTANCE.play(Assets.SND_ZAP);
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0);
        particle.am = 0.6f;
        particle.setLifespan(2f);
        particle.speed.set(0, 5);
        particle.setSize(0.5f, 2f);
        particle.shuffleXY(1f);
    }

}
