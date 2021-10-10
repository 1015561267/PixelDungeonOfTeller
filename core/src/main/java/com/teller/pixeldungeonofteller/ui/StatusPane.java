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

import com.teller.pixeldungeonofteller.Assets;
import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.PDAction;
import com.teller.pixeldungeonofteller.effects.Speck;
import com.teller.pixeldungeonofteller.items.Item;
import com.teller.pixeldungeonofteller.items.weapon.Weapon;
import com.teller.pixeldungeonofteller.scenes.GameScene;
import com.teller.pixeldungeonofteller.scenes.PixelScene;
import com.teller.pixeldungeonofteller.sprites.HeroSprite;
import com.teller.pixeldungeonofteller.windows.WndGame;
import com.teller.pixeldungeonofteller.windows.WndHero;
import com.teller.pixeldungeonofteller.windows.WndJournal;
import com.watabou.input.GameAction;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.ui.Button;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.ColorMath;

public class StatusPane extends Component {

    private NinePatch bg;
    private Image avatar;
    private float warning;
    private int lastTier = 0;

    private Image rawShielding;
    private Image shieldedHP;

    private Image armor;

    private Image hp;
    private Image exp;

    private Image mp;

    private BossHealthBar bossHP;
    private int lastLvl = -1;
    private BitmapText level;
    private BitmapText depth;

    private ClockIndicator clock;

    private DangerIndicator danger;
    private BuffIndicator buffs;
    private Compass compass;
    private JournalButton btnJournal;
    private MenuButton btnMenu;
    private Toolbar.PickedUpItem pickedUp;

    public MainHandIndicator mainhand;
    public OffHandIndicator offhand;
    public SpellSwitchIndicator switchIndicator;


    @Override
    protected void createChildren() {

        bg = new NinePatch(Assets.STATUS, 0, 0, 128, 36, 85, 0, 45, 0);
        add(bg);

        add(new Button() {
            @Override
            protected void onClick() {
                Camera.main.panTo( Dungeon.hero.sprite.center(), 5f );
                GameScene.show(new WndHero());
            }
            @Override
            public GameAction keyAction() {
                return PDAction.HERO_INFO;
            }
        }.setRect( 0, 1, 30, 30 ));

        btnJournal = new JournalButton();
        add(btnJournal);

        btnMenu = new MenuButton();
        add(btnMenu);

        avatar = HeroSprite.avatar(Dungeon.hero.heroClass, lastTier);
        add(avatar);

        compass = new Compass(Dungeon.level.exit);
        add(compass);

        rawShielding = new Image(Assets.SHLD_BAR);
        rawShielding.alpha(0.5f);
        add(rawShielding);

        shieldedHP = new Image(Assets.SHLD_BAR);
        add(shieldedHP);

        hp = new Image(Assets.HP_BAR);
        add(hp);

        armor = new Image(Assets.ARMOR_BAR);
        add(armor);

        mp= new Image(Assets.MP_BAR);
        add(mp);

        exp = new Image(Assets.XP_BAR);
        add(exp);

        bossHP = new BossHealthBar();
        add(bossHP);

        level = new BitmapText(PixelScene.pixelFont);
        level.hardlight(0xFFEBA4);
        add(level);

        depth = new BitmapText(Integer.toString(Dungeon.depth), PixelScene.pixelFont);
        depth.hardlight(0xCACFC2);
        depth.measure();
        add(depth);

        danger = new DangerIndicator();
        add(danger);

        clock = new ClockIndicator(0xff4c4c);
        add(clock);

        buffs = new BuffIndicator(Dungeon.hero);
        add(buffs);

        add(pickedUp = new Toolbar.PickedUpItem());

        mainhand=new MainHandIndicator();
        mainhand.initialization(Dungeon.hero);
        add(mainhand);

        offhand=new OffHandIndicator();
        offhand.initialization(Dungeon.hero);
        add(offhand);

        switchIndicator=new SpellSwitchIndicator();
        add(switchIndicator);
    }

