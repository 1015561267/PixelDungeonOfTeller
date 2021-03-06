package com.teller.pixeldungeonofteller.levels.rooms.special;

import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.items.Generator;
import com.teller.pixeldungeonofteller.items.Item;
import com.teller.pixeldungeonofteller.items.keys.IronKey;
import com.teller.pixeldungeonofteller.items.scrolls.Scroll;
import com.teller.pixeldungeonofteller.items.scrolls.ScrollOfIdentify;
import com.teller.pixeldungeonofteller.items.scrolls.ScrollOfRemoveCurse;
import com.teller.pixeldungeonofteller.levels.Level;
import com.teller.pixeldungeonofteller.levels.Terrain;
import com.teller.pixeldungeonofteller.levels.painters.Painter;
import com.watabou.utils.Random;

public class LibraryRoom extends SpecialRoom {

    public void paint( Level level ) {

        Painter.fill( level, this, Terrain.WALL );
        Painter.fill( level, this, 1, Terrain.EMPTY_SP );

        Door entrance = entrance();

        Painter.fill( level, left + 1, top+1, width() - 2, 1 , Terrain.BOOKSHELF );
        Painter.drawInside(level, this, entrance, 1, Terrain.EMPTY_SP );

        int n = Random.IntRange( 2, 3 );
        for (int i=0; i < n; i++) {
            int pos;
            do {
                pos = level.pointToCell(random());
            } while (level.map[pos] != Terrain.EMPTY_SP || level.heaps.get( pos ) != null);
            Item item;
            if (i == 0)
                item = Random.Int(2) == 0 ? new ScrollOfIdentify() : new ScrollOfRemoveCurse();
            else
                item = prize( level );
            level.drop( item, pos );
        }

        entrance.set( Door.Type.LOCKED );

        level.addItemToSpawn( new IronKey( Dungeon.depth ) );
    }

    private static Item prize( Level level ) {

        Item prize = level.findPrizeItem( Scroll.class );
        if (prize == null)
            prize = Generator.random( Generator.Category.SCROLL );

        return prize;
    }
}