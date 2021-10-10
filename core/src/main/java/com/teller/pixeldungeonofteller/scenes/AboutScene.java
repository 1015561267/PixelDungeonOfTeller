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

import com.teller.pixeldungeonofteller.PDSettings;
import com.teller.pixeldungeonofteller.PixelDungeonOfTeller;
import com.teller.pixeldungeonofteller.effects.Flare;
import com.teller.pixeldungeonofteller.ui.Archs;
import com.teller.pixeldungeonofteller.ui.ExitButton;
import com.teller.pixeldungeonofteller.ui.Icons;
import com.teller.pixeldungeonofteller.ui.RenderedTextBlock;
import com.teller.pixeldungeonofteller.ui.Window;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.RenderedText;

public class AboutScene extends PixelScene {

    private static final String TTL_DOT = "Dungeon Of Teller";

    private static final String TXT_TELLER = "Design , Code , &Graphics: Teller";

    private static final String TXT_PICTURE= "Picture and Icons : Passerby";

    private static final String TXT_DATA   = "Statistics and Balance : Trident";

    private static final String TTL_SHPX = "Shattered Pixel Dungeon";

    private static final String TTL_WATA = "Pixel Dungeon";


    @Override
    public void create() {
        super.create();
        final float colWidth = Camera.main.width / (landscape() ? 2 : 1);
        final float colTop = (Camera.main.height / 2) - (landscape() ? 30 : 90);
        final float wataOffset = landscape() ? colWidth : 0;

        float offline = colTop;

        RenderedTextBlock dot = renderTextBlock(TTL_DOT,16);
        dot.hardlight(0x706220);
        add(dot);
        dot.setPos((colWidth - dot.width())/2 , colTop);
        align(dot);

        offline = colTop+dot.height();

        Image teller = Icons.TELLER.get();
        teller.x = (colWidth - teller.width()) / 2;
        teller.y = offline;

        align(teller);
        add(teller);
        new Flare(14, 96).color(0x706220, true).show(teller, 0).angularSpeed = +20;

        RenderedTextBlock Tellertitle = renderTextBlock(TXT_TELLER,8);
        Tellertitle.hardlight(Window.TITLE_COLOR);
        add(Tellertitle);
        Tellertitle.setPos((colWidth - Tellertitle.width())/2 , teller.y + teller.height()+2);
        align(Tellertitle);

        offline = teller.y + teller.height() + Tellertitle.height() + 2;

        Image passerby = Icons.PASSERBY.get();
        passerby.x = (colWidth - passerby.width()) / 2;
        passerby.y = offline;
        align(passerby);
        add(passerby);
        new Flare(7, 96).color(0x6C34A8, true).show(passerby, 0).angularSpeed = +15;


        RenderedTextBlock Passerbytitle = renderTextBlock(TXT_PICTURE,8);
        Passerbytitle.hardlight(0x6C34A8);
        add( Passerbytitle);
        Passerbytitle.setPos((colWidth -  Passerbytitle.width())/2 , passerby.y + passerby.height()+1);
        align( Passerbytitle);

        offline = passerby.y + passerby.height() + Passerbytitle.height() + 2;

        Image trident =Icons.TRIDENT.get();
        trident.x = (colWidth - trident.width()) / 2;
        trident.y = offline;
        align(trident);
        add(trident);
        new Flare(14, 64).color(0x0D047B, true).show(trident, 0).angularSpeed = +15;

        RenderedTextBlock Tridenttitle = renderTextBlock(TXT_DATA,8);
        Tridenttitle.hardlight(0x0C90D0);
        add( Tridenttitle);
        Tridenttitle.setPos((colWidth -  Tridenttitle.width())/2 ,trident.y + trident.height()+2 );
        align(Tridenttitle);

        Image shpx = Icons.SHPX.get();
        if (landscape()) {
            shpx.y = colTop;
            shpx.x = teller.x + colWidth;
        } else {
            shpx.x = (colWidth - shpx.width()) / 2;
            shpx.y = trident.y + trident.height()+ 2 + Tridenttitle.height() + 20;
        }
        align(shpx);
        add(shpx);

        new Flare(7, 64).color(0x225511, true).show(shpx, 0).angularSpeed = +10;

        RenderedTextBlock shpxtitle = renderTextBlock(TTL_SHPX, 8);
        shpxtitle.hardlight(Window.SHPX_COLOR);
        add(shpxtitle);
        shpxtitle.setPos((colWidth - shpxtitle.width() + (landscape() ? colWidth : 0)) ,shpx.y + shpx.height + 2);
        align(shpxtitle);

        // pixel dungeon
        Image wata = Icons.WATA.get();
        wata.x = wataOffset + (colWidth - wata.width()) / 2;
        wata.y = shpx.y + shpx.height + 2 + shpxtitle.height()+20;
        align(wata);
        add(wata);

        new Flare(7, 64).color(0x112233, true).show(wata, 0).angularSpeed = +20;

        RenderedTextBlock wataTitle = renderTextBlock(TTL_WATA, 8);
        wataTitle.hardlight(Window.TITLE_COLOR);
        add(wataTitle);

        wataTitle.setPos(wataOffset + (colWidth - wataTitle.width()) / 2 ,wata.y + wata.height + 2);

        align(wataTitle);

        Archs archs = new Archs();
        archs.setSize(Camera.main.width, Camera.main.height);
        addToBack(archs);

        ExitButton btnExit = new ExitButton();
        btnExit.setPos(Camera.main.width - btnExit.width(), 0);
        add(btnExit);
        fadeIn();
    }

    @Override
    protected void onBackPressed() {
        PixelDungeonOfTeller.switchNoFade(TitleScene.class);
    }
}
