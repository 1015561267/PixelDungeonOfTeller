package com.teller.pixeldungeonofteller.journal;

import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.items.keys.Key;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Collections;

public class Notes {
    public static abstract class Record implements Comparable<Record>, Bundlable {

        protected int depth;

        public int depth(){
            return depth;
        }

        public abstract String desc();

        @Override
        public abstract boolean equals(Object obj);

        @Override
        public int compareTo( Record another ) {
            return another.depth() - depth();
        }

        private static final String DEPTH	= "depth";

        @Override
        public void restoreFromBundle( Bundle bundle ) {
            depth = bundle.getInt( DEPTH );
        }

        @Override
        public void storeInBundle( Bundle bundle ) {
            bundle.put( DEPTH, depth );
        }
    }

    public enum Landmark {
        WELL_OF_HEALTH,
        WELL_OF_AWARENESS,
        WELL_OF_TRANSMUTATION,
        ALCHEMY,
        GARDEN,
        STATUE,

        GHOST,
        WANDMAKER,
        TROLL,
        IMP;

        public String desc() {
            return Messages.get(this, name());
        }
    }

    public static class LandmarkRecord extends Record {

        protected Landmark landmark;

        public LandmarkRecord() {}

        public LandmarkRecord(Landmark landmark, int depth ) {
            this.landmark = landmark;
            this.depth = depth;
        }

        @Override
        public String desc() {
            return landmark.desc();
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof LandmarkRecord)
                    && landmark == ((LandmarkRecord) obj).landmark
                    && depth() == ((LandmarkRecord) obj).depth();
        }

        private static final String LANDMARK	= "landmark";

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            landmark = Landmark.valueOf(bundle.getString(LANDMARK));
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put( LANDMARK, landmark.toString() );
        }
    }

    public static class KeyRecord extends Record {

        protected Key key;

        public KeyRecord() {}

        public KeyRecord( Key key ){
            this.key = key;
        }

        @Override
        public int depth() {
            return key.depth;
        }

        @Override
        public String desc() {
            return key.toString();
        }

        public Class<? extends Key> type(){
            return key.getClass();
        }

        public int quantity(){
            return key.quantity();
        }

        public void quantity(int num){
            key.quantity(num);
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof KeyRecord)
                    && key.isSimilar(((KeyRecord) obj).key);
        }

        private static final String KEY	= "key";

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            key = (Key) bundle.get(KEY);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put( KEY, key );
        }
    }

    private static ArrayList<Record> records;

    public static void reset() {
        records = new ArrayList<>();
    }

    private static final String RECORDS	= "records";

    public static void storeInBundle( Bundle bundle ) {
        bundle.put( RECORDS, records );
    }

    public static void restoreFromBundle( Bundle bundle ) {
        records = new ArrayList<>();
        for (Bundlable rec : bundle.getCollection( RECORDS ) ) {
            records.add( (Record) rec );
        }
    }

    public static void add( Landmark landmark ) {
        LandmarkRecord l = new LandmarkRecord( landmark, Dungeon.depth );
        if (!records.contains(l)) {
            records.add(new LandmarkRecord(landmark, Dungeon.depth));
            Collections.sort(records);
        }
    }

    public static void remove( Landmark landmark ) {
        records.remove( new LandmarkRecord(landmark, Dungeon.depth) );
    }

    public static void add( Key key ){
        KeyRecord k = new KeyRecord(key);
        if (!records.contains(k)){
            records.add(k);
            Collections.sort(records);
        } else {
            k = (KeyRecord) records.get(records.indexOf(k));
            k.quantity(k.quantity() + key.quantity());
        }
    }

    public static void remove( Key key ){
        KeyRecord k = new KeyRecord( key );
        if (records.contains(k)){
            k = (KeyRecord) records.get(records.indexOf(k));
            k.quantity(k.quantity() - key.quantity());
            if (k.quantity() <= 0){
                records.remove(k);
            }
        }
    }

    public static int keyCount( Key key ){
        KeyRecord k = new KeyRecord( key );
        if (records.contains(k)){
            k = (KeyRecord) records.get(records.indexOf(k));
            return k.quantity();
        } else {
            return 0;
        }
    }

    public static ArrayList<Record> getRecords(){
        return getRecords(Record.class);
    }

    public static <T extends Record> ArrayList<T> getRecords(Class<T> recordType ){
        ArrayList<T> filtered = new ArrayList<>();
        for (Record rec : records){
            if (recordType.isInstance(rec)){
                filtered.add((T)rec);
            }
        }
        return filtered;
    }

    public static void remove( Record rec ){
        records.remove(rec);
    }

}
