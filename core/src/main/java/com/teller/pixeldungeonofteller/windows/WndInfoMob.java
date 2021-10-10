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
package com.teller.pixeldungeonofteller.windows;

import com.teller.pixeldungeonofteller.actors.mobs.Mob;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.scenes.PixelScene;
import com.teller.pixeldungeonofteller.sprites.CharSprite;
import com.teller.pixeldungeonofteller.ui.BuffIndicator;
import com.teller.pixeldungeonofteller.ui.HealthBar;
import com.teller.pixeldungeonofteller.ui.RenderedTextBlock;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.ui.Component;

public class WndInfoMob extends WndTitledMessage {

    public WndInfoMob(Mob mob) {

        super(new MobTitle(mob), desc(mob));

    }

    private static String desc(Mob mob) {

        StringBuilder builder = new StringBuilder(mob.description());

        builder.append("\n\n" + mob.state.status());

        return builder.toString();
    }

    private static class MobTitle extends Component {

        private static final int GAP = 2;

        private CharSprite image;
        private RenderedTextBlock name;
        private HealthBar health;
        private BuffIndicator buffs;

        public MobTitle(Mob mob) {

            name = PixelScene.renderTextBlock(Messages.titleCase(mob.name), 9);
            name.hardlight(TITLE_COLOR);
            add(name);

            image = mob.sprite();
            add(image);

            health = new HealthBar();
            health.level(mob);
            add(health);

            buffs = new BuffIndicator(mob);
            add(buffs);
        }

        @Override
        protected void layout() {

            image.x = 0;
            image.y = Math.max(0, name.height() + GAP + health.height() - image.height);

            name.setPos(x + image.width + GAP,
                    image.height() > name.height() ? y +(image.height() - name.height()) / 2 : y);

            float w = width - image.width - GAP;

            health.setRect(image.width + GAP, image.height - health.height(), w, health.height());

            buffs.setPos(
                    name.right() + GAP-1,
                    name.bottom() - BuffIndicator.SIZE-2
            );
            height = health.bottom();
        }
    }
}
