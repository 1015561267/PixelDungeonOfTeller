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
package com.teller.pixeldungeonofteller.levels;

import com.teller.pixeldungeonofteller.Assets;
import com.teller.pixeldungeonofteller.Challenges;
import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.PixelDungeonOfTeller;
import com.teller.pixeldungeonofteller.actors.buffs.Noise;
import com.teller.pixeldungeonofteller.actors.hazards.Hazard;
import com.teller.pixeldungeonofteller.levels.rooms.special.MassGraveRoom;
import com.teller.pixeldungeonofteller.tiles.CustomTiledVisual;
import com.teller.pixeldungeonofteller.tiles.DungeonTileSheet;
import com.teller.pixeldungeonofteller.Statistics;
import com.teller.pixeldungeonofteller.actors.Actor;
import com.teller.pixeldungeonofteller.actors.Char;
import com.teller.pixeldungeonofteller.actors.blobs.Alchemy;
import com.teller.pixeldungeonofteller.actors.blobs.Blob;
import com.teller.pixeldungeonofteller.actors.blobs.WellWater;
import com.teller.pixeldungeonofteller.actors.buffs.Awareness;
import com.teller.pixeldungeonofteller.actors.buffs.Blindness;
import com.teller.pixeldungeonofteller.actors.buffs.Buff;
import com.teller.pixeldungeonofteller.actors.buffs.LockedFloor;
import com.teller.pixeldungeonofteller.actors.buffs.MindVision;
import com.teller.pixeldungeonofteller.actors.buffs.Shadows;
import com.teller.pixeldungeonofteller.actors.hero.Hero;
import com.teller.pixeldungeonofteller.actors.hero.HeroClass;
import com.teller.pixeldungeonofteller.actors.mobs.Bestiary;
import com.teller.pixeldungeonofteller.actors.mobs.Mob;
import com.teller.pixeldungeonofteller.effects.particles.FlowParticle;
import com.teller.pixeldungeonofteller.effects.particles.WindParticle;
import com.teller.pixeldungeonofteller.items.Dewdrop;
import com.teller.pixeldungeonofteller.items.Generator;
import com.teller.pixeldungeonofteller.items.Heap;
import com.teller.pixeldungeonofteller.items.Item;
import com.teller.pixeldungeonofteller.items.Stylus;
import com.teller.pixeldungeonofteller.items.Torch;
import com.teller.pixeldungeonofteller.items.armor.Armor;
import com.teller.pixeldungeonofteller.items.artifacts.AlchemistsToolkit;
import com.teller.pixeldungeonofteller.items.artifacts.DriedRose;
import com.teller.pixeldungeonofteller.items.artifacts.TimekeepersHourglass;
import com.teller.pixeldungeonofteller.items.bags.ScrollHolder;
import com.teller.pixeldungeonofteller.items.bags.SeedPouch;
import com.teller.pixeldungeonofteller.items.food.Blandfruit;
import com.teller.pixeldungeonofteller.items.food.Food;
import com.teller.pixeldungeonofteller.items.potions.PotionOfHealing;
import com.teller.pixeldungeonofteller.items.potions.PotionOfMight;
import com.teller.pixeldungeonofteller.items.potions.PotionOfStrength;
import com.teller.pixeldungeonofteller.items.rings.RingOfWealth;
import com.teller.pixeldungeonofteller.items.scrolls.Scroll;
import com.teller.pixeldungeonofteller.items.scrolls.ScrollOfMagicalInfusion;
import com.teller.pixeldungeonofteller.items.scrolls.ScrollOfUpgrade;
import com.teller.pixeldungeonofteller.levels.features.Chasm;
import com.teller.pixeldungeonofteller.levels.features.Door;
import com.teller.pixeldungeonofteller.levels.features.HighGrass;
import com.teller.pixeldungeonofteller.levels.painters.Painter;
import com.teller.pixeldungeonofteller.levels.traps.Trap;
import com.teller.pixeldungeonofteller.mechanics.ShadowCaster;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.plants.BlandfruitBush;
import com.teller.pixeldungeonofteller.plants.Plant;
import com.teller.pixeldungeonofteller.scenes.GameScene;
import com.teller.pixeldungeonofteller.sprites.ItemSprite;
import com.teller.pixeldungeonofteller.utils.BArray;
import com.teller.pixeldungeonofteller.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import static com.teller.pixeldungeonofteller.Dungeon.posNeeded;

public abstract class Level implements Bundlable {

    public enum Feeling {
        NONE,
        CHASM,
        WATER,
        GRASS,
        DARK
    }

