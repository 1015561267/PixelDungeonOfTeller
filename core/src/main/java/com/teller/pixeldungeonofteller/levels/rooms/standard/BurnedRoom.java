package com.teller.pixeldungeonofteller.levels.rooms.standard;

import com.teller.pixeldungeonofteller.levels.Level;
import com.teller.pixeldungeonofteller.levels.Terrain;
import com.teller.pixeldungeonofteller.levels.painters.Painter;
import com.teller.pixeldungeonofteller.levels.traps.FireTrap;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class BurnedRoom extends PatchRoom {

    @Override
    public float[] sizeCatProbs() {
        return new float[]{4, 1, 0};
    }

    @Override
    public void paint(Level level) {
        Painter.fill( level, this, Terrain.WALL );
        Painter.fill( level, this, 1, Terrain.EMPTY );
        for (Door door : connected.values()) {
            door.set( Door.Type.REGULAR );
        }

        //past 8x8 each point of width/height decreases fill by 3%
        // e.g. a 14x14 burned room has a fill of 54%
        float fill = Math.min( 1f, 1.48f - (width()+height())*0.03f);
        setupPatch(level, fill, 2, false );

        for (int i=top + 1; i < bottom; i++) {
            for (int j=left + 1; j < right; j++) {
                if (!patch[xyToPatchCoords(j, i)])
                    continue;
                int cell = i * level.width() + j;
                int t;
                switch (Random.Int( 5 )) {
                    case 0: default:
                        t = Terrain.EMPTY;
                        break;
                    case 1:
                        t = Terrain.EMBERS;
                        break;
                    case 2:
                        t = Terrain.TRAP;
                        level.setTrap(new FireTrap().reveal(), cell);
                        break;
                    case 3:
                        t = Terrain.SECRET_TRAP;
                        level.setTrap(new FireTrap().hide(), cell);
                        break;
                    case 4:
                        t = Terrain.INACTIVE_TRAP;
                        FireTrap trap = new FireTrap();
                        trap.reveal().active = false;
                        level.setTrap(trap, cell);
                        break;
                }
                level.map[cell] = t;
            }
        }
    }

    @Override
    public boolean canModifyTerrain(Point p) {
        return super.canModifyTerrain(p) && !patch[xyToPatchCoords(p.x, p.y)];
    }

    @Override
    public boolean canPlaceTrap(Point p) {
        return super.canPlaceTrap(p) && !patch[xyToPatchCoords(p.x, p.y)];
    }

}