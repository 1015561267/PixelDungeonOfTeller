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

import com.teller.pixeldungeonofteller.Challenges;
import com.teller.pixeldungeonofteller.PDSettings;
import com.teller.pixeldungeonofteller.PixelDungeonOfTeller;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.scenes.PixelScene;
import com.teller.pixeldungeonofteller.ui.CheckBox;
import com.teller.pixeldungeonofteller.ui.RenderedTextBlock;
import com.teller.pixeldungeonofteller.ui.Window;
import com.watabou.noosa.RenderedText;

import java.util.ArrayList;

public class WndChallenges extends Window {

    private static final int WIDTH = 108;
    private static final int TTL_HEIGHT = 12;
    private static final int BTN_HEIGHT = 18;
    private static final int GAP = 1;

    private boolean editable;
    private ArrayList<CheckBox> boxes;

    public WndChallenges(int checked, boolean editable) {

        super();

        this.editable = editable;

        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
        title.hardlight(TITLE_COLOR);
        title.setPos(
                (WIDTH - title.width()) / 2,
                (TTL_HEIGHT - title.height()) / 2
        );
        PixelScene.align(title);
        add(title);

        boxes = new ArrayList<>();

        float pos = TTL_HEIGHT;
        for (int i = 0; i < Challenges.NAME_IDS.length; i++) {

            CheckBox cb = new CheckBox(Messages.get(Challenges.class, Challenges.NAME_IDS[i]));
            cb.checked((checked & Challenges.MASKS[i]) != 0);
            cb.active = editable;

            if (i > 0) {
                pos += GAP;
            }
            cb.setRect(0, pos, WIDTH, BTN_HEIGHT);
            pos = cb.bottom();

            add(cb);
            boxes.add(cb);
        }

        resize(WIDTH, (int) pos);
    }

    @Override
    public void onBackPressed() {

        if (editable) {
            int value = 0;
            for (int i = 0; i < boxes.size(); i++) {
                if (boxes.get(i).checked()) {
                    value |= Challenges.MASKS[i];
                }
            }
            PDSettings.challenges(value);
        }

        super.onBackPressed();
    }
}