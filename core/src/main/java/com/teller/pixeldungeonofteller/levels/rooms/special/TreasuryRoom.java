package com.teller.pixeldungeonofteller.levels.rooms.special;

import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.items.Gold;
import com.teller.pixeldungeonofteller.items.Heap;
import com.teller.pixeldungeonofteller.items.keys.IronKey;
import com.teller.pixeldungeonofteller.levels.Level;
import com.teller.pixeldungeonofteller.levels.Terrain;
import com.teller.pixeldungeonofteller.levels.painters.Painter;
import com.watabou.utils.Random;

public class TreasuryRoom extends SpecialRoom {

    public void paint( Level level ) {

        Painter.fill( level, this, Terrain.WALL );
        Painter.fill( level, this, 1, Terrain.EMPTY );

        Painter.set( level, center(), Terrain.STATUE );

        Heap.Type heapType = Random.Int( 2 ) == 0 ? Heap.Type.CHEST : Heap.Type.HEAP;

        int n = Random.IntRange( 2, 3 );
        for (int i=0; i < n; i++) {
            int pos;
            do {
                pos = level.pointToCell(random());
            } while (level.map[pos] != Terrain.EMPTY || level.heaps.get( pos ) != null);
            level.drop( new Gold().random(), pos ).type = (Random.Int(20) == 0 && heapType == Heap.Type.CHEST ? Heap.Type.MIMIC : heapType);
        }

        if (heapType == Heap.Type.HEAP) {
            for (int i=0; i < 6; i++) {
                int pos;
                do {
                    pos = level.pointToCell(random());
                } while (level.map[pos] != Terrain.EMPTY);
                level.drop( new Gold( Random.IntRange( 5, 12 ) ), pos );
            }
        }

        entrance().set( Door.Type.LOCKED );
        level.addItemToSpawn( new IronKey( Dungeon.depth ) );
    }
}