    protected int width;
    protected int height;
    protected int length;

    protected static final float TIME_TO_RESPAWN = 50;
    private static final String VERSION = "version";
    private static final String WIDTH       = "width";
    private static final String HEIGHT      = "height";
    private static final String MAP = "map";
    private static final String VISITED = "visited";
    private static final String MAPPED = "mapped";
    private static final String ENTRANCE = "entrance";
    private static final String EXIT = "exit";
    private static final String LOCKED = "locked";

    private static final String HAZARDS		= "hazards";

    private static final String HEAPS = "heaps";
    private static final String PLANTS = "plants";
    private static final String TRAPS = "traps";
    private static final String CUSTOM_TILES = "customTiles";
    private static final String CUSTOM_WALLS= "customWalls";
    private static final String MOBS = "mobs";
    private static final String BLOBS = "blobs";
    private static final String FEELING = "feeling";
    //FIXME should not be static!
    public  boolean[] fieldOfView = null;

    public  boolean[] passable;
    public  boolean[] losBlocking;
    public  boolean[] flamable;
    public  boolean[] secret;
    public  boolean[] solid;
    public  boolean[] avoid;
    public  boolean[] water;
    public  boolean[] pit;
    public boolean[] discoverable;

    public boolean[] ice;

    public static boolean weakFloorCreated = false;
    //FIXME this is sloppy. Should be able to keep track of this without static variables
    public int version;
    public int[] map;
    public boolean[] visited;
    public boolean[] mapped;
    public int viewDistance = Dungeon.isChallenged(Challenges.DARKNESS) ? 4 : 8;
    public Feeling feeling = Feeling.NONE;

    public int entrance;
    public int exit;

    //when a boss level has become locked.
    public boolean locked = false;
    public HashSet<Mob> mobs;

    public HashSet<Hazard> hazards;//from yet another pd

    public SparseArray<Heap> heaps;
    public HashMap<Class<? extends Blob>, Blob> blobs;
    public SparseArray<Plant> plants;
    public SparseArray<Trap> traps;
    public HashSet<CustomTiledVisual> customTiles;
    public HashSet<CustomTiledVisual> customWalls;
    public int color1 = 0x004400;
    public int color2 = 0x88CC44;
    protected ArrayList<Item> itemsToSpawn = new ArrayList<>();

    protected Group visuals;

    public void create() {

        if (!(Dungeon.bossLevel() || Dungeon.depth == 21) /*final shop floor*/) {
            addItemToSpawn( Generator.random( Generator.Category.FOOD ) );

            int bonus = RingOfWealth.getBonus(Dungeon.hero, RingOfWealth.Wealth.class);

            if (posNeeded()) {
                if (Random.Float() > Math.pow(0.925, bonus))
                    addItemToSpawn( new PotionOfMight() );
                else
                    addItemToSpawn( new PotionOfStrength() );
                Dungeon.limitedDrops.strengthPotions.count++;
            }
            if (Dungeon.souNeeded()) {
                if (Random.Float() > Math.pow(0.925, bonus))
                    addItemToSpawn( new ScrollOfMagicalInfusion() );
                else
                    addItemToSpawn( new ScrollOfUpgrade() );
                Dungeon.limitedDrops.upgradeScrolls.count++;
            }
            if (Dungeon.asNeeded()) {
                if (Random.Float() > Math.pow(0.925, bonus))
                    addItemToSpawn( new Stylus() );
                addItemToSpawn( new Stylus() );
                Dungeon.limitedDrops.arcaneStyli.count++;
            }

            DriedRose rose = Dungeon.hero.belongings.getItem( DriedRose.class );
            if (rose != null && !rose.cursed){
                //this way if a rose is dropped later in the game, player still has a chance to max it out.
                int petalsNeeded = (int) Math.ceil((float)((Dungeon.depth / 2) - rose.droppedPetals) / 3);

                for (int i=1; i <= petalsNeeded; i++) {
                    //the player may miss a single petal and still max their rose.
                    if (rose.droppedPetals < 11) {
                        addItemToSpawn(new DriedRose.Petal());
                        rose.droppedPetals++;
                    }
                }
            }

            if (Dungeon.depth > 1) {
                switch (Random.Int( 10 )) {
                    case 0:
                        if (!Dungeon.bossLevel( Dungeon.depth + 1 )) {
                            feeling = Feeling.CHASM;
                        }
                        break;
                    case 1:
                        feeling = Feeling.WATER;
                        break;
                    case 2:
                        feeling = Feeling.GRASS;
                        break;
                    case 3:
                        feeling = Feeling.DARK;
                        addItemToSpawn(new Torch());
                        viewDistance = Math.round(viewDistance/2f);
                        break;
                }
            }
        }

        do {
            width = height = length = 0;

            mobs = new HashSet<>();

            hazards = new HashSet<Hazard>();

            heaps = new SparseArray<>();

            blobs = new HashMap<>();
            plants = new SparseArray<>();
            traps = new SparseArray<>();
            customTiles = new HashSet<>();
            customWalls = new HashSet<>();

        } while (!build());

        buildFlagMaps();
        cleanWalls();

        createMobs();
        createItems();

        Random.seed();
    }

