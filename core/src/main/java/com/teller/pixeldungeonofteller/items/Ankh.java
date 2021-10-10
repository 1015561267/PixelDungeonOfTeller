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
package com.teller.pixeldungeonofteller.items;

import com.teller.pixeldungeonofteller.Assets;
import com.teller.pixeldungeonofteller.actors.hero.Hero;
import com.teller.pixeldungeonofteller.effects.CellEmitter;
import com.teller.pixeldungeonofteller.effects.Speck;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.sprites.ItemSprite.Glowing;
import com.teller.pixeldungeonofteller.sprites.ItemSpriteSheet;
import com.teller.pixeldungeonofteller.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class Ankh extends Item {

    public static final String AC_BLESS = "BLESS";
    private static final Glowing WHITE = new Glowing(0xFFFFCC);
    private static final String BLESSED = "blessed";
    private Boolean blessed = false;

    {
        image = ItemSpriteSheet.ANKH;

        //You tell the ankh no, don't revive me, and then it comes back to revive you again in another run.
        //I'm not sure if that's enthusiasm or passive-aggression.
        bones = true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        DewVial vial = hero.belongings.getItem(DewVial.class);
        if (vial != null && vial.isFull() && !blessed)
            actions.add(AC_BLESS);
        return actions;
    }

    @Override
    public void execute(final Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_BLESS)) {

            DewVial vial = hero.belongings.getItem(DewVial.class);
            if (vial != null) {
                blessed = true;
                vial.empty();
                GLog.p(Messages.get(this, "bless"));
                hero.spend(1f);
                hero.busy();


                Sample.INSTANCE.play(Assets.SND_DRINK);
                CellEmitter.get(hero.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
                hero.sprite.operate(hero.pos);
            }
        }
    }

    @Override
    public String desc() {
        if (blessed)
            return Messages.get(this, "desc_blessed");
        else
            return super.desc();
    }

    public Boolean isBlessed() {
        return blessed;
    }

    @Override
    public Glowing glowing() {
        return isBlessed() ? WHITE : null;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(BLESSED, blessed);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        blessed = bundle.getBoolean(BLESSED);
    }

    @Override
    public int price() {
        return 50 * quantity;
    }
}
