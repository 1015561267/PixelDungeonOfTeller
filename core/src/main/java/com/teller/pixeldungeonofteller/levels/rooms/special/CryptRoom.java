package com.teller.pixeldungeonofteller.levels.rooms.special;

import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.items.Generator;
import com.teller.pixeldungeonofteller.items.Heap;
import com.teller.pixeldungeonofteller.items.Item;
import com.teller.pixeldungeonofteller.items.armor.Armor;
import com.teller.pixeldungeonofteller.items.keys.IronKey;
import com.teller.pixeldungeonofteller.levels.Level;
import com.teller.pixeldungeonofteller.levels.Terrain;
import com.teller.pixeldungeonofteller.levels.painters.Painter;
import com.watabou.utils.Point;

public class CryptRoom extends SpecialRoom {

    public void paint( Level level ) {

        Painter.fill( level, this, Terrain.WALL );
        Painter.fill( level, this, 1, Terrain.EMPTY );

        Point c = center();
        int cx = c.x;
        int cy = c.y;

        Door entrance = entrance();

        entrance.set( Door.Type.LOCKED );
        level.addItemToSpawn( new IronKey( Dungeon.depth ) );

        if (entrance.x == left) {
            Painter.set( level, new Point( right-1, top+1 ), Terrain.STATUE );
            Painter.set( level, new Point( right-1, bottom-1 ), Terrain.STATUE );
            cx = right - 2;
        } else if (entrance.x == right) {
            Painter.set( level, new Point( left+1, top+1 ), Terrain.STATUE );
            Painter.set( level, new Point( left+1, bottom-1 ), Terrain.STATUE );
            cx = left + 2;
        } else if (entrance.y == top) {
            Painter.set( level, new Point( left+1, bottom-1 ), Terrain.STATUE );
            Painter.set( level, new Point( right-1, bottom-1 ), Terrain.STATUE );
            cy = bottom - 2;
        } else if (entrance.y == bottom) {
            Painter.set( level, new Point( left+1, top+1 ), Terrain.STATUE );
            Painter.set( level, new Point( right-1, top+1 ), Terrain.STATUE );
            cy = top + 2;
        }

        level.drop( prize( level ), cx + cy * level.width() ).type = Heap.Type.TOMB;
    }

    private static Item prize(Level level ) {

        //1 floor set higher than normal
        Armor prize = Generator.randomArmor( (Dungeon.depth / 5) + 1);

        //if it isn't already cursed, give it a free upgrade
        if (!prize.cursed){
            prize.upgrade();
            //curse the armor, unless it has a glyph
            if (!prize.hasGoodGlyph()){
                prize.cursed = prize.cursedKnown = true;
                prize.inscribe(Armor.Glyph.randomCurse());
            }
        }

        return prize;
    }
}
