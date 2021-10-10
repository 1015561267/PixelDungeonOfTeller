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
import com.teller.pixeldungeonofteller.PDSettings;
import com.teller.pixeldungeonofteller.Rankings;
import com.teller.pixeldungeonofteller.PixelDungeonOfTeller;
import com.teller.pixeldungeonofteller.effects.BannerSprites;
import com.teller.pixeldungeonofteller.effects.Fireball;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.ui.RedButton;
import com.teller.pixeldungeonofteller.ui.RenderedTextBlock;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.FileUtils;

public class WelcomeScene extends PixelScene {

    private static int LATEST_UPDATE = 1;

    @Override
    public void create() {
        super.create();

        final int previousVersion = PDSettings.version();

        if (PixelDungeonOfTeller.versionCode == previousVersion) {
            PixelDungeonOfTeller.switchNoFade(TitleScene.class);
            return;
        }

        uiCamera.visible = false;

        int w = Camera.main.width;
        int h = Camera.main.height;

        Image title = BannerSprites.get(BannerSprites.Type.PIXEL_DUNGEON);
        title.brightness(0.6f);
        add(title);

        float topRegion = Math.max(95f, h * 0.45f);

        title.x = (w - title.width()) / 2f;
        if (landscape())
            title.y = (topRegion - title.height()) / 2f;
        else
            title.y = 16 + (topRegion - title.height() - 16) / 2f;

        align(title);

        Image signs = new Image(BannerSprites.get(BannerSprites.Type.PIXEL_DUNGEON_SIGNS)) {
            private float time = 0;

            @Override
            public void update() {
                super.update();
                am = (float) Math.sin(-(time += Game.elapsed));
            }

            @Override
            public void draw() {
                Blending.setLightMode();
                super.draw();
                Blending.setNormalMode();
            }
        };
        signs.x = title.x + (title.width() - signs.width()) / 2f;
        signs.y = title.y;
        add(signs);

        DarkRedButton okay = new DarkRedButton(Messages.get(this, "continue")) {
            @Override
            protected void onClick() {
                super.onClick();
                updateVersion(previousVersion);
                PixelDungeonOfTeller.switchScene(TitleScene.class);
            }
        };

        if (previousVersion != 0) {
            DarkRedButton changes = new DarkRedButton(Messages.get(this, "changelist")) {
                @Override
                protected void onClick() {
                    super.onClick();
                    updateVersion(previousVersion);
                    PixelDungeonOfTeller.switchScene(ChangesScene.class);
                }
            };
            okay.setRect(title.x, h - 20, (title.width() / 2) - 2, 16);
            okay.textColor(0xBBBB33);
            add(okay);

            changes.setRect(okay.right() + 2, h - 20, (title.width() / 2) - 2, 16);
            changes.textColor(0xBBBB33);
            add(changes);
        } else {
            okay.setRect(title.x, h - 20, title.width(), 16);
            okay.textColor(0xBBBB33);
            add(okay);
        }

        RenderedTextBlock text = PixelScene.renderTextBlock(6);
        String message;
        if (previousVersion == 0) {
            message = Messages.get(this, "welcome_msg");
        } else if (previousVersion <= PixelDungeonOfTeller.versionCode) {
            if (previousVersion < LATEST_UPDATE) {
                message = Messages.get(this, "update_intro");
                message += "\n\n" + Messages.get(this, "update_msg");
            } else {
                //TODO: change the messages here in accordance with the type of patch.
                message = Messages.get(this, "patch_intro");
                message += "\n\n" + Messages.get(this, "patch_bugfixes");
                message += "\n" + Messages.get(this, "patch_translations");
                message += "\n" + Messages.get(this, "patch_balance");

            }
        } else {
            message = Messages.get(this, "what_msg");
        }
        text.text(message, w - 20);
        float textSpace = h - title.y - (title.height() - 10) - okay.height() - 2;
        text.setPos((w - text.width()) / 2f, title.y + (title.height() - 10) + ((textSpace - text.height()) / 2));
        add(text);

    }

    private void updateVersion(int previousVersion) {
        //rankings conversion
        if (previousVersion < LATEST_UPDATE){
            try {
                Rankings.INSTANCE.load();
                Rankings.INSTANCE.save();
            } catch (Exception e) {
                //if we encounter a fatal error, then just clear the rankings
                FileUtils.deleteFile( Rankings.RANKINGS_FILE );
            }
        }

        //convert game saves from the old format
        if (previousVersion <= LATEST_UPDATE){

        }

        PDSettings.version(PixelDungeonOfTeller.versionCode);
    }

    private void placeTorch(float x, float y) {
        Fireball fb = new Fireball();
        fb.setPos(x, y);
        add(fb);
    }

    private class DarkRedButton extends RedButton {
        {
            bg.brightness(0.4f);
        }

        DarkRedButton(String text) {
            super(text);
        }

        @Override
        protected void onPointerDown() {
            bg.brightness(0.5f);
            Sample.INSTANCE.play( Assets.SND_CLICK );
        }

        @Override
        protected void onPointerUp() {
            super.onPointerUp();
            bg.brightness(0.4f);
        }
    }
}
