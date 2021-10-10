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
package com.teller.pixeldungeonofteller.scenes;

import com.teller.pixeldungeonofteller.Assets;
import com.teller.pixeldungeonofteller.Badges;
import com.teller.pixeldungeonofteller.PDSettings;
import com.teller.pixeldungeonofteller.PixelDungeonOfTeller;
import com.teller.pixeldungeonofteller.effects.BadgeBanner;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.ui.Archs;
import com.teller.pixeldungeonofteller.ui.BadgesGrid;
import com.teller.pixeldungeonofteller.ui.ExitButton;
import com.teller.pixeldungeonofteller.ui.RenderedTextBlock;
import com.teller.pixeldungeonofteller.ui.Window;
import com.teller.pixeldungeonofteller.windows.WndBadge;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.List;

public class BadgesScene extends PixelScene {

    @Override
    public void create() {

        super.create();

        float margin = 5;

        Music.INSTANCE.play(Assets.THEME, true);
        Music.INSTANCE.volume(PDSettings.musicVol() / 10f);

        uiCamera.visible = false;

        int w = Camera.main.width;
        int h = Camera.main.height;

        Archs archs = new Archs();
        archs.setSize(w, h);
        add(archs);

        float pw = Math.min(w, (landscape() ? MIN_WIDTH_L : MIN_WIDTH_P) * 3) - 16;
        float ph = Math.min(h, (landscape() ? MIN_HEIGHT_L : MIN_HEIGHT_P) * 3) - 32;

        float size = (float) Math.sqrt(pw * ph / 27f);
        int nCols = (int) Math.ceil(pw / size);
        int nRows = (int) Math.ceil(ph / size);
        size = Math.min(pw / nCols, ph / nRows);

        float left = (w - size * nCols) / 2;
        float top = (h - size * nRows) / 2;

        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
        title.hardlight(Window.TITLE_COLOR);

        title.setPos((w - title.width()) / 2f,
                (top - title.height()) / 2f );
        align(title);
        add(title);

        Badges.loadGlobal();


        /*List<Badges.Badge> badges = Badges.filtered(true);
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                int index = i * nCols + j;
                Badges.Badge b = index < badges.size() ? badges.get(index) : null;
                BadgeButton button = new BadgeButton(b);
                button.setPos(
                        left + j * size + (size - button.width()) / 2,
                        top + i * size + (size - button.height()) / 2);
                align(button);
                add(button);
            }
        }*/

        Badges.loadGlobal();
        BadgesGrid grid = new BadgesGrid(true);
        grid.setRect(margin, top, w-(2*margin), h-top-margin);
        add(grid);

        ExitButton btnExit = new ExitButton();
        btnExit.setPos(Camera.main.width - btnExit.width(), 0);
        add(btnExit);

        fadeIn();

        /*Badges.loadingListener = new Callback() {
            @Override
            public void call() {
                if (Game.scene() == BadgesScene.this) {
                    PixelDungeonOfTeller.switchNoFade(BadgesScene.class);
                }
            }
        };*/
    }

    @Override
    public void destroy() {

        Badges.saveGlobal();
        Badges.loadingListener = null;

        super.destroy();
    }

    @Override
    protected void onBackPressed() {
        PixelDungeonOfTeller.switchNoFade(TitleScene.class);
    }

    /*
    private static class BadgeButton extends Button {

        private Badges.Badge badge;

        private Image icon;

        public BadgeButton(Badges.Badge badge) {
            super();

            this.badge = badge;
            active = (badge != null);

            icon = active ? BadgeBanner.image(badge.image) : new Image(Assets.LOCKED);
            add(icon);

            setSize(icon.width(), icon.height());
        }

        @Override
        protected void layout() {
            super.layout();

            icon.x = x + (width - icon.width()) / 2;
            icon.y = y + (height - icon.height()) / 2;
        }

        @Override
        public void update() {
            super.update();

            if (Random.Float() < Game.elapsed * 0.1) {
                BadgeBanner.highlight(icon, badge.image);
            }
        }

        @Override
        protected void onClick() {
            Sample.INSTANCE.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f);
            Game.scene().add(new WndBadge(badge));
        }
    }*/
}
