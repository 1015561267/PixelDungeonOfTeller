package com.teller.pixeldungeonofteller.levels.painters;

import com.teller.pixeldungeonofteller.levels.Level;
import com.teller.pixeldungeonofteller.levels.Terrain;
import com.teller.pixeldungeonofteller.levels.rooms.Room;
import com.teller.pixeldungeonofteller.levels.rooms.connection.ConnectionRoom;
import com.teller.pixeldungeonofteller.levels.rooms.standard.CaveRoom;
import com.teller.pixeldungeonofteller.levels.rooms.standard.EmptyRoom;
import com.teller.pixeldungeonofteller.levels.rooms.standard.StandardRoom;
import com.teller.pixeldungeonofteller.tiles.DungeonTileSheet;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

import java.util.ArrayList;

public class CavesPainter extends RegularPainter {

    @Override
    protected void decorate(Level level, ArrayList<Room> rooms) {

        int w = level.width();
        int l = level.length();
        int[] map = level.map;

        for (Room room : rooms) {
            if (!(room instanceof EmptyRoom || room instanceof CaveRoom)) {
                continue;
            }

            if (room.width() <= 4 || room.height() <= 4) {
                continue;
            }

            int s = room.square();

            if (Random.Int( s ) > 8) {
                int corner = (room.left + 1) + (room.top + 1) * w;
                if (map[corner - 1] == Terrain.WALL && map[corner - w] == Terrain.WALL) {
                    map[corner] = Terrain.WALL;
                    level.traps.remove(corner);
                }
            }

            if (Random.Int( s ) > 8) {
                int corner = (room.right - 1) + (room.top + 1) * w;
                if (map[corner + 1] == Terrain.WALL && map[corner - w] == Terrain.WALL) {
                    map[corner] = Terrain.WALL;
                    level.traps.remove(corner);
                }
            }

            if (Random.Int( s ) > 8) {
                int corner = (room.left + 1) + (room.bottom - 1) * w;
                if (map[corner - 1] == Terrain.WALL && map[corner + w] == Terrain.WALL) {
                    map[corner] = Terrain.WALL;
                    level.traps.remove(corner);
                }
            }

            if (Random.Int( s ) > 8) {
                int corner = (room.right - 1) + (room.bottom - 1) * w;
                if (map[corner + 1] == Terrain.WALL && map[corner + w] == Terrain.WALL) {
                    map[corner] = Terrain.WALL;
                    level.traps.remove(corner);
                }
            }

            for (Room n : room.connected.keySet()) {
                if ((n instanceof StandardRoom || n instanceof ConnectionRoom) && Random.Int( 3 ) == 0) {
                    Painter.set( level, room.connected.get( n ), Terrain.EMPTY_DECO );
                }
            }
        }

        for (int i=w + 1; i < l - w; i++) {
            if (map[i] == Terrain.EMPTY) {
                int n = 0;
                if (map[i+1] == Terrain.WALL) {
                    n++;
                }
                if (map[i-1] == Terrain.WALL) {
                    n++;
                }
                if (map[i+w] == Terrain.WALL) {
                    n++;
                }
                if (map[i-w] == Terrain.WALL) {
                    n++;
                }
                if (Random.Int( 6 ) <= n) {
                    map[i] = Terrain.EMPTY_DECO;
                }
            }
        }

        for (int i=0; i < l - w; i++) {
            if (map[i] == Terrain.WALL &&
                    DungeonTileSheet.floorTile(map[i + w])
                    && Random.Int( 4 ) == 0) {
                map[i] = Terrain.WALL_DECO;
            }
        }

        for (Room r : rooms) {
            if (r instanceof EmptyRoom) {
                for (Room n : r.neigbours) {
                    if (n instanceof EmptyRoom && !r.connected.containsKey( n )) {
                        Rect i = r.intersect( n );
                        if (i.left == i.right && i.bottom - i.top >= 5) {

                            i.top += 2;
                            i.bottom -= 1;

                            i.right++;

                            Painter.fill( level, i.left, i.top, 1, i.height(), Terrain.CHASM );

                        } else if (i.top == i.bottom && i.right - i.left >= 5) {

                            i.left += 2;
                            i.right -= 1;

                            i.bottom++;

                            Painter.fill( level, i.left, i.top, i.width(), 1, Terrain.CHASM );
                        }
                    }
                }
            }
        }
    }
}

