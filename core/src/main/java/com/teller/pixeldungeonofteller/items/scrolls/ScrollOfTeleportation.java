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
import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.actors.Char;
import com.teller.pixeldungeonofteller.actors.buffs.Invisibility;
import com.teller.pixeldungeonofteller.actors.hero.Hero;
import com.teller.pixeldungeonofteller.effects.Speck;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.scenes.GameScene;
import com.teller.pixeldungeonofteller.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;

public class ScrollOfTeleportation extends Scroll {

    {
        initials = 9;
    }

    public static void teleportHero(Hero hero) {

        int count = 10;
        int pos;
        do {
            pos = Dungeon.level.randomRespawnCell();
            if (count-- <= 0) {
                break;
            }
        } while (pos == -1);

        if (pos == -1 || Dungeon.bossLevel()) {

            GLog.w(Messages.get(ScrollOfTeleportation.class, "no_tele"));

        } else {

            appear(hero, pos);
            Dungeon.level.press(pos, hero);
            Dungeon.observe();
            GameScene.updateFog();

            GLog.i(Messages.get(ScrollOfTeleportation.class, "tele"));

        }
    }

    public static void appear(Char ch, int pos) {

        ch.sprite.interruptMotion();

        ch.move(pos);
        ch.sprite.place(pos);

        if (ch.invisible == 0) {
            ch.sprite.alpha(0);
            ch.sprite.parent.add(new AlphaTweener(ch.sprite, 1, 0.4f));
        }

        ch.sprite.emitter().start(Speck.factory(Speck.LIGHT), 0.2f, 3);
        Sample.INSTANCE.play(Assets.SND_TELEPORT);
    }

    @Override
    protected void doRead() {

        Sample.INSTANCE.play(Assets.SND_READ);
        Invisibility.dispel();

        teleportHero(curUser);
        setKnown();

        readAnimation();
    }

    @Override
    public int price() {
        return isKnown() ? 30 * quantity : super.price();
    }
}
