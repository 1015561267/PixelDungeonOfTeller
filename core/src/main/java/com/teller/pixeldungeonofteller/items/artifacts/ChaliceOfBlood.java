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
package com.teller.pixeldungeonofteller.items.artifacts;

import com.teller.pixeldungeonofteller.Assets;
import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.actors.AbsoluteDamage;
import com.teller.pixeldungeonofteller.actors.Damage;
import com.teller.pixeldungeonofteller.actors.PhysicalDamage;
import com.teller.pixeldungeonofteller.actors.PhysicalPercentage;
import com.teller.pixeldungeonofteller.actors.hero.Hero;
import com.teller.pixeldungeonofteller.effects.particles.ShadowParticle;
import com.teller.pixeldungeonofteller.items.Item;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.plants.Earthroot;
import com.teller.pixeldungeonofteller.scenes.GameScene;
import com.teller.pixeldungeonofteller.sprites.ItemSpriteSheet;
import com.teller.pixeldungeonofteller.utils.GLog;
import com.teller.pixeldungeonofteller.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class ChaliceOfBlood extends Artifact {

    public static final String AC_PRICK = "PRICK";

    {
        image = ItemSpriteSheet.ARTIFACT_CHALICE1;

        levelCap = 10;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped(hero) && level() < levelCap && !cursed)
            actions.add(AC_PRICK);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_PRICK)) {

            int damage = 3 * (level() * level());

            if (damage > hero.HP * 0.75) {

                GameScene.show(
                        new WndOptions(Messages.get(this, "name"),
                                Messages.get(this, "prick_warn"),
                                Messages.get(this, "yes"),
                                Messages.get(this, "no")) {
                            @Override
                            protected void onSelect(int index) {
                                if (index == 0)
                                    prick(Dungeon.hero);
                            }
                        }
                );

            } else {
                prick(hero);
            }
        }
    }

    private void prick(Hero hero) {
        int damage = 3 * (level() * level());
        Earthroot.Armor armor = hero.buff(Earthroot.Armor.class);
        PhysicalDamage dmg = new PhysicalDamage(damage, new PhysicalPercentage(0, 1, 0));
        Damage rdmg = new Damage();
        if (armor != null) {
            rdmg = armor.absorb(dmg);
        }
        damage -= hero.drRoll();
        hero.sprite.operate(hero.pos);
        hero.busy();
        hero.spend(3f);
        GLog.w(Messages.get(this, "onprick"));
        if (damage <= 0) {
            damage = 1;
        } else {
            Sample.INSTANCE.play(Assets.SND_CURSED);
            hero.sprite.emitter().burst(ShadowParticle.CURSE, 4 + (damage / 10));
        }

        hero.damage(new AbsoluteDamage(damage, this, hero), this);

        if (!hero.isAlive()) {
            Dungeon.fail(getClass());
            GLog.n(Messages.get(this, "ondeath"));
        } else {
            upgrade();
        }
    }

    @Override
    public Item upgrade() {
        if (level() >= 6)
            image = ItemSpriteSheet.ARTIFACT_CHALICE3;
        else if (level() >= 2)
            image = ItemSpriteSheet.ARTIFACT_CHALICE2;
        return super.upgrade();
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (level() >= 7) image = ItemSpriteSheet.ARTIFACT_CHALICE3;
        else if (level() >= 3) image = ItemSpriteSheet.ARTIFACT_CHALICE2;
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new chaliceRegen();
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if (isEquipped(Dungeon.hero)) {
            desc += "\n\n";
            if (cursed)
                desc += Messages.get(this, "desc_cursed");
            else if (level() == 0)
                desc += Messages.get(this, "desc_1");
            else if (level() < levelCap)
                desc += Messages.get(this, "desc_2");
            else
                desc += Messages.get(this, "desc_3");
        }

        return desc;
    }

    public class chaliceRegen extends ArtifactBuff {

    }

}
