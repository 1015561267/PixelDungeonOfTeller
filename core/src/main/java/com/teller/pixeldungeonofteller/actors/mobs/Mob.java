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
package com.teller.pixeldungeonofteller.actors.mobs;

import com.teller.pixeldungeonofteller.Badges;
import com.teller.pixeldungeonofteller.Challenges;
import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.PixelDungeonOfTeller;
import com.teller.pixeldungeonofteller.Statistics;
import com.teller.pixeldungeonofteller.actors.Actor;
import com.teller.pixeldungeonofteller.actors.Char;
import com.teller.pixeldungeonofteller.actors.Damage;
import com.teller.pixeldungeonofteller.actors.buffs.Amok;
import com.teller.pixeldungeonofteller.actors.buffs.Buff;
import com.teller.pixeldungeonofteller.actors.buffs.Corruption;
import com.teller.pixeldungeonofteller.actors.buffs.Hunger;
import com.teller.pixeldungeonofteller.actors.buffs.Noise;
import com.teller.pixeldungeonofteller.actors.buffs.Sleep;
import com.teller.pixeldungeonofteller.actors.buffs.SoulMark;
import com.teller.pixeldungeonofteller.actors.buffs.Terror;
import com.teller.pixeldungeonofteller.actors.buffs.Vertigo;
import com.teller.pixeldungeonofteller.actors.hero.Hero;
import com.teller.pixeldungeonofteller.actors.hero.HeroSubClass;
import com.teller.pixeldungeonofteller.effects.Speck;
import com.teller.pixeldungeonofteller.effects.Surprise;
import com.teller.pixeldungeonofteller.effects.Wound;
import com.teller.pixeldungeonofteller.items.Generator;
import com.teller.pixeldungeonofteller.items.Item;
import com.teller.pixeldungeonofteller.items.artifacts.TimekeepersHourglass;
import com.teller.pixeldungeonofteller.items.rings.RingOfAccuracy;
import com.teller.pixeldungeonofteller.items.rings.RingOfWealth;
import com.teller.pixeldungeonofteller.levels.Level;
import com.teller.pixeldungeonofteller.levels.Level.Feeling;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.sprites.CharSprite;
import com.teller.pixeldungeonofteller.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class Mob extends Char {

    protected static final String TXT_NOTICE1 = "?!";
    protected static final String TXT_RAGE = "#$%^";
    protected static final String TXT_EXP = "%+dEXP";
    protected static final float TIME_TO_WAKE_UP = 1f;
    private static final String TXT_DIED = "You hear something died in the distance";
    private static final String STATE = "state";
    private static final String SEEN = "seen";
    private static final String TARGET = "target";

    public AiState SLEEPING = new Sleeping();
    //public AiState LOW_ALERTING = new LowAlerting();
    //public AiState MID_ALERTING = new MidAlerting();
    //public AiState HIGH_ALERTING = new HighAlerting();
    public AiState SEARCHING =new Searching();
    public AiState HUNTING = new Hunting();
    public AiState WANDERING = new Wandering();
    public AiState FLEEING = new Fleeing();
    public AiState PASSIVE = new Passive();
    public AiState state = SLEEPING;
    public Class<? extends CharSprite> spriteClass;
    public boolean hostile = true;
    public boolean ally = false;

    public ArrayList<Integer>  search_pos;

    public int alert = 0;
    protected int target = -1;
    protected int defenseSkill = 0;
    protected int EXP = 1;
    protected int maxLvl = Hero.MAX_LEVEL;
    protected Char enemy;
    protected boolean enemySeen;
    protected boolean alerted = false;
    protected Object loot = null;
    protected float lootChance = 0;

    {
        name = Messages.get(this, "name");
        actPriority = 2; //hero gets priority over mobs.
    }

    @Override
    public void storeInBundle(Bundle bundle) {

        super.storeInBundle(bundle);

        if (state == SLEEPING) {
            bundle.put(STATE, Sleeping.TAG);
        }//else if (state == LOW_ALERTING) {
        //   bundle.put(STATE, LowAlerting.TAG);
        //}else if (state == MID_ALERTING) {
        //    bundle.put(STATE, MidAlerting.TAG);
        ///}else if (state == HIGH_ALERTING) {
        //    bundle.put(STATE, HighAlerting.TAG);
        //}
        else if (state == SEARCHING) {
            bundle.put(STATE, Searching.TAG);
        } else if (state == WANDERING) {
            bundle.put(STATE, Wandering.TAG);
        } else if (state == HUNTING) {
            bundle.put(STATE, Hunting.TAG);
        } else if (state == FLEEING) {
            bundle.put(STATE, Fleeing.TAG);
        } else if (state == PASSIVE) {
            bundle.put(STATE, Passive.TAG);
        }
        bundle.put(SEEN, enemySeen);
        bundle.put(TARGET, target);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {

        super.restoreFromBundle(bundle);

        String state = bundle.getString(STATE);
        if (state.equals(Sleeping.TAG)) {
            this.state = SLEEPING;
        }//else if (state.equals(LowAlerting.TAG)) {
        //    this.state = LOW_ALERTING;
        //}else if (state.equals(MidAlerting.TAG)) {
        //    this.state = MID_ALERTING;
        //}else if (state.equals(HighAlerting.TAG)) {
        //    this.state = HIGH_ALERTING;
        //}
        else if (state.equals(Searching.TAG)) {
            this.state = SEARCHING;
        } else if (state.equals(Wandering.TAG)) {
            this.state = WANDERING;
        } else if (state.equals(Hunting.TAG)) {
            this.state = HUNTING;
        } else if (state.equals(Fleeing.TAG)) {
            this.state = FLEEING;
        } else if (state.equals(Passive.TAG)) {
            this.state = PASSIVE;
        }

        enemySeen = bundle.getBoolean(SEEN);

        target = bundle.getInt(TARGET);
    }

    public CharSprite sprite() {
        CharSprite sprite = null;
        try {
            sprite = spriteClass.newInstance();
        } catch (Exception e) {
            PixelDungeonOfTeller.reportException(e);
        }
        return sprite;
    }

    @Override
    protected boolean act() {

        super.act();

        boolean justAlerted = alerted;
        alerted = false;

        sprite.hideAlert();
        if (paralysed > 0) {
            enemySeen = false;
            spend(TICK);
            return true;
        }
        enemy = chooseEnemy();
        boolean enemyInFOV = enemy != null && enemy.isAlive() && fieldOfView[enemy.pos] && enemy.invisible <= 0;

        int distance=distance(Dungeon.hero);
        boolean heroinview= fieldOfView[Dungeon.hero.pos];
        boolean invisible=Dungeon.hero.invisible>0;
        //int noise=Dungeon.hero.NOISE();
        AiState PreviousState=state;

        if(invisible)
            distance+=Random.Int(5);
        if(distance>8)
            alert-=distance;
            //else if(noise>75)
            //{
            //alert+=heroinview?Math.floor(2*noise/distance):Math.floor(2*noise/distance)*2;
            // }
        else if(heroinview)
        {
            //alert+=Math.floor(noise/distance);
        }
        else
        {
            //alert+=Math.floor(noise/2*distance);
        }
        if(alert>100) alert=100;
        else if(alert<0) alert=0;

        if(alert>=25)
        {
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if(mob.alert<alert) {
                    if (Dungeon.level.distance(pos, mob.pos)<5) {
                        mob.alert += alert/Math.floor(distance*(fieldOfView[pos]? 1:2));
                    }
                }
            }
        }

        return state.act(enemyInFOV, justAlerted);
    }

    protected Char chooseEnemy() {
        Terror terror = buff(Terror.class);
        if (terror != null) {
            Char source = (Char) Actor.findById(terror.object);
            if (source != null) {
                return source;
            }
        }
        //find a new enemy if..
        boolean newEnemy = false;
        //we have no enemy, or the current one is dead
        if (enemy == null || !enemy.isAlive() || state == WANDERING)
            newEnemy = true;
            //We are corrupted, and current enemy is either the hero or another corrupted character.
        else if (buff(Corruption.class) != null && (enemy == Dungeon.hero || enemy.buff(Corruption.class) != null))
            newEnemy = true;
            //We are amoked and current enemy is the hero
        else if (buff(Amok.class) != null && enemy == Dungeon.hero)
            newEnemy = true;

        if (newEnemy) {

            HashSet<Char> enemies = new HashSet<>();

            //if the mob is corrupted...
            if (buff(Corruption.class) != null) {

                //look for enemy mobs to attack, which are also not corrupted
                for (Mob mob : Dungeon.level.mobs)
                    if (mob != this && fieldOfView[mob.pos] && mob.hostile && mob.buff(Corruption.class) == null)
                        enemies.add(mob);
                if (enemies.size() > 0) return Random.element(enemies);
                //otherwise go for nothing
                return null;
                //if the mob is amoked...
            } else if (buff(Amok.class) != null) {
                //try to find an enemy mob to attack first.
                for (Mob mob : Dungeon.level.mobs)
                    if (mob != this && fieldOfView[mob.pos] && mob.hostile)
                        enemies.add(mob);
                if (enemies.size() > 0) return Random.element(enemies);
                //try to find ally mobs to attack second.
                for (Mob mob : Dungeon.level.mobs)
                    if (mob != this && fieldOfView[mob.pos] && mob.ally)
                        enemies.add(mob);
                if (enemies.size() > 0) return Random.element(enemies);
                    //if there is nothing, go for the hero
                else return Dungeon.hero;
            } else {
                //try to find ally mobs to attack.
                for (Mob mob : Dungeon.level.mobs)
                    if (mob != this && fieldOfView[mob.pos] && mob.ally)
                        enemies.add(mob);
                //and add the hero to the list of targets.
                enemies.add(Dungeon.hero);
                //target one at random.
                return Random.element(enemies);
            }
        } else
            return enemy;
    }

    protected boolean moveSprite(int from, int to) {
        if (sprite.isVisible() && (Dungeon.visible[from] || Dungeon.visible[to])) {
            sprite.move(from, to);
            return true;
        } else {
            sprite.place(to);
            return true;
        }
    }
    @Override
    public void add(Buff buff) {
        super.add(buff);
        if (buff instanceof Amok) {
            if (sprite != null) {
                sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "rage"));
            }
            state = HUNTING;
        } else if (buff instanceof Terror) {
            state = FLEEING;
        } else if (buff instanceof Sleep) {
            state = SLEEPING;
            this.sprite().showSleep();
            postpone(Sleep.SWS);
        }
    }

    @Override
    public void remove(Buff buff) {
        super.remove(buff);
        if (buff instanceof Terror) {
            sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "rage"));
            state = HUNTING;
        }
    }

    protected boolean canAttack(Char enemy) {
        return Dungeon.level.adjacent(pos, enemy.pos);
    }

    protected boolean getCloser(int target) {

        if (rooted || target == pos) {
            return false;
        }

        int step = -1;

        if (Dungeon.level.adjacent(pos, target)) {

            path = null;

            if (Actor.findChar(target) == null && Dungeon.level.passable[target]) {
                step = target;
            }

        } else {

            boolean newPath = false;
            if (path == null || path.isEmpty() || !Dungeon.level.adjacent(pos, path.getFirst()))
                newPath = true;
            else if (path.getLast() != target) {
                //if the new target is adjacent to the end of the path, adjust for that
                //rather than scrapping the whole path. Unless the path is too long,
                //in which case re-checking will likely result in a better path
                if (Dungeon.level.adjacent(target, path.getLast()) && path.size() < Dungeon.level.distance(pos, target)) {
                    int last = path.removeLast();

                    if (path.isEmpty()) {
                        //shorten for a closer one
                        if (Dungeon.level.adjacent(target, pos)) {
                            path.add(target);
                            //extend the path for a further target
                        } else {
                            path.add(last);
                            path.add(target);
                        }

                    } else if (!path.isEmpty()) {
                        //if the new target is simply 1 earlier in the path shorten the path
                        if (path.getLast() == target) {
                            //if the new target is closer/same, need to modify end of path
                        } else if (Dungeon.level.adjacent(target, path.getLast())) {
                            path.add(target);
                            //if the new target is further away, need to extend the path
                        } else {
                            path.add(last);
                            path.add(target);
                        }
                    }
                } else {
                    newPath = true;
                }
            }

            if (!newPath) {
                //looks ahead for path validity, up to length-1 or 4, but always at least 1.
                int lookAhead = (int) GameMath.gate(1, path.size() - 1, 4);
                for (int i = 0; i < lookAhead; i++) {
                    int cell = path.get(i);
                    if (!Dungeon.level.passable[cell] || (Dungeon.visible[cell] && Actor.findChar(cell) != null)) {
                        newPath = true;
                        break;
                    }
                }
            }

            if (newPath) {
                path = Dungeon.findPath(this, pos, target,
                        Dungeon.level.passable,
                        fieldOfView);
            }

            if (path == null)
                return false;

            step = path.removeFirst();
        }
        if (step != -1) {

            if (Dungeon.level.adjacent(step, pos) && buff(Vertigo.class) != null) {
                sprite.interruptMotion();
                int newPos = pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
                if (!(Dungeon.level.passable[newPos] || Dungeon.level.avoid[newPos]) || Actor.findChar(newPos) != null) {
                    sprite.move(pos, newPos);
                    return false;
                }
                else {
                    step = newPos;
                }
            }
            step = slipto(pos,step,flying);
            move(step);
            return true;
        } else {
            return false;
        }
    }

    protected boolean getFurther(int target) {
        int step = Dungeon.flee(this, pos, target,
                Dungeon.level.passable,
                fieldOfView);
        if (step != -1) {
            if (Dungeon.level.adjacent(step, pos) && buff(Vertigo.class) != null) {
                sprite.interruptMotion();
                int newPos = pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
                if (!(Dungeon.level.passable[newPos] || Dungeon.level.avoid[newPos]) || Actor.findChar(newPos) != null) {
                    sprite.move(pos, newPos);
                    return false;
                }
                else {
                    step = newPos;
                }
            }
            step = slipto(pos,step,flying);
            move(step);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void updateSpriteState() {
        super.updateSpriteState();
        if (Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class) != null)
            sprite.add(CharSprite.State.PARALYSED);
    }

    @Override
    public void move(int step) {
        super.move(step);

        if (!flying) {
            Dungeon.level.mobPress(this);
        }
    }

    protected float attackDelay() {
        return 1f;
    }

    protected boolean doAttack(Char enemy) {

        boolean visible = Dungeon.visible[pos];

        if (visible) {
            sprite.attack(enemy.pos);
        } else {
            attack(enemy);
        }

        spend(attackDelay());

        return !visible;
    }

    @Override
    public void onAttackComplete() {
        attack(enemy);
        super.onAttackComplete();
    }

    @Override
    public int defenseSkill(Char enemy) {
        boolean seen = enemySeen || (enemy == Dungeon.hero && !Dungeon.hero.canSurpriseAttack());
        if (seen && paralysed == 0) {
            int defenseSkill = this.defenseSkill;
            int penalty = RingOfAccuracy.getBonus(enemy, RingOfAccuracy.Accuracy.class);
            if (penalty != 0 && enemy == Dungeon.hero)
                defenseSkill *= Math.pow(0.75, penalty);
            return defenseSkill;
        } else {
            return 0;
        }
    }

    @Override
    public Damage defenseProc(Char enemy, Damage damage) {
        if (!enemySeen && enemy == Dungeon.hero && Dungeon.hero.canSurpriseAttack()) {
            if (((Hero) enemy).subClass == HeroSubClass.ASSASSIN) {
                damage.multiplie(1.25);
                Wound.hit(this);
            } else {
                Surprise.hit(this);
            }
        }

        //become aggro'd by a corrupted enemy
        if (enemy.buff(Corruption.class) != null) {
            aggro(enemy);
            target = enemy.pos;
            if (state == SLEEPING || state == WANDERING)
                state = HUNTING;
        }

        if (buff(SoulMark.class) != null) {
            int restoration = Math.min(damage.effictivehpdamage + damage.effictiveslddamage, HP);
            Dungeon.hero.buff(Hunger.class).satisfy(restoration * 0.5f);
            Dungeon.hero.HP = (int) Math.ceil(Math.min(Dungeon.hero.HT, Dungeon.hero.HP + (restoration * 0.25f)));
            Dungeon.hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
        }

        return damage;
    }

    public boolean surprisedBy(Char enemy) {
        return !enemySeen && enemy == Dungeon.hero;
    }

    public void aggro(Char ch) {
        enemy = ch;
        if (state != PASSIVE) {
            state = HUNTING;
        }
    }

    @Override
    public void damage(Damage dmg, Object src) {

        Terror.recover(this);

        if (state == SLEEPING) {
            state = WANDERING;
        }
        alerted = true;

        super.damage(dmg, src);
    }

    @Override
    public void destroy() {

        super.destroy();

        Dungeon.level.mobs.remove(this);

        if (Dungeon.hero.isAlive()) {
            if (hostile) {
                Statistics.enemiesSlain++;
                Badges.validateMonstersSlain();
                Statistics.qualifiedForNoKilling = false;
                if (Dungeon.level.feeling == Feeling.DARK) {
                    Statistics.nightHunt++;
                } else {
                    Statistics.nightHunt = 0;
                }
                Badges.validateNightHunter();
            }
            int exp = exp();
            if (exp > 0) {
                Dungeon.hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "exp", exp));
                Dungeon.hero.earnExp(exp);
            }
        }
    }

    public int exp() {
        return Dungeon.hero.lvl <= maxLvl ? EXP : 0;
    }

    @Override
    public void die(Object cause) {

        super.die(cause);

        float lootChance = this.lootChance;
        int bonus = RingOfWealth.getBonus(Dungeon.hero, RingOfWealth.Wealth.class);
        lootChance *= Math.pow(1.15, bonus);

        if (Random.Float() < lootChance && Dungeon.hero.lvl <= maxLvl + 2) {
            Item loot = createLoot();
            if (loot != null)
                Dungeon.level.drop(loot, pos).sprite.drop();
        }

        if (Dungeon.hero.isAlive() && !Dungeon.visible[pos]) {
            GLog.i(Messages.get(this, "died"));
        }
    }

    @SuppressWarnings("unchecked")
    protected Item createLoot() {
        Item item;
        if (loot instanceof Generator.Category) {

            item = Generator.random((Generator.Category) loot);

        } else if (loot instanceof Class<?>) {

            item = Generator.random((Class<? extends Item>) loot);

        } else {

            item = (Item) loot;

        }
        return item;
    }

    public boolean reset() {
        return false;
    }

    public void beckon(int cell) {

        notice();

        if (state != HUNTING) {
            state = WANDERING;
        }
        target = cell;
    }

    public String description() {
        //String msg=Messages.get(this,"desc");
        return Messages.get(this, "desc") + "\nhp:" + HP + "/" + HT + "\nArmor:" + ARMOR + "\nShield:" + SHLD + "/" + MAXSHLD + "\nThreshold:" + SlashThreshold;
        //return Messages.get(this, "desc")+"\nhp:"+HP+"/"+HT+"\nArmor:"+ARMOR/10+"\nShield:"+SHLD+"/"+MAXSHLD;
    }

    public void notice() {
        sprite.showAlert();
    }

    public void yell(String str) {
        GLog.n("%s: \"%s\" ", name, str);
    }

    //returns true when a mob sees the hero, and is currently targeting them.
    public boolean focusingHero() {
        return enemySeen && (target == Dungeon.hero.pos);
    }

    public interface AiState {
        boolean act(boolean enemyInFOV, boolean justAlerted);
        String status();
    }

    protected class Sleeping implements AiState {
        public static final String TAG = "SLEEPING";
        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {


            /*
            if(alert<=25)
            {
                enemySeen = false;
                spend(TICK);
            }
            else
            {
                if(alert<=50)
                {
                    state=LOW_ALERTING;
                    target = Dungeon.level.randomDestination();
                }
                else if(alert<=75)
                {
                    state=MID_ALERTING;
                }
                else if(alert<=90)
                {
                    state=HIGH_ALERTING;
                    target = get_alley_pos(pos);
                }
                else {
                    enemySeen = true;
                    notice();
                    state = HUNTING;
                    target = enemy.pos;
                }
                spend(TIME_TO_WAKE_UP);
            }
            return true;*/
            //if (enemyInFOV && Random.Int( distance( enemy ) + enemy.stealth() + (enemy.flying ? 2 : 0) ) == 0)
            if(noticeEnemy(enemyInFOV,enemy))
            {
                enemySeen = true;
                notice();
                state = HUNTING;
                target = enemy.pos;
                if (Dungeon.isChallenged( Challenges.SWARM_INTELLIGENCE )) {
                    for (Mob mob : Dungeon.level.mobs) {
                        if (Dungeon.level.distance(pos, mob.pos) <= 8 && mob.state != mob.HUNTING) {
                            mob.beckon( target );
                        }
                    }
                }

                spend( TIME_TO_WAKE_UP );

            } else {

                enemySeen = false;

                spend( TICK );

            }
            return true;
        }

        @Override
        public String status() {
            return Messages.get(this, "status", name);
        }
    }

    protected boolean noticeEnemy(boolean enemyInFOV,Char enemy)
    {
        if(enemy instanceof Hero)
        {
            if(Dungeon.hero.buff(Noise.class).getNoise()==0)
                return false;
            return (enemyInFOV && Random.Float()<((0.4 + 0.6*Dungeon.hero.buff(Noise.class).getNoise()/40)* Dungeon.hero.buff(Noise.class).stealthRevise() ));
        }
        else return (enemyInFOV && Random.Int( distance( enemy ) + enemy.stealth() + (enemy.flying ? 2 : 0) ) == 0);
    }


    /*protected class LowAlerting implements AiState {
        public static final String TAG = "LOWALERTING";
        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if(alert<=50)
            {
                enemySeen = false;
                int oldPos = pos;
                if (target != -1 && getCloser(target)) {
                    spend(1 / speed());
                    return moveSprite(oldPos, pos);
                } else {
                    target = Dungeon.level.randomDestination();
                    spend(TICK);
                }
            }
            else if(alert<=75)
            {
                state=MID_ALERTING;
                spend(TICK);
            }
            else if(alert<=90)
            {
                state=HIGH_ALERTING;
                target= get_alley_pos(pos);
                spend(TICK);
            }
            else
            {
                enemySeen = true;
                notice();
                state = HUNTING;
                target = enemy.pos;
                spend(TICK);
            }
            return true;
        }
        @Override
        public String status() {
            return Messages.get(this, "status", name);
        }
    }*/


    /*protected class MidAlerting implements AiState {
        public static final String TAG = "MIDALERTING";
        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if(alert<=25)
            {
                state=LOW_ALERTING;
                target = Dungeon.level.randomDestination();
                spend(TICK);
            }
            else if(alert<=75)
            {
                spend(TICK);
            }
            else if(alert<=90)
            {
                state=HIGH_ALERTING;
                target=get_alley_pos(pos);
                spend(TICK);
            }
            else
            {
                enemySeen = true;
                notice();
                state = HUNTING;
                target = enemy.pos;
            }
            return true;
        }
        @Override
        public String status() {
            return Messages.get(this, "status", name);
        }
    }*/

  /*  protected class HighAlerting implements AiState {
        public static final String TAG = "HIGHALERTING";
        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if(alert>90)
            {
                enemySeen = true;
                notice();
                state = HUNTING;
                target = enemy.pos;
            }
            else if(alert>=75)
            {
            }
            else if(alert>=50)
            {
            }
            else
            {
            }
            return true;
        }
        @Override
        public String status() {
            return Messages.get(this, "status", name);
        }
    }*/

    protected int get_alley_pos(int pos)
    {
        int target_pos=pos;
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if(mob.buffs(Corruption.class)==null)
            {
                if(Random.Int(8)>distance(mob));
                {
                    target_pos=mob.pos;
                    for (int i : PathFinder.NEIGHBOURS8) {
                        if (Dungeon.level.passable[target_pos + i]) {
                            target_pos=target_pos+i;
                            break;
                        }
                    }
                }
            }
        }
        return target_pos;
    }

    protected class Searching implements AiState {
        public static final String TAG = "SEARCHING";
        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            return true;
        }
        @Override
        public String status() {
            return Messages.get(this, "status", name);
        }
    }

    protected class Wandering implements AiState {
        public static final String TAG = "WANDERING";
        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (enemyInFOV && (noticeEnemy(enemyInFOV,enemy))) {
                enemySeen = true;
                notice();
                state = HUNTING;
                target = enemy.pos;
            } else {
                enemySeen = false;
                int oldPos = pos;
                if (target != -1 && getCloser(target)) {
                    spend(1 / speed());
                    return moveSprite(oldPos, pos);
                } else {
                    target = Dungeon.level.randomDestination();
                    spend(TICK);
                }
            }
            return true;
        }
        @Override
        public String status() {
            return Messages.get(this, "status", name);
        }
    }

    protected class Hunting implements AiState {

        public static final String TAG = "HUNTING";
        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            enemySeen = enemyInFOV;
            if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {
                return doAttack( enemy );
            } else {
                if (enemyInFOV) {
                    target = enemy.pos;
                } else if (enemy == null) {
                    state = WANDERING;
                    target = Dungeon.level.randomDestination();
                    return true;
                }
                int oldPos = pos;
                if (target != -1 && getCloser( target )) {
                    spend( 1 / speed() );
                    return moveSprite( oldPos,  pos );
                } else {
                    spend( TICK );
                    /*if (!enemyInFOV) {
                        state = SEARCHING;
                        search_pos = new ArrayList<Integer>();
                        for (int i : PathFinder.NEIGHBOURS8)
                        {
                            if(Level.passable[pos + i])
                            {
                                search_pos.add(pos+i);
                            }
                        }
                        target=search_pos.isEmpty() ? pos : Random.element(search_pos);
                    }*/
                    if (!enemyInFOV) {
                        state = WANDERING;
                        target = Dungeon.level.randomDestination();
                    }
                    return true;
                }
            }
        }
        @Override
        public String status() {
            return Messages.get(this, "status", name);
        }
    }

    protected class Fleeing implements AiState {
        public static final String TAG = "FLEEING";
        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            enemySeen = enemyInFOV;
            //loses target when 0-dist rolls a 6 or greater.
            if (enemy == null || !enemyInFOV && 1 + Random.Int(Dungeon.level.distance(pos, target)) >= 6) {
                target = -1;
            } else {
                target = enemy.pos;
            }

            int oldPos = pos;

            if (target != -1 && getFurther(target)) {

                spend(1 / speed());
                return moveSprite(oldPos, pos);

            } else {

                spend(TICK);
                nowhereToRun();

                return true;
            }
        }

        protected void nowhereToRun() {
        }

        @Override
        public String status() {
            return Messages.get(this, "status", name);
        }
    }

    protected class Passive implements AiState {

        public static final String TAG = "PASSIVE";

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            enemySeen = false;
            spend(TICK);
            return true;
        }

        @Override
        public String status() {
            return Messages.get(this, "status", name);
        }
    }
}