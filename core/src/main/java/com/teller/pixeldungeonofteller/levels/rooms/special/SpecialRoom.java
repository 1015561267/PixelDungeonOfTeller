package com.teller.pixeldungeonofteller.levels.rooms.special;

import com.teller.pixeldungeonofteller.Challenges;
import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.PixelDungeonOfTeller;
import com.teller.pixeldungeonofteller.actors.blobs.WaterOfHealth;
import com.teller.pixeldungeonofteller.actors.blobs.WaterOfTransmutation;
import com.teller.pixeldungeonofteller.levels.rooms.Room;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SpecialRoom extends Room {

    @Override
    public int minWidth() { return 5; }
    public int maxWidth() { return 10; }

    @Override
    public int minHeight() {
        return 5;
    }
    public int maxHeight() { return 10; }

    @Override
    public int minConnections(int direction) {
        if (direction == ALL)   return 1;
        else                    return 0;
    }

    @Override
    public int maxConnections(int direction) {
        return 1;
    }

    public Door entrance() {
        return connected.values().iterator().next();
    }

    private static final ArrayList<Class<? extends SpecialRoom>> ALL_SPEC = new ArrayList<>( Arrays.asList(
            WeakFloorRoom.class, MagicWellRoom.class, CryptRoom.class, PoolRoom.class, FoliageRoom.class, LibraryRoom.class, ArmoryRoom.class,
            TreasuryRoom.class, TrapsRoom.class, StorageRoom.class, StatueRoom.class, LaboratoryRoom.class, VaultRoom.class
    ) );

    public static ArrayList<Class<? extends Room>> runSpecials = new ArrayList<>();
    public static ArrayList<Class<? extends Room>> floorSpecials = new ArrayList<>();

    private static int pitNeededDepth = -1;
    private static int guaranteedWellDepth = Integer.MAX_VALUE;

    public static void initForRun() {
        runSpecials = (ArrayList<Class<?extends Room>>)ALL_SPEC.clone();

        //remove special rooms disallowed by challenges
        if (Dungeon.isChallenged( Challenges.NO_ARMOR )){
            //no sense in giving an armor reward room on a run with no armor.
            runSpecials.remove( CryptRoom.class );
        }
        if (Dungeon.isChallenged( Challenges.NO_HERBALISM )){
            //Would be a bit mean to spawn these with no plants in them
            runSpecials.remove( FoliageRoom.class );
        }

        pitNeededDepth = -1;
        guaranteedWellDepth = Random.IntRange( 6, 14 );
        //guaranteedWellDepth = 2;
        Random.shuffle(runSpecials);
    }

    public static void initForFloor(){
        //laboratory rooms are more common
        int labIdx = runSpecials.indexOf(LaboratoryRoom.class);
        if (labIdx > 0) {
            Collections.swap(runSpecials, labIdx-1, labIdx);
        }

        floorSpecials = (ArrayList<Class<?extends Room>>) runSpecials.clone();
    }

    private static void useType( Class<?extends Room> type ) {
        if (runSpecials.remove( type )) {
            floorSpecials.remove( type );
            runSpecials.add( type );
        }
    }

    public static void resetPitRoom(int depth){
        if (pitNeededDepth == depth) pitNeededDepth = -1;
    }

    public static void disableGuaranteedWell(){
        guaranteedWellDepth = Integer.MAX_VALUE;
    }

    public static SpecialRoom createRoom(){
        if (Dungeon.depth == pitNeededDepth){
            pitNeededDepth = -1;

            floorSpecials.remove( ArmoryRoom.class );
            floorSpecials.remove( CryptRoom.class );
            floorSpecials.remove( LaboratoryRoom.class );
            floorSpecials.remove( LibraryRoom.class );
            floorSpecials.remove( StatueRoom.class );
            floorSpecials.remove( TreasuryRoom.class );
            floorSpecials.remove( VaultRoom.class );
            floorSpecials.remove( WeakFloorRoom.class );

            return new PitRoom();

        } else if (Dungeon.depth >= guaranteedWellDepth) {
            useType( MagicWellRoom.class );

            MagicWellRoom r = new MagicWellRoom();
            r.overrideWater = WaterOfTransmutation.class;
            guaranteedWellDepth = Integer.MAX_VALUE;
            return r;

        } else {

            if (Dungeon.bossLevel(Dungeon.depth + 1)){
                floorSpecials.remove(WeakFloorRoom.class);
            }

            Room r = null;
            int index = floorSpecials.size();
            for (int i = 0; i < 4; i++){
                int newidx = Random.Int( floorSpecials.size() );
                if (newidx < index) index = newidx;
            }
            try {
                r = floorSpecials.get( index ).newInstance();
            } catch (Exception e) {
                PixelDungeonOfTeller.reportException(e);
            }

            if (r instanceof WeakFloorRoom){
                pitNeededDepth = Dungeon.depth + 1;
            }

            useType( r.getClass() );
            return (SpecialRoom)r;

        }
    }

    private static final String ROOMS	= "special_rooms";
    private static final String PIT	    = "pit_needed";
    private static final String WELL    = "guaranteed_well";

    public static void restoreRoomsFromBundle( Bundle bundle ) {
        runSpecials.clear();
        if (bundle.contains( ROOMS )) {
            for (Class<? extends Room> type : bundle.getClassArray(ROOMS)) {
                if (type != null) runSpecials.add(type);
            }
        } else {
            initForRun();
            PixelDungeonOfTeller.reportException(new Exception("specials array didn't exist!"));
        }
        pitNeededDepth = bundle.getInt(PIT);
        guaranteedWellDepth = bundle.getInt(WELL);
    }

    public static void storeRoomsInBundle( Bundle bundle ) {
        bundle.put( ROOMS, runSpecials.toArray(new Class[0]) );
        bundle.put( PIT, pitNeededDepth );
        bundle.put( WELL, guaranteedWellDepth );
    }
}