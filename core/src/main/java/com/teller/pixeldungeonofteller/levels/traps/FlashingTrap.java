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

import com.teller.pixeldungeonofteller.Assets;
import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.actors.Actor;
import com.teller.pixeldungeonofteller.actors.Char;
import com.teller.pixeldungeonofteller.actors.buffs.Blindness;
import com.teller.pixeldungeonofteller.actors.buffs.Buff;
import com.teller.pixeldungeonofteller.actors.buffs.Cripple;
import com.teller.pixeldungeonofteller.actors.mobs.Mob;
import com.teller.pixeldungeonofteller.effects.CellEmitter;
import com.teller.pixeldungeonofteller.effects.Speck;
import com.teller.pixeldungeonofteller.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class FlashingTrap extends Trap {

    {
        color = YELLOW;
        shape = STARS;
    }

    @Override
    public void activate() {
        Char ch = Actor.findChar(pos);

        if (ch != null) {
            int len = Random.Int(5, 10) + Dungeon.depth;
            Buff.prolong(ch, Blindness.class, len);
            Buff.prolong(ch, Cripple.class, len);
            if (ch instanceof Mob) {
                if (((Mob) ch).state == ((Mob) ch).HUNTING) ((Mob) ch).state = ((Mob) ch).WANDERING;
                ((Mob) ch).beckon(Dungeon.level.randomDestination());
            }
            if (ch == Dungeon.hero) {
                Sample.INSTANCE.play(Assets.SND_BLAST);
            }
        }

        if (Dungeon.visible[pos]) {
            GameScene.flash(0xFFFFFF);
            CellEmitter.get(pos).burst(Speck.factory(Speck.LIGHT), 4);
        }
    }

}