    public void setSize(int w, int h){

        width = w;
        height = h;
        length = w * h;

        map = new int[length];
        //Arrays.fill( map, Terrain.WALL );
        Arrays.fill( map, feeling == Level.Feeling.CHASM ? Terrain.CHASM : Terrain.WALL );

        visited = new boolean[length];
        mapped = new boolean[length];
        Dungeon.visible = new boolean[length];

        fieldOfView = new boolean[length()];

        passable	= new boolean[length()];
        losBlocking	= new boolean[length()];
        flamable	= new boolean[length()];
        secret		= new boolean[length()];
        solid		= new boolean[length()];
        avoid		= new boolean[length()];
        water		= new boolean[length()];
        pit			= new boolean[length()];

        ice		= new boolean[length()];

        PathFinder.setMapSize(w, h);
    }

    public void reset() {

        for (Mob mob : mobs.toArray(new Mob[0])) {
            if (!mob.reset()) {
                mobs.remove(mob);
            }
        }
        createMobs();
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {

        version = bundle.getInt(VERSION);

        //if (bundle.contains("width") && bundle.contains("height")) {
        //    width = bundle.getInt("width");
        //    height = bundle.getInt("height");
        //} else
            setSize( bundle.getInt(WIDTH), bundle.getInt(HEIGHT)); //default sizes

        mobs = new HashSet<>();

        hazards = new HashSet<>();

        heaps = new SparseArray<>();
        blobs = new HashMap<>();
        plants = new SparseArray<>();
        traps = new SparseArray<>();
        customTiles = new HashSet<>();
        customWalls = new HashSet<>();

        map = bundle.getIntArray(MAP);

        visited = bundle.getBooleanArray(VISITED);
        mapped = bundle.getBooleanArray(MAPPED);

        entrance = bundle.getInt(ENTRANCE);
        exit = bundle.getInt(EXIT);

        locked = bundle.getBoolean(LOCKED);

        //for pre-0.3.0c saves
        //if (version < 44) {
        //    map = Terrain.convertTrapsFrom43(map, traps);
        //}

        ///for pre-0.4.3 saves
        //if (version < 130) {
        //    map = Terrain.convertTilesFrom129(map);
        //}

        Collection<Bundlable> collection = bundle.getCollection(HEAPS);
        for (Bundlable h : collection) {
            Heap heap = (Heap) h;
            if (!heap.isEmpty())
                heaps.put(heap.pos, heap);
        }

        collection = bundle.getCollection( HAZARDS );
        for (Bundlable z : collection) {
            Hazard hazard = (Hazard)z;
            if (hazard != null){
                //if( resizingNeeded ){
                //    hazard.pos = adjustPos( hazard.pos );
                //}
                hazards.add( hazard );
            }
        }

        collection = bundle.getCollection(PLANTS);
        for (Bundlable p : collection) {
            Plant plant = (Plant) p;
            plants.put(plant.pos, plant);
        }

        collection = bundle.getCollection(TRAPS);
        for (Bundlable p : collection) {
            Trap trap = (Trap) p;
            traps.put(trap.pos, trap);
        }

        collection = bundle.getCollection(CUSTOM_TILES);
        for (Bundlable p : collection) {
            CustomTiledVisual vis = (CustomTiledVisual) p;
            //for compatibilities with pre-0.5.0b saves
            //extends one of the bones visuals and discards the rest
            if (vis instanceof MassGraveRoom.Bones && vis.tileH == 0){
                int cell = vis.tileX + vis.tileY*width;
                if (map[cell] == Terrain.EMPTY_SP &&
                        DungeonTileSheet.wallStitcheable(map[cell - width]) &&
                        DungeonTileSheet.wallStitcheable(map[cell - 1])){

                    vis.tileY--; //move top to into the wall
                    vis.tileW = 1;
                    vis.tileH = 2;

                    while (map[cell+1] == Terrain.EMPTY_SP){
                        vis.tileW++;
                        cell++;
                    }
                    while (map[cell+width] == Terrain.EMPTY_SP){
                        vis.tileH++;
                        cell+=width;
                    }
                    customTiles.add(vis);
                }
            } else {
                customTiles.add(vis);
            }
            customTiles.add(vis);
        }

        collection = bundle.getCollection( CUSTOM_WALLS );
        for (Bundlable p : collection) {
            CustomTiledVisual vis = (CustomTiledVisual)p;
            customWalls.add(vis);
        }

        collection = bundle.getCollection(MOBS);
        for (Bundlable m : collection) {
            Mob mob = (Mob) m;
            if (mob != null) {
                mobs.add(mob);
            }
        }

        collection = bundle.getCollection(BLOBS);
        for (Bundlable b : collection) {
            Blob blob = (Blob) b;
            blobs.put(blob.getClass(), blob);
        }

        feeling = bundle.getEnum(FEELING, Feeling.class);
        if (feeling == Feeling.DARK)
            viewDistance = (int) Math.round(viewDistance / 2f);

        buildFlagMaps();
        cleanWalls();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(VERSION, Game.versionCode);
        bundle.put( WIDTH, width );
        bundle.put( HEIGHT, height );
        bundle.put(MAP, map);
        bundle.put(VISITED, visited);
        bundle.put(MAPPED, mapped);
        bundle.put(ENTRANCE, entrance);
        bundle.put(EXIT, exit);
        bundle.put(LOCKED, locked);

        bundle.put(HAZARDS,hazards);

        bundle.put(HEAPS, heaps.valueList());
        bundle.put(PLANTS, plants.valueList());
        bundle.put(TRAPS, traps.valueList());
        bundle.put(CUSTOM_TILES, customTiles);
        bundle.put(CUSTOM_WALLS, customWalls );
        bundle.put(MOBS, mobs);
        bundle.put(BLOBS, blobs.values());
        bundle.put(FEELING, feeling);
    }

    public int tunnelTile() {
        return feeling == Feeling.CHASM ? Terrain.EMPTY_SP : Terrain.EMPTY;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int length() {
        return length;
    }

    public String tilesTex() {
        return null;
    }

    public String waterTex() {
        return null;
    }

    abstract protected boolean build();

    abstract public Mob createMob();

    abstract protected void createMobs();

    abstract protected void createItems();

    public void seal() {
        if (!locked) {
            locked = true;
            Buff.affect(Dungeon.hero, LockedFloor.class);
        }
    }

    public void unseal() {
        if (locked) {
            locked = false;
        }
    }

    public Group addVisuals() {
        if (visuals == null || visuals.parent == null) {
            visuals = new Group();
        } else {
            visuals.clear();
            visuals.camera = null;
        }
        for (int i = 0; i < length(); i++) {
            if (pit[i]) {
                visuals.add(new WindParticle.Wind(i));
                if (i >= width() && water[i - width()]) {
                    visuals.add(new FlowParticle.Flow(i - width()));
                }
            }
        }
        return visuals;
    }

    public int nMobs() {
        return 0;
    }

    public Mob findMob(int pos) {
        for (Mob mob : mobs) {
            if (mob.pos == pos) {
                return mob;
            }
        }
        return null;
    }

    public Actor respawner() {
        return new Actor() {

            {
                actPriority = 1; //as if it were a buff.
            }

            @Override
            protected boolean act() {
                if (mobs.size() < nMobs()) {

                    Mob mob = Bestiary.mutable(Dungeon.depth);
                    mob.state = mob.WANDERING;
                    mob.pos = randomRespawnCell();
                    if (Dungeon.hero.isAlive() && mob.pos != -1 && distance(Dungeon.hero.pos, mob.pos) >= 4) {
                        GameScene.add(mob);
                        if (Statistics.amuletObtained) {
                            mob.beckon(Dungeon.hero.pos);
                        }
                    }
                }
                spend(Dungeon.level.feeling == Feeling.DARK || Statistics.amuletObtained ? TIME_TO_RESPAWN / 2 : TIME_TO_RESPAWN);
                return true;
            }
        };
    }

    public int randomRespawnCell() {
        int cell;
        do {
            cell = Random.Int(length());
        } while (!passable[cell] || Dungeon.visible[cell] || Actor.findChar(cell) != null);
        return cell;
    }

    public int randomDestination() {
        int cell;
        do {
            cell = Random.Int(length());
        } while (!passable[cell]);
        return cell;
    }

    public void addItemToSpawn(Item item) {
        if (item != null) {
            itemsToSpawn.add(item);
        }
    }

    public Item findPrizeItem() {
        return findPrizeItem(null);
    }

    public Item findPrizeItem(Class<? extends Item> match) {
        if (itemsToSpawn.size() == 0)
            return null;

        if (match == null) {
            Item item = Random.element(itemsToSpawn);
            itemsToSpawn.remove(item);
            return item;
        }

        for (Item item : itemsToSpawn) {
            if (match.isInstance(item)) {
                itemsToSpawn.remove(item);
                return item;
            }
        }

        return null;
    }

    protected void buildFlagMaps() {

        for (int i = 0; i < length(); i++) {
            int flags = Terrain.flags[map[i]];
            passable[i] = (flags & Terrain.PASSABLE) != 0;
            losBlocking[i] = (flags & Terrain.LOS_BLOCKING) != 0;
            flamable[i] = (flags & Terrain.FLAMABLE) != 0;
            secret[i] = (flags & Terrain.SECRET) != 0;
            solid[i] = (flags & Terrain.SOLID) != 0;
            avoid[i] = (flags & Terrain.AVOID) != 0;
            water[i] = (flags & Terrain.LIQUID) != 0;
            pit[i] = (flags & Terrain.PIT) != 0;

            ice[i] = (flags & Terrain.SLIPPERY) != 0;
        }

        int lastRow = length() - width();
        for (int i = 0; i < width(); i++) {
            passable[i] = avoid[i] = false;
            losBlocking[i] = true;
            passable[lastRow + i] = avoid[lastRow + i] = false;
            losBlocking[lastRow + i] = true;
        }
        for (int i = width(); i < lastRow; i += width()) {
            passable[i] = avoid[i] = false;
            losBlocking[i] = true;
            passable[i + width() - 1] = avoid[i + width() - 1] = false;
            losBlocking[i + width()-1] = true;
        }
    }

    public void destroy(int pos) {
        set(pos, Terrain.EMBERS);
    }

    protected void cleanWalls() {
        discoverable = new boolean[length()];

        for (int i = 0; i < length(); i++) {

            boolean d = false;

            for (int j = 0; j < PathFinder.NEIGHBOURS9.length; j++) {
                int n = i + PathFinder.NEIGHBOURS9[j];
                if (n >= 0 && n < length() && map[n] != Terrain.WALL && map[n] != Terrain.WALL_DECO) {
                    d = true;
                    break;
                }
            }
            discoverable[i] = d;
        }
    }

    public static void set( int cell, int terrain ){
        set( cell, terrain, Dungeon.level );
    }

    public static void set( int cell, int terrain, Level level ) {
        Painter.set( level, cell, terrain );

        if (terrain != Terrain.TRAP && terrain != Terrain.SECRET_TRAP && terrain != Terrain.INACTIVE_TRAP){
            level.traps.remove( cell );
        }

        int flags = Terrain.flags[terrain];
        level.passable[cell]		= (flags & Terrain.PASSABLE) != 0;
        level.losBlocking[cell]	    = (flags & Terrain.LOS_BLOCKING) != 0;
        level.flamable[cell]		= (flags & Terrain.FLAMABLE) != 0;
        level.secret[cell]		    = (flags & Terrain.SECRET) != 0;
        level.solid[cell]			= (flags & Terrain.SOLID) != 0;
        level.avoid[cell]			= (flags & Terrain.AVOID) != 0;
        level.pit[cell]			    = (flags & Terrain.PIT) != 0;
        level.water[cell]			= terrain == Terrain.WATER;

        level.ice[cell]             = terrain == Terrain.ICE;

    }

    public Heap drop(Item item, int cell) {

        //This messy if statement deals will items which should not drop in challenges primarily.
        if ((Dungeon.isChallenged(Challenges.NO_FOOD) && (item instanceof Food || item instanceof BlandfruitBush.Seed)) ||
                (Dungeon.isChallenged(Challenges.NO_ARMOR) && item instanceof Armor) ||
                (Dungeon.isChallenged(Challenges.NO_HEALING) && item instanceof PotionOfHealing) ||
                (Dungeon.isChallenged(Challenges.NO_HERBALISM) && (item instanceof Plant.Seed || item instanceof Dewdrop || item instanceof SeedPouch)) ||
                (Dungeon.isChallenged(Challenges.NO_SCROLLS) && ((item instanceof Scroll && !(item instanceof ScrollOfUpgrade || item instanceof ScrollOfMagicalInfusion)) || item instanceof ScrollHolder)) ||
                item == null) {

            //create a dummy heap, give it a dummy sprite, don't add it to the game, and return it.
            //effectively nullifies whatever the logic calling this wants to do, including dropping items.
            Heap heap = new Heap();
            ItemSprite sprite = heap.sprite = new ItemSprite();
            sprite.link(heap);
            return heap;

        }

        if ((map[cell] == Terrain.ALCHEMY) && (
                !(item instanceof Plant.Seed || item instanceof Blandfruit) ||
                        item instanceof BlandfruitBush.Seed ||
                        (item instanceof Blandfruit && (((Blandfruit) item).potionAttrib != null || heaps.get(cell) != null)) ||
                        Dungeon.hero.buff(AlchemistsToolkit.alchemy.class) != null && Dungeon.hero.buff(AlchemistsToolkit.alchemy.class).isCursed())) {
            int n;
            do {
                n = cell + PathFinder.NEIGHBOURS8[Random.Int(8)];
            } while (map[n] != Terrain.EMPTY_SP);
            cell = n;
        }

        Heap heap = heaps.get(cell);
        if (heap == null) {

            heap = new Heap();
            heap.seen = Dungeon.visible[cell];
            heap.pos = cell;
            heap.drop(item);
            if (map[cell] == Terrain.CHASM || (Dungeon.level != null && pit[cell])) {
                Dungeon.dropToChasm(item);
                GameScene.discard(heap);
            } else {
                heaps.put(cell, heap);
                GameScene.add(heap);
            }

        } else if (heap.type == Heap.Type.LOCKED_CHEST || heap.type == Heap.Type.CRYSTAL_CHEST) {

            int n;
            do {
                n = cell + PathFinder.NEIGHBOURS8[Random.Int(8)];
            } while (!passable[n] && !avoid[n]);
            return drop(item, n);

        }
        else {
            heap.drop(item);
        }

        if (Dungeon.level != null) {
            press(cell, null);
        }

        return heap;
    }

    public Plant plant(Plant.Seed seed, int pos) {

        if (Dungeon.isChallenged(Challenges.NO_HERBALISM)){
            return null;
        }

        Plant plant = plants.get(pos);
        if (plant != null) {
            plant.wither();
        }

        if (map[pos] == Terrain.HIGH_GRASS ||
                map[pos] == Terrain.EMPTY ||
                map[pos] == Terrain.EMBERS ||
                map[pos] == Terrain.EMPTY_DECO) {
            map[pos] = Terrain.GRASS;
            flamable[pos] = true;
        }

        plant = seed.couch(pos);
        plants.put(pos, plant);

        GameScene.plantSeed(pos);

        return plant;
    }

    public void uproot(int pos) {
        plants.remove(pos);
        GameScene.updateMap(pos);
    }

    public Trap setTrap(Trap trap, int pos) {
        Trap existingTrap = traps.get(pos);
        if (existingTrap != null) {
            traps.remove(pos);
        }
        trap.set(pos);
        traps.put(pos, trap);
        GameScene.updateMap(pos);
        return trap;
    }

    public void disarmTrap(int pos) {
        set(pos, Terrain.INACTIVE_TRAP);
        GameScene.updateMap(pos);
    }

    public void discover(int cell) {
        set(cell, Terrain.discover(map[cell]));
        Trap trap = traps.get(cell);
        if (trap != null)
            trap.reveal();
        GameScene.updateMap(cell);
    }


    public int fallCell( boolean fallIntoPit ) {
        int result;
        do {
            result = randomRespawnCell();
        } while (traps.get(result) != null
                || findMob(result) != null
                || heaps.get(result) != null);
        return result;
    }

    public void press(int cell, Char ch) {
        if (ch != null && pit[cell] && !ch.flying) {
            if (ch == Dungeon.hero) {
                Chasm.heroFall(cell);
            } else if (ch instanceof Mob) {
                Chasm.mobFall((Mob) ch);
            }
            return;
        }

        Trap trap = null;

        switch (map[cell]) {

            case Terrain.SECRET_TRAP:
                GLog.i(Messages.get(Level.class, "hidden_plate"));
            case Terrain.TRAP:
                trap = traps.get(cell);
                break;

            case Terrain.HIGH_GRASS:
                HighGrass.trample(this, cell, ch);
                break;

            case Terrain.WELL:
                WellWater.affectCell(cell);
                break;

            case Terrain.ALCHEMY:
                if (ch == null) {
                    Alchemy.transmute( cell );
                }
                break;

            case Terrain.DOOR:
                Door.enter(cell);
                break;
        }

        TimekeepersHourglass.timeFreeze timeFreeze = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);

        if (trap != null) {
            if (timeFreeze == null) {
                if (ch == Dungeon.hero)
                    Dungeon.hero.interrupt();
                    trap.trigger();
                    if(Dungeon.hero.isAlive()) { Dungeon.hero.buff(Noise.class).trapNoise(); }
            } else {
                Sample.INSTANCE.play(Assets.SND_TRAP);
                discover(cell);
                timeFreeze.setDelayedPress(cell);
            }
        }
        Plant plant = plants.get(cell);
        if (plant != null) {
            plant.trigger();
        }
    }

    public void mobPress(Mob mob) {

        int cell = mob.pos;

        if (pit[cell] && !mob.flying) {
            Chasm.mobFall(mob);
            return;
        }

        Trap trap = null;
        switch (map[cell]) {

            case Terrain.TRAP:
                trap = traps.get(cell);
                break;

            case Terrain.DOOR:
                Door.enter(cell);
                break;
        }

        if (trap != null) {
            trap.trigger();
        }

        Plant plant = plants.get(cell);
        if (plant != null) {
            plant.trigger();
        }

        if ( map[cell] == Terrain.HIGH_GRASS){
            HighGrass.trample( this, cell, mob );
        }
    }

    public void updateFieldOfView(Char c, boolean[] fieldOfView) {

        int cx = c.pos % width();
        int cy = c.pos / width();


        boolean heroResting = c instanceof Hero && ((Hero) c).resting;

        boolean sighted = c.buff(Blindness.class) == null && c.buff(Shadows.class) == null
                && c.buff(TimekeepersHourglass.timeStasis.class) == null && c.isAlive();

        if(heroResting){
            sighted = false;
        }

        if (sighted) {
            ShadowCaster.castShadow(cx, cy, fieldOfView, c.getViewDistance());
        } else {
            BArray.setFalse(fieldOfView);
        }

        int sense = 1;
        //Currently only the hero can get mind vision
        if (c.isAlive() && c == Dungeon.hero) {
            for (Buff b : c.buffs(MindVision.class)) {
                sense = Math.max(((MindVision) b).distance, sense);
            }
        }

        if (!sighted || sense > 1) {

            int ax = Math.max(0, cx - sense);
            int bx = Math.min(cx + sense, width() - 1);
            int ay = Math.max(0, cy - sense);
            int by = Math.min(cy + sense, height() - 1);

            int len = bx - ax + 1;
            int pos = ax + ay * width();
            for (int y = ay; y <= by; y++, pos += width()) {
                System.arraycopy(discoverable, pos, fieldOfView, pos, len);
            }
        }

        //Currently only the hero can get mind vision or awareness
        if (c.isAlive() && c == Dungeon.hero) {
            Dungeon.hero.mindVisionEnemies.clear();
            if (c.buff(MindVision.class) != null) {
                for (Mob mob : mobs) {
                    int p = mob.pos;

                    if (!fieldOfView[p]) {
                        Dungeon.hero.mindVisionEnemies.add(mob);
                    }

                }
            } else if (((Hero) c).heroClass == HeroClass.HUNTRESS) {
                for (Mob mob : mobs) {
                    int p = mob.pos;
                    if (distance(c.pos, p) == 2) {

                        if (!fieldOfView[p]) {
                            Dungeon.hero.mindVisionEnemies.add(mob);
                        }
                    }
                }
            }

            for (Mob m : Dungeon.hero.mindVisionEnemies) {
                for (int i : PathFinder.NEIGHBOURS9) {
                    fieldOfView[m.pos + i] = true;
                }
            }

            if (c.buff(Awareness.class) != null) {
                for (Heap heap : heaps.values()) {
                    int p = heap.pos;
                    for (int i : PathFinder.NEIGHBOURS9)
                        fieldOfView[p + i] = true;
                }
            }
        }

        if (c == Dungeon.hero) {
            for (Heap heap : heaps.values())
                if (!heap.seen && fieldOfView[heap.pos])
                    heap.seen = true;
        }

    }

    public int distance(int a, int b) {
        int ax = a % width();
        int ay = a / width();
        int bx = b % width();
        int by = b / width();
        return Math.max(Math.abs(ax - bx), Math.abs(ay - by));
    }

    public boolean adjacent(int a, int b) {
        return distance(a, b) == 1;
    }

    //returns true if the input is a valid tile within the level
    public boolean insideMap(int tile) {
        //top and bottom row and beyond
        return !((tile < width || tile >= length - width) ||
                //left and right column
                (tile % width == 0 || tile % width == width - 1));
    }

    public Point cellToPoint(int cell) {
        return new Point(cell % width(), cell / width());
    }

    public int pointToCell(Point p) {
        return p.x + p.y * width();
    }

    public String tileName(int tile) {

        switch (tile) {
            case Terrain.CHASM:
                return Messages.get(Level.class, "chasm_name");
            case Terrain.EMPTY:
            case Terrain.EMPTY_SP:
            case Terrain.EMPTY_DECO:
            case Terrain.SECRET_TRAP:
                return Messages.get(Level.class, "floor_name");
            case Terrain.GRASS:
                return Messages.get(Level.class, "grass_name");
            case Terrain.WATER:
                return Messages.get(Level.class, "water_name");

            case Terrain.ICE:
                return Messages.get(Level.class, "ice_name");

            case Terrain.WALL:
            case Terrain.WALL_DECO:
            case Terrain.SECRET_DOOR:
                return Messages.get(Level.class, "wall_name");
            case Terrain.DOOR:
                return Messages.get(Level.class, "closed_door_name");
            case Terrain.OPEN_DOOR:
                return Messages.get(Level.class, "open_door_name");
            case Terrain.ENTRANCE:
                return Messages.get(Level.class, "entrace_name");
            case Terrain.EXIT:
                return Messages.get(Level.class, "exit_name");
            case Terrain.EMBERS:
                return Messages.get(Level.class, "embers_name");
            case Terrain.LOCKED_DOOR:
                return Messages.get(Level.class, "locked_door_name");
            case Terrain.PEDESTAL:
                return Messages.get(Level.class, "pedestal_name");
            case Terrain.BARRICADE:
                return Messages.get(Level.class, "barricade_name");
            case Terrain.HIGH_GRASS:
                return Messages.get(Level.class, "high_grass_name");
            case Terrain.LOCKED_EXIT:
                return Messages.get(Level.class, "locked_exit_name");
            case Terrain.UNLOCKED_EXIT:
                return Messages.get(Level.class, "unlocked_exit_name");
            case Terrain.SIGN:
                return Messages.get(Level.class, "sign_name");
            case Terrain.WELL:
                return Messages.get(Level.class, "well_name");
            case Terrain.EMPTY_WELL:
                return Messages.get(Level.class, "empty_well_name");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(Level.class, "statue_name");
            case Terrain.INACTIVE_TRAP:
                return Messages.get(Level.class, "inactive_trap_name");
            case Terrain.BOOKSHELF:
                return Messages.get(Level.class, "bookshelf_name");
            case Terrain.ALCHEMY:
                return Messages.get(Level.class, "alchemy_name");
            default:
                return Messages.get(Level.class, "default_name");
        }
    }

    public String tileDesc(int tile) {

        switch (tile) {
            case Terrain.CHASM:
                return Messages.get(Level.class, "chasm_desc");
            case Terrain.WATER:
                return Messages.get(Level.class, "water_desc");

            case Terrain.ICE:
                return Messages.get(Level.class, "ice_desc");

            case Terrain.ENTRANCE:
                return Messages.get(Level.class, "entrance_desc");
            case Terrain.EXIT:
            case Terrain.UNLOCKED_EXIT:
                return Messages.get(Level.class, "exit_desc");
            case Terrain.EMBERS:
                return Messages.get(Level.class, "embers_desc");
            case Terrain.HIGH_GRASS:
                return Messages.get(Level.class, "high_grass_desc");
            case Terrain.LOCKED_DOOR:
                return Messages.get(Level.class, "locked_door_desc");
            case Terrain.LOCKED_EXIT:
                return Messages.get(Level.class, "locked_exit_desc");
            case Terrain.BARRICADE:
                return Messages.get(Level.class, "barricade_desc");
            case Terrain.SIGN:
                return Messages.get(Level.class, "sign_desc");
            case Terrain.INACTIVE_TRAP:
                return Messages.get(Level.class, "inactive_trap_desc");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(Level.class, "statue_desc");
            case Terrain.ALCHEMY:
                return Messages.get(Level.class, "alchemy_desc");
            case Terrain.EMPTY_WELL:
                return Messages.get(Level.class, "empty_well_desc");
            default:
                return Messages.get(Level.class, "default_desc");
        }
    }

    public int getWidth(){return width;}
    public int getHeight(){return height;}
}
