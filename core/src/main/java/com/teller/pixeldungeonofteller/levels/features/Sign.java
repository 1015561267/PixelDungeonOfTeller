/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
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
package com.teller.pixeldungeonofteller.levels.features;

import com.teller.pixeldungeonofteller.Assets;
import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.effects.CellEmitter;
import com.teller.pixeldungeonofteller.effects.particles.ElmoParticle;
import com.teller.pixeldungeonofteller.levels.DeadEndLevel;
import com.teller.pixeldungeonofteller.levels.Terrain;
import com.teller.pixeldungeonofteller.messages.Languages;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.scenes.GameScene;
import com.teller.pixeldungeonofteller.utils.GLog;
import com.teller.pixeldungeonofteller.windows.WndMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class Sign {

    private static final String[] teaser_texts = new String[]{
            "G<@8R9BER4RA8JRC8EFC86G<I8 _0_",
            "G;8RJ4??FR4E8RF;<9G<A: _5_",
            "G;8R7HA:8BARJ<??R58RF;4GG8E87 _0_"
    };

    public static void read(int pos) {

        if (Dungeon.level instanceof DeadEndLevel) {
            Game.runOnRenderThread(new Callback() {
                @Override
                public void call() {
                    GameScene.show(new WndMessage(Messages.get(Sign.class, "dead_end")));
                }
            });
        } else {
            if (Dungeon.depth <= 21) {
                Game.runOnRenderThread(new Callback() {
                    @Override
                    public void call() {
                        GameScene.show(new WndMessage(Messages.get(Sign.class, "tip_" + Dungeon.depth)));
                    }
                });
            } else {

                //if we are at depths 22-24 and in english
                if (Dungeon.depth - 21 <= 3 && Messages.lang() == Languages.ENGLISH) {
                    GameScene.show(new WndMessage(teaser_texts[Dungeon.depth - 22]));
                }

                Dungeon.level.destroy(pos);
                GameScene.updateMap(pos);
                GameScene.discoverTile(pos, Terrain.SIGN);

                GLog.w(Messages.get(Sign.class, "burn"));

                CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6);
                Sample.INSTANCE.play(Assets.SND_BURNING);
            }

        }
    }
}
