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

import com.teller.pixeldungeonofteller.Assets;
import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.actors.Actor;
import com.teller.pixeldungeonofteller.actors.buffs.Buff;
import com.teller.pixeldungeonofteller.actors.buffs.Burning;
import com.teller.pixeldungeonofteller.actors.buffs.Roots;
import com.teller.pixeldungeonofteller.actors.mobs.Mob;
import com.teller.pixeldungeonofteller.effects.particles.ElmoParticle;
import com.teller.pixeldungeonofteller.levels.Level;
import com.teller.pixeldungeonofteller.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class MageArmor extends ClassArmor {

    {
        image = ItemSpriteSheet.ARMOR_MAGE;
    }

    @Override
    public void doSpecial() {

        for (Mob mob : Dungeon.level.mobs) {
            if (Dungeon.hero.fieldOfView[mob.pos]) {
                Buff.affect(mob, Burning.class).reignite(mob);
                Buff.prolong(mob, Roots.class, 3);
            }
        }

        curUser.HP -= (curUser.HP / 3);

        curUser.spend(Actor.TICK);
        curUser.sprite.operate(curUser.pos);
        curUser.busy();

        curUser.sprite.centerEmitter().start(ElmoParticle.FACTORY, 0.15f, 4);
        Sample.INSTANCE.play(Assets.SND_READ);
    }

}