    @Override
    protected void layout() {

        height = 32;

        bg.size(width, bg.height);

        avatar.x = bg.x + 15 - avatar.width / 2f;
        avatar.y = bg.y + 16 - avatar.height / 2f;
        PixelScene.align(avatar);

        compass.x = avatar.x + avatar.width / 2f - compass.origin.x;
        compass.y = avatar.y + avatar.height / 2f - compass.origin.y;
        PixelScene.align(compass);

        hp.x = shieldedHP.x = rawShielding.x = 30;
        hp.y = shieldedHP.y = rawShielding.y = 3;

        armor.x = hp.x;
        armor.y = hp.y;

        mp.x=hp.x;
        mp.y=8;

        bossHP.setPos(6 + (width - bossHP.width()) / 2, 20);

        depth.x = width - 35.5f - depth.width() / 2f;
        depth.y = 8f - depth.baseLine() / 2f;
        PixelScene.align(depth);

        danger.setPos(width - danger.width(), 20);

        clock.setPos(width - clock.width(), danger.bottom() + 2);

        buffs.setPos(31, 12);

        btnJournal.setPos(width - 42, 1);

        btnMenu.setPos(width - btnMenu.width(), 1);

        mainhand.setPos(0,52);

        offhand.setPos(width-23,52);

        switchIndicator.setPos(width-23,72);
    }

    @Override
    public void update() {
        super.update();

        float Armor = Dungeon.hero.ARMOR;
        float MaxArmor = Dungeon.hero.MAXARMOR;

        if( MaxArmor == 0 )
        {armor.scale.x = 0;}
        else  {armor.scale.x = Armor/MaxArmor;}

        float health = Dungeon.hero.HP;
        float shield = Dungeon.hero.SHLD;
        float max = Dungeon.hero.HT;

        if (!Dungeon.hero.isAlive()) {
            avatar.tint(0x000000, 0.5f);
        } else if ((health / max) < 0.3f) {
            warning += Game.elapsed * 5f * (0.4f - (health / max));
            warning %= 1f;
            avatar.tint(ColorMath.interpolate(warning, 0x660000, 0xCC0000, 0x660000), 0.5f);
        } else {
            avatar.resetColor();
        }

        hp.scale.x = Math.max(0, (health - shield) / max);
        shieldedHP.scale.x = health / max;
        rawShielding.scale.x = shield / max;



        float mana = Dungeon.hero.MANA;
        float manacap = Dungeon.hero.MANACAP;

        mp.scale.x = Math.max(0,mana/manacap);

        exp.scale.x = (width / exp.width) * Dungeon.hero.exp / Dungeon.hero.maxExp();

        if (Dungeon.hero.lvl != lastLvl) {

            if (lastLvl != -1) {
                Emitter emitter = (Emitter) recycle(Emitter.class);
                emitter.revive();
                emitter.pos(27, 27);
                emitter.burst(Speck.factory(Speck.STAR), 12);
            }

            lastLvl = Dungeon.hero.lvl;
            level.text(Integer.toString(lastLvl));
            level.measure();
            level.x = 27.5f - level.width() / 2f;
            level.y = 28.0f - level.baseLine() / 2f;
            PixelScene.align(level);
        }

        int tier = Dungeon.hero.tier();
        if (tier != lastTier) {
            lastTier = tier;
            avatar.copy(HeroSprite.avatar(Dungeon.hero.heroClass, tier));
        }

        danger.update();
    }

    public void pickup(Item item, int cell) {
        pickedUp.reset( item,
                cell,
                btnJournal.journalIcon.x + btnJournal.journalIcon.width()/2f,
                btnJournal.journalIcon.y + btnJournal.journalIcon.height()/2f);
    }

    public void flash(){
        btnJournal.flashing = true;
    }

    public void updateKeys(){
        btnJournal.updateKeyDisplay();
    }

    private static class JournalButton extends Button {

        private Image bg;
        private Image journalIcon;
        private KeyDisplay keyIcon;

