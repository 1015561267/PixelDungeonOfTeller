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

import com.teller.pixeldungeonofteller.Assets;
import com.teller.pixeldungeonofteller.Badges;
import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.Rankings;
import com.teller.pixeldungeonofteller.Statistics;
import com.teller.pixeldungeonofteller.actors.hero.Belongings;
import com.teller.pixeldungeonofteller.items.Item;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.scenes.PixelScene;
import com.teller.pixeldungeonofteller.sprites.HeroSprite;
import com.teller.pixeldungeonofteller.ui.BadgesList;
import com.teller.pixeldungeonofteller.ui.Icons;
import com.teller.pixeldungeonofteller.ui.ItemSlot;
import com.teller.pixeldungeonofteller.ui.RedButton;
import com.teller.pixeldungeonofteller.ui.RenderedTextBlock;
import com.teller.pixeldungeonofteller.ui.ScrollPane;
import com.teller.pixeldungeonofteller.ui.Window;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;

import java.util.Locale;

public class WndRanking extends WndTabbed {

    private static final int WIDTH = 90;
    private static final int HEIGHT = 180;

    private Thread thread;
    private String error = null;

    private Image busy;

    public WndRanking(final Rankings.Record rec) {

        super();
        resize(WIDTH, HEIGHT);

        thread = new Thread() {
            @Override
            public void run() {
                try {
                    Badges.loadGlobal();
                    Rankings.INSTANCE.loadGameData(rec);
                } catch (Exception e) {
                    error = Messages.get(WndRanking.class, "error");
                }
            }
        };
        thread.start();

        busy = Icons.BUSY.get();
        busy.origin.set(busy.width / 2, busy.height / 2);
        busy.angularSpeed = 720;
        busy.x = (WIDTH - busy.width) / 2;
        busy.y = (HEIGHT - busy.height) / 2;
        add(busy);
    }

    @Override
    public void update() {
        super.update();

        if (thread != null && !thread.isAlive()) {
            thread = null;
            if (error == null) {
                remove(busy);
                createControls();
            } else {
                hide();
                Game.scene().add(new WndError(error));
            }
        }
    }

    private void createControls() {

        String[] labels =
                {Messages.get(this, "stats"), Messages.get(this, "items"), Messages.get(this, "badges")};
        Group[] pages =
                {new StatsTab(), new ItemsTab(), new BadgesTab()};

        for (int i = 0; i < pages.length; i++) {

            add(pages[i]);

            Tab tab = new RankingTab(labels[i], pages[i]);
            add(tab);
        }

        layoutTabs();

        select(0);
    }

    private class RankingTab extends LabeledTab {

        private Group page;

        public RankingTab(String label, Group page) {
            super(label);
            this.page = page;
        }

        @Override
        protected void select(boolean value) {
            super.select(value);
            if (page != null) {
                page.visible = page.active = selected;
            }
        }
    }

    private class StatsTab extends Group {

        private int GAP = 7;

        public StatsTab() {
            super();

            if (Dungeon.challenges > 0) GAP--;

            String heroClass = Dungeon.hero.className();

            IconTitle title = new IconTitle();
            title.icon(HeroSprite.avatar(Dungeon.hero.heroClass, Dungeon.hero.tier()));
            title.label(Messages.get(this, "title", Dungeon.hero.lvl, heroClass).toUpperCase(Locale.ENGLISH));
            title.color(Window.SHPX_COLOR);
            title.setRect(0, 0, WIDTH, 0);
            add(title);

            float pos = title.bottom();

            if (Dungeon.challenges > 0) {
                RedButton btnCatalogus = new RedButton(Messages.get(this, "challenges")) {
                    @Override
                    protected void onClick() {
                        Game.scene().add(new WndChallenges(Dungeon.challenges, false));
                    }
                };
                btnCatalogus.setRect(0, pos, btnCatalogus.reqWidth() + 2, btnCatalogus.reqHeight() + 2);
                add(btnCatalogus);

                pos = btnCatalogus.bottom();
            }

            pos += GAP + GAP;

            pos = statSlot(this, Messages.get(this, "str"), Integer.toString(Dungeon.hero.STR), pos);
            pos = statSlot(this, Messages.get(this, "health"), Integer.toString(Dungeon.hero.HT), pos);

            pos += GAP;

            pos = statSlot(this, Messages.get(this, "duration"), Integer.toString((int) Statistics.duration), pos);

            pos += GAP;

            pos = statSlot(this, Messages.get(this, "depth"), Integer.toString(Statistics.deepestFloor), pos);
            pos = statSlot(this, Messages.get(this, "enemies"), Integer.toString(Statistics.enemiesSlain), pos);
            pos = statSlot(this, Messages.get(this, "gold"), Integer.toString(Statistics.goldCollected), pos);

            pos += GAP;

            pos = statSlot(this, Messages.get(this, "food"), Integer.toString(Statistics.foodEaten), pos);
            pos = statSlot(this, Messages.get(this, "alchemy"), Integer.toString(Statistics.potionsCooked), pos);
            pos = statSlot(this, Messages.get(this, "ankhs"), Integer.toString(Statistics.ankhsUsed), pos);
        }

