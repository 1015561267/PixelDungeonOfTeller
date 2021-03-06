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
package com.teller.pixeldungeonofteller.ui;

import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.scenes.PixelScene;
import com.watabou.noosa.Image;

public class ActionIndicator extends Tag {

    public static Action action;
    public static ActionIndicator instance;
    Image icon;

    public ActionIndicator() {
        super(0xFFFF4C);

        instance = this;

        setSize(24, 24);
        visible = false;
    }

    public static void setAction(Action action) {
        ActionIndicator.action = action;
        updateIcon();
    }

    public static void clearAction(Action action) {
        if (ActionIndicator.action == action)
            ActionIndicator.action = null;
    }

    public static void updateIcon() {
        if (instance != null) {
            if (instance.icon != null) {
                instance.icon.killAndErase();
                instance.icon = null;
            }
            if (action != null) {
                instance.icon = action.getIcon();
                instance.layout();
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        instance = null;
        action = null;
    }

    @Override
    protected void layout() {
        super.layout();

        if (icon != null) {
            icon.x = x + (width - icon.width()) / 2;
            icon.y = y + (height - icon.height()) / 2;
            PixelScene.align(icon);
            if (!members.contains(icon))
                add(icon);
        }
    }

    @Override
    public void update() {
        super.update();

        if (!Dungeon.hero.ready) {
            if (icon != null) icon.alpha(0.5f);
        } else {
            if (icon != null) icon.alpha(1f);
        }

        if (!visible && action != null) {
            visible = true;
            updateIcon();
            flash();
        } else {
            visible = action != null;
        }
    }

    @Override
    protected void onClick() {
        if (action != null && Dungeon.hero.ready)
            action.doAction();
    }

    public interface Action {

        Image getIcon();

        void doAction();

    }

}