        private boolean flashing;

        public JournalButton() {
            super();

            width = bg.width + 13; //includes the depth display to the left
            height = bg.height + 4;
        }

        @Override
        protected void createChildren() {
            super.createChildren();

            bg = new Image( Assets.MENU, 2, 2, 13, 11 );
            add( bg );

            journalIcon = new Image( Assets.MENU, 31, 0, 11, 7);
            add( journalIcon );

            keyIcon = new KeyDisplay();
            add(keyIcon);
            updateKeyDisplay();
        }

        @Override
        protected void layout() {
            super.layout();

            bg.x = x + 13;
            bg.y = y + 2;

            journalIcon.x = bg.x + (bg.width() - journalIcon.width())/2f;
            journalIcon.y = bg.y + (bg.height() - journalIcon.height())/2f;
            PixelScene.align(journalIcon);

            keyIcon.x = bg.x + 1;
            keyIcon.y = bg.y + 1;
            keyIcon.width = bg.width - 2;
            keyIcon.height = bg.height - 2;
            PixelScene.align(keyIcon);
        }

        private float time;

        @Override
        public void update() {
            super.update();

            if (flashing){
                journalIcon.am = (float)Math.abs(Math.cos( 3 * (time += Game.elapsed) ));
                keyIcon.am = journalIcon.am;
                if (time >= 0.333f*Math.PI) {
                    time = 0;
                }
            }
        }

        public void updateKeyDisplay() {
            keyIcon.updateKeys();
            keyIcon.visible = keyIcon.keyCount() > 0;
            journalIcon.visible = !keyIcon.visible;
            if (keyIcon.keyCount() > 0) {
                bg.brightness(.8f - (Math.min(6, keyIcon.keyCount()) / 20f));
            } else {
                bg.resetColor();
            }
        }

        @Override
        protected void onPointerDown() {
            bg.brightness( 1.5f );
            Sample.INSTANCE.play( Assets.SND_CLICK );
        }

        @Override
        protected void onPointerUp() {
            if (keyIcon.keyCount() > 0) {
                bg.brightness(.8f - (Math.min(6, keyIcon.keyCount()) / 20f));
            } else {
                bg.resetColor();
            }
        }

        @Override
        protected void onClick() {
            flashing = false;
            time = 0;
            GameScene.show( new WndJournal() );
        }
    }

    private static class MenuButton extends Button {

        private Image image;

        public MenuButton() {
            super();

            width = image.width + 4;
            height = image.height + 4;
        }

        @Override
        protected void createChildren() {
            super.createChildren();

            image = new Image(Assets.MENU, 17, 2, 12, 11);
            add(image);
        }

        @Override
        protected void layout() {
            super.layout();

            image.x = x + 2;
            image.y = y + 2;
        }

        @Override
        protected void onPointerDown() {
            image.brightness( 1.5f );
            Sample.INSTANCE.play( Assets.SND_CLICK );
        }

        @Override
        protected void onPointerUp() {
            image.resetColor();
        }

        @Override
        protected void onClick() {
            GameScene.show(new WndGame());
        }
    }

    public void updateweaponindicator(Weapon weapon, Boolean IsMainhand)
    {
        if(IsMainhand)
        {
            mainhand.update(weapon);
            mainhand.update();
        }
        else
        {
            offhand.update(weapon);
            offhand.update();
        }
    }

    public void hideweaponindicator(Weapon weapon, Boolean IsMainhand)
    {
        if(IsMainhand)
        {
            mainhand.update(null);
            mainhand.update();
        }
        else
        {
            offhand.update(null);
            switchIndicator.visible=false;
            offhand.update();
        }
    }

    public void offhandupdate()
    {
        offhand.initialization(Dungeon.hero);
    }

    public void changeswitch(Boolean value)
    {
        if(switchIndicator!=null)
        switchIndicator.visible=value;
    }

    public void enableswitchindicator(Boolean value)
    {
        if(switchIndicator!=null)
            switchIndicator.enable(value);
    }
}
