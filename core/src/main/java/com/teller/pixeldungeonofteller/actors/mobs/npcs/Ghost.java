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
package com.teller.pixeldungeonofteller.actors.mobs.npcs;

import com.teller.pixeldungeonofteller.Assets;
import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.journal.Notes;
import com.teller.pixeldungeonofteller.PixelDungeonOfTeller;
import com.teller.pixeldungeonofteller.actors.Char;
import com.teller.pixeldungeonofteller.actors.Damage;
import com.teller.pixeldungeonofteller.actors.buffs.Buff;
import com.teller.pixeldungeonofteller.actors.buffs.Paralysis;
import com.teller.pixeldungeonofteller.actors.buffs.Roots;
import com.teller.pixeldungeonofteller.actors.mobs.FetidRat;
import com.teller.pixeldungeonofteller.actors.mobs.GnollTrickster;
import com.teller.pixeldungeonofteller.actors.mobs.GreatCrab;
import com.teller.pixeldungeonofteller.actors.mobs.Mob;
import com.teller.pixeldungeonofteller.effects.CellEmitter;
import com.teller.pixeldungeonofteller.effects.Speck;
import com.teller.pixeldungeonofteller.items.Generator;
import com.teller.pixeldungeonofteller.items.armor.Armor;
import com.teller.pixeldungeonofteller.items.armor.LeatherArmor;
import com.teller.pixeldungeonofteller.items.armor.MailArmor;
import com.teller.pixeldungeonofteller.items.armor.PlateArmor;
import com.teller.pixeldungeonofteller.items.armor.ScaleArmor;
import com.teller.pixeldungeonofteller.items.weapon.Weapon;
import com.teller.pixeldungeonofteller.items.weapon.missiles.MissileWeapon;
import com.teller.pixeldungeonofteller.items.weapon.weapons.MainHandWeapon.NewShortsword;
import com.teller.pixeldungeonofteller.levels.SewerLevel;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.scenes.GameScene;
import com.teller.pixeldungeonofteller.sprites.GhostSprite;
import com.teller.pixeldungeonofteller.utils.GLog;
import com.teller.pixeldungeonofteller.windows.WndQuest;
import com.teller.pixeldungeonofteller.windows.WndSadGhost;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Ghost extends NPC {

    private static final HashSet<Class<?>> IMMUNITIES = new HashSet<>();

    static {
        IMMUNITIES.add(Paralysis.class);
        IMMUNITIES.add(Roots.class);
    }

    {
        spriteClass = GhostSprite.class;

        flying = true;

        state = WANDERING;
    }

    public Ghost() {
        super();

        Sample.INSTANCE.load(Assets.SND_GHOST);
    }

    @Override
    protected boolean act() {
        if (Quest.completed())
            target = Dungeon.hero.pos;
        return super.act();
    }

    @Override
    public int defenseSkill(Char enemy) {
        return 1000;
    }

    @Override
    public float speed() {
        return Quest.completed() ? 2f : 0.5f;
    }

    @Override
    protected Char chooseEnemy() {
        return null;
    }

    @Override
    public void damage(Damage dmg, Object src) {
    }

    @Override
    public void add(Buff buff) {
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public boolean interact() {
        sprite.turnTo(pos, Dungeon.hero.pos);

        Sample.INSTANCE.play(Assets.SND_GHOST);

        if (Quest.given) {
            if (Quest.weapon != null) {
                if (Quest.processed) {
                    Game.runOnRenderThread(new Callback() {
						@Override
                        public void call() {
                            GameScene.show(new WndSadGhost(Ghost.this,Quest.type));}
                    });
                } else {
                    Game.runOnRenderThread(new Callback() {
                        @Override
                        public void call() {
                            switch (Quest.type) {
                                case 1:
                                default:
                                    GameScene.show(new WndQuest(Ghost.this, Messages.get(Ghost.this, "rat_2")));
                                    break;
                                case 2:
                                    GameScene.show(new WndQuest(Ghost.this, Messages.get(Ghost.this, "gnoll_2")));
                                    break;
                                case 3:
                                    GameScene.show(new WndQuest(Ghost.this, Messages.get(Ghost.this, "crab_2")));
                                    break;
                            }
                        }
                    });

                    int newPos = -1;
                    for (int i = 0; i < 10; i++) {
                        newPos = Dungeon.level.randomRespawnCell();
                        if (newPos != -1) {
                            break;
                        }
                    }
                    if (newPos != -1) {

                        CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
                        pos = newPos;
                        sprite.place(pos);
                        sprite.visible = Dungeon.hero.fieldOfView[pos];
                        //sprite.visible = Dungeon.visible[pos];
                    }
                }
            }
        } else {
            Mob questBoss;
            String txt_quest;

            switch (Quest.type) {
                case 1:
                default:
                    questBoss = new FetidRat();
                    txt_quest = Messages.get(this, "rat_1", Dungeon.hero.givenName());
                    break;
                case 2:
                    questBoss = new GnollTrickster();
                    txt_quest = Messages.get(this, "gnoll_1", Dungeon.hero.givenName());
                    break;
                case 3:
                    questBoss = new GreatCrab();
                    txt_quest = Messages.get(this, "crab_1", Dungeon.hero.givenName());
                    break;
            }

            questBoss.pos = Dungeon.level.randomRespawnCell();

            if (questBoss.pos != -1) {
                GameScene.add(questBoss);
                Quest.given = true;
                Notes.add( Notes.Landmark.GHOST );
                Game.runOnRenderThread(new Callback() {
                    @Override
                    public void call() {
                        GameScene.show( new WndQuest( Ghost.this, txt_quest ) );
                    }
                });
            }

        }

        return false;
    }

    @Override
    public HashSet<Class<?>> immunities() {
        return IMMUNITIES;
    }

    public static class Quest {

        private static final String NODE = "sadGhost";
        private static final String SPAWNED = "spawned";
        private static final String TYPE = "type";
        private static final String GIVEN = "given";
        private static final String PROCESSED = "processed";
        private static final String DEPTH = "depth";
        private static final String WEAPON = "weapon";
        private static final String ARMOR = "armor";
        public static Weapon weapon;
        public static Armor armor;
        private static boolean spawned;
        private static int type;
        private static boolean given;
        private static boolean processed;
        private static int depth;

        public static void reset() {
            spawned = false;

            weapon = null;
            armor = null;
        }

        public static void storeInBundle(Bundle bundle) {

            Bundle node = new Bundle();

            node.put(SPAWNED, spawned);

            if (spawned) {

                node.put(TYPE, type);

                node.put(GIVEN, given);
                node.put(DEPTH, depth);
                node.put(PROCESSED, processed);

                node.put(WEAPON, weapon);
                node.put(ARMOR, armor);
            }

            bundle.put(NODE, node);
        }

        public static void restoreFromBundle(Bundle bundle) {

            Bundle node = bundle.getBundle(NODE);

            if (!node.isNull() && (spawned = node.getBoolean(SPAWNED))) {

                type = node.getInt(TYPE);
                given = node.getBoolean(GIVEN);
                processed = node.getBoolean(PROCESSED);

                depth = node.getInt(DEPTH);

                weapon = (Weapon) node.get(WEAPON);
                armor = (Armor) node.get(ARMOR);
            } else {
                reset();
            }
        }

        public static void spawn(SewerLevel level) {
            if (!spawned && Dungeon.depth > 1 && Random.Int(5 - Dungeon.depth) == 0) {

                Ghost ghost = new Ghost();
                do {
                    ghost.pos = level.randomRespawnCell();
                } while (ghost.pos == -1);
                level.mobs.add(ghost);

                spawned = true;
                //dungeon depth determines type of quest.
                //depth2=fetid rat, 3=gnoll trickster, 4=great crab
                type = Dungeon.depth - 1;

                given = false;
                processed = false;
                depth = Dungeon.depth;

                //50%:tier2, 30%:tier3, 15%:tier4, 5%:tier5
                float itemTierRoll = Random.Float();
                int wepTier;

                if (itemTierRoll < 0.5f) {
                    wepTier = 2;
                    armor = new LeatherArmor();
                } else if (itemTierRoll < 0.8f) {
                    wepTier = 3;
                    armor = new MailArmor();
                } else if (itemTierRoll < 0.95f) {
                    wepTier = 4;
                    armor = new ScaleArmor();
                } else {
                    wepTier = 5;
                    armor = new PlateArmor();
                }

                try {
                    do {
                        weapon = (Weapon) Generator.wepTiers[wepTier - 1].classes[Random.chances(Generator.wepTiers[wepTier - 1].probs)].newInstance();
                    } while ((weapon instanceof MissileWeapon));
                } catch (Exception e) {
                    PixelDungeonOfTeller.reportException(e);
                    weapon = new NewShortsword();
                }

                //50%:+0, 30%:+1, 15%:+2, 5%:+3
                float itemLevelRoll = Random.Float();
                int itemLevel;
                if (itemLevelRoll < 0.5f) {
                    itemLevel = 0;
                } else if (itemLevelRoll < 0.8f) {
                    itemLevel = 1;
                } else if (itemLevelRoll < 0.95f) {
                    itemLevel = 2;
                } else {
                    itemLevel = 3;
                }
                weapon.upgrade(itemLevel);
                armor.upgrade(itemLevel);

                //10% to be enchanted
                if (Random.Int(10) == 0) {
                    weapon.enchant();
                    armor.inscribe();
                }

                weapon.identify();
                armor.identify();
            }
        }

        public static void process() {
            if (spawned && given && !processed && (depth == Dungeon.depth)) {
                GLog.n(Messages.get(Ghost.class, "find_me"));
                Sample.INSTANCE.play(Assets.SND_GHOST);
                processed = true;
                Generator.Category.ARTIFACT.probs[10] = 1; //flags the dried rose as spawnable.
            }
        }

        public static void complete() {
            weapon = null;
            armor = null;
            Notes.remove( Notes.Landmark.GHOST );
        }

        public static boolean completed() {
            return spawned && processed;
        }
    }
}