        private float statSlot(Group parent, String label, String value, float pos) {

            RenderedTextBlock txt = PixelScene.renderTextBlock(label, 7);
            txt.setPos(0, pos);
            parent.add( txt );

            txt = PixelScene.renderTextBlock( value, 7 );
            txt.setPos(WIDTH * 0.7f, pos);
            PixelScene.align(txt);
            parent.add( txt );

            return pos + GAP + txt.height();
        }
    }

    private class ItemsTab extends Group {

        private float pos;

        public ItemsTab() {
            super();

            Belongings stuff = Dungeon.hero.belongings;
            if (stuff.mainhandweapon != null) {
                addItem(stuff.mainhandweapon);
            }

            if (stuff.offhandweapon != null) {
                addItem(stuff.offhandweapon);
            }

            if (stuff.armor != null) {
                addItem(stuff.armor);
            }
            if (stuff.misc1 != null) {
                addItem(stuff.misc1);
            }
            if (stuff.misc2 != null) {
                addItem(stuff.misc2);
            }

            pos = 0;
            for (int i = 0; i < 4; i++) {

                float posx = i % 4 * (22 + 1);
                float posy = i / 4 * (22 + 1) + 22 * 6 + 6 +19;

                if (Dungeon.quickslot.getItem(i) != null) {
                    QuickSlotButton slot = new QuickSlotButton(Dungeon.quickslot.getItem(i));

                    slot.setRect(posx, posy, 22, 22);

                    add(slot);

                } else {
                    ColorBlock bg = new ColorBlock(22, 22, 0x9953564D);
                    bg.x = posx;
                    bg.y = posy;
                    add(bg);
                }
                //pos += 29;
            }
        }

        private void addItem(Item item) {
            ItemButton slot = new ItemButton(item);
            slot.setRect(0, pos, width, ItemButton.HEIGHT);
            add(slot);

            pos += slot.height() + 1;
        }
    }

    private class BadgesTab extends Group {

        public BadgesTab() {
            super();

            camera = WndRanking.this.camera;

            ScrollPane list = new BadgesList(false);
            add(list);

            list.setSize(WIDTH, HEIGHT);
        }
    }

    private class ItemButton extends Button {

        public static final int HEIGHT = 22;

        private Item item;

        private ItemSlot slot;
        private ColorBlock bg;
        private RenderedTextBlock name;

        public ItemButton(Item item) {

            super();

            this.item = item;

            slot.item(item);
            if (item.cursed && item.cursedKnown) {
                bg.ra = +0.2f;
                bg.ga = -0.1f;
            } else if (!item.isIdentified()) {
                bg.ra = 0.1f;
                bg.ba = 0.1f;
            }
        }

        @Override
        protected void createChildren() {

            bg = new ColorBlock(HEIGHT, HEIGHT, 0x9953564D);
            add(bg);

            slot = new ItemSlot();
            add(slot);

            name = PixelScene.renderTextBlock("?", 7);
            add(name);

            super.createChildren();
        }

        @Override
        protected void layout() {
            bg.x = x;
            bg.y = y;

            slot.setRect(x, y, HEIGHT, HEIGHT);
            PixelScene.align(slot);

            name.maxWidth((int)(width - slot.width() - 2));
            name.text(Messages.titleCase(item.name()));
            name.setPos(
                    slot.right()+2,
                    y + (height - name.height()) / 2
            );
            PixelScene.align(name);

            super.layout();
        }

        protected void onPointerDown() {
            bg.brightness( 1.5f );
            Sample.INSTANCE.play( Assets.SND_CLICK, 0.7f, 0.7f, 1.2f );
        };

        protected void onPointerUp() {
            bg.brightness( 1.0f );
        };

        @Override
        protected void onClick() {
            Game.scene().add(new WndItem(null, item));
        }
    }

    private class QuickSlotButton extends ItemSlot {

        public static final int HEIGHT = 22;

        private Item item;
        private ColorBlock bg;

        QuickSlotButton(Item item) {
            super(item);
            this.item = item;
        }

        @Override
        protected void createChildren() {
            bg = new ColorBlock(HEIGHT, HEIGHT, 0x9953564D);
            add(bg);

            super.createChildren();
        }

        @Override
        protected void layout() {
            bg.x = x;
            bg.y = y;

            super.layout();
        }

        @Override
        protected void onPointerDown() {
            bg.brightness( 1.5f );
            Sample.INSTANCE.play( Assets.SND_CLICK, 0.7f, 0.7f, 1.2f );
        };

        protected void onPointerUp() {
            bg.brightness( 1.0f );
        };

        @Override
        protected void onClick() {
            Game.scene().add(new WndItem(null, item));
        }
    }
}
