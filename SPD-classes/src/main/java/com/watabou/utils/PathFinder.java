/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
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

package com.watabou.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class PathFinder {

    public static int[] distance;
    private static int[] maxVal;
    private static int[][] largeMaxVal;

    private static boolean[] goals;
    private static int[] queue;

    private static int stop[][]; //use this to locate ice stop pos for current condition,avoid finding distination again in same round.
    //FIXME This can be improved further by remember the state is whether 'dirty' or not , to save more time

    private static int size = 0;
    private static int width = 0;

    private static int[] dir;
    private static int[] dirLR;
    private static ArrayList<Integer> toAdd;

    //performance-light shortcuts for some common pathfinder cases
    //they are in array-access order for increased memory performance
    public static int[] NEIGHBOURS4;
    public static int[] NEIGHBOURS8;
    public static int[] NEIGHBOURS9;
    public static int[] NEIGHBOURS24;

    public static ArrayList<Integer> ADJUSTEDNEIGHBOURS8;//Add this to make pathfinding with ice consider straight direction ,then diagonal.At least hope the performance to be less strange

    public static int[]CUSTOMCONE;//Add this to handle handconnon and to come shotgun in a easier and simple way

    //similar to their equivalent neighbour arrays, but the order is clockwise.
    //Useful for some logic functions, but is slower due to lack of array-access order.
    public static int[] CIRCLE4;
    public static int[] CIRCLE8;

    public static void setMapSize( int width, int height ) {

        PathFinder.width = width;
        PathFinder.size = width * height;

        distance = new int[size];
        goals = new boolean[size];
        queue = new int[size];

        stop = new int[size][8];

        maxVal = new int[size];
        largeMaxVal = new int[size][8];

        Arrays.fill(maxVal, Integer.MAX_VALUE);

        for(int i = 0 ; i < largeMaxVal.length;i++)
        {
            Arrays.fill(largeMaxVal[i],Integer.MAX_VALUE);
        }

        toAdd = new ArrayList<Integer>();

        dir = new int[]{-1, +1, -width, +width, -width-1, -width+1, +width-1, +width+1};
        dirLR = new int[]{-1-width, -1, -1+width, -width, +width, +1-width, +1, +1+width};

        NEIGHBOURS4 = new int[]{-width, -1, +1, +width};
        NEIGHBOURS8 = new int[]{-width-1, -width, -width+1, -1, +1, +width-1, +width, +width+1};
        NEIGHBOURS9 = new int[]{-width-1, -width, -width+1, -1, 0, +1, +width-1, +width, +width+1};
        NEIGHBOURS24= new int[]{-2*width+2,-2*width+1,-2*width,-2*width-1,-2*width-2,-width+2,-width+1,-width,-width-1,-width-2,+2,+1,-1,-2,width+2,width+1,width,width-1,width-2,2*width+2,2*width+1,2*width,2*width-1,2*width-2};

        final Integer w = width;
        ADJUSTEDNEIGHBOURS8 = new ArrayList<Integer>() { {add(-w); add(+w); add(-1); add(+1); add(-w-1); add(-w+1); add(+w-1); add(+w+1); } };

        //-w ,+ w , -1 , +1 , -w-1 , -w+1 , +w-1 , +w+1

        CIRCLE4 = new int[]{-width, +1, +width, -1};
        CIRCLE8 = new int[]{-width-1, -width, -width+1, +1, +width+1, +width, +width-1, -1};


        CUSTOMCONE = new int[]{//note that the most far block would contain the block next to hero when use ballistica
                -width-3 , -width-2 , -2*width-2 , -2*width-1 , -3*width-1,

                -width-1 , -width+1 , -2*width-1, -2*width , -2*width+1,

                -width+2 , -width+3 , -2*width+1 , -2*width+2 , -3*width+1,

                -width-2 , -width-1 , -2 , +width-2 , +width-1,

                -width+1 , -width+2 , +2 , +width+1 , +width+2,

                +width-3 , +width-2 , +2*width-2 , +2*width-1 , +3*width-1,

                +width-1 , +width+1 , +2*width-1, +2*width , +2*width+1,

                +width+2 , +width+3 , +2*width+1 , +2*width+2 , +3*width+1,
        };

        //FIXME It's damn stupid but I can not think up a easy data struct to handle 2d key  :(

    }

    public static int getStopPos(int ori,int from, int to, boolean[] passable, boolean[] ice) //FIXME There might be redundant code,might polish that later
    {

        int i = ADJUSTEDNEIGHBOURS8.indexOf(to - from);

        if(i!=-1 && stop[from][i] < Integer.MAX_VALUE)
        {
            //Log.i("Have located", String.valueOf(from) + " " + String.valueOf(stop[from]));
            return stop[from][i];
        }

        if(!passable[to]) {
            //Log.i("Blocked", String.valueOf(from) );
            return from;
        }
        if(!ice[to])
        {
            //Log.i("Solid", String.valueOf(to) );
            return to;
        }


        if(to >= 0 && to < size) {
            int newpos = to + (to - from);

            if(newpos == ori)
            {
                //Log.i("Get target", String.valueOf(newpos) );
                return newpos;
            }

            if (newpos >= 0 && newpos < size) {
                if (!passable[newpos]) {
                    //Log.i("Unpassable", String.valueOf(to));
                    return to;
                }//I forget the issuses that player is not removed in passable,so when look back from stop to start,player self would block it,and make it stop before the real pos
                //2021-5-26 After counfused for about 4 hours then realize this,feels like I'm nothing but a really idiot
                else if (!ice[newpos])
                {
                    //Log.i("Landed", String.valueOf(newpos));
                    return newpos;
                }
                else {
                    //Log.i("Continue", String.valueOf(ori) + " "  + String.valueOf(from) + " " + String.valueOf(to) + " " + String.valueOf(newpos) + " ");

                    int result =  getStopPos(ori, to, newpos, passable, ice);

                    //Log.i("Result", String.valueOf(result));

                    return result;
                }
            }
            else
                //Log.i("Out of size", String.valueOf(to));
            return to;
        }
        return from;
    }


    public static int getStep( int from, int to, boolean[] passable , boolean[] ice , boolean ignoreIce) {

        Path result = findPathWithIce(from,to,passable,ice,ignoreIce);

        if(result==null || result.isEmpty())
        {
            return -1;
        }
        else return result.removeFirst();
    }

    public static int getStepBack( int cur, int from, boolean[] passable ) {

        int d = buildEscapeDistanceMap( cur, from, 2f, passable );
        for (int i=0; i < size; i++) {
            goals[i] = distance[i] == d;
        }
        if (!buildDistanceMap( cur, goals, passable )) {
            return -1;
        }

        int s = cur;

        // From the starting position we are making one step downwards
        int minD = distance[s];
        int mins = s;

        for (int i=0; i < dir.length; i++) {

            int n = s + dir[i];
            int thisD = distance[n];

            if (thisD < minD) {
                minD = thisD;
                mins = n;
            }
        }

        return mins;
    }

    //FIXME After adding ice floor,mobs' auto-path-finding becomes stupid,as the ice change the rule of distance,I'm trying to fix it but can't count too much on it
    /*private static boolean buildDistanceMap( int from, int to, boolean[] passable ,boolean[] ice , boolean ignoreIce ) {

        if (from == to) {
            return false;
        }

        System.arraycopy(maxVal, 0, distance, 0, maxVal.length);

        boolean pathFound = false;

        int head = 0;
        int tail = 0;

        // Add to queue
        queue[tail++] = to;
        distance[to] = 0;

        while (head < tail) {

            // Remove from queue
            int step = queue[head++];
            if (step == from) {
                pathFound = true;
                break;
            }

            int nextDistance = distance[step] + 1;

            int start = (step % width == 0 ? 3 : 0);//if this pos happen to place on the left side,then can not add former 3(no -1)
            int stop   = ((step+1) % width == 0 ? 3 : 0);//similar,but on the right side(no +1)

            for (int i = start; i < dirLR.length - stop; i++) {

                int n = step + dirLR[i];

                if(!ignoreIce && ice[n])
                {
                    n = getStopPos(step,n,passable,ice);
                }

                if (n == from || (n >= 0 && n < size && passable[n] && (distance[n] > nextDistance))) {
                    // Add to queue
                    queue[tail++] = n;
                    distance[n] = nextDistance;
                }
            }
        }

        return pathFound;
    }*/

    public static void buildDistanceMap( int to, boolean[] passable, int limit ) {

        System.arraycopy(maxVal, 0, distance, 0, maxVal.length);

        int head = 0;
        int tail = 0;

        // Add to queue
        queue[tail++] = to;
        distance[to] = 0;

        while (head < tail) {

            // Remove from queue
            int step = queue[head++];

            int nextDistance = distance[step] + 1;
            if (nextDistance > limit) {
                return;
            }

            int start = (step % width == 0 ? 3 : 0);
            int end   = ((step+1) % width == 0 ? 3 : 0);
            for (int i = start; i < dirLR.length - end; i++) {

                int n = step + dirLR[i];
                if (n >= 0 && n < size && passable[n] && (distance[n] > nextDistance)) {
                    // Add to queue
                    queue[tail++] = n;
                    distance[n] = nextDistance;
                }

            }
        }
    }

    private static boolean buildDistanceMap( int from, boolean[] to, boolean[] passable ) {

        if (to[from]) {
            return false;
        }

        System.arraycopy(maxVal, 0, distance, 0, maxVal.length);

        boolean pathFound = false;

        int head = 0;
        int tail = 0;

        // Add to queue
        for (int i=0; i < size; i++) {
            if (to[i]) {
                queue[tail++] = i;
                distance[i] = 0;
            }
        }

        while (head < tail) {

            // Remove from queue
            int step = queue[head++];
            if (step == from) {
                pathFound = true;
                break;
            }
            int nextDistance = distance[step] + 1;

            int start = (step % width == 0 ? 3 : 0);
            int end   = ((step+1) % width == 0 ? 3 : 0);
            for (int i = start; i < dirLR.length - end; i++) {

                int n = step + dirLR[i];
                if (n == from || (n >= 0 && n < size && passable[n] && (distance[n] > nextDistance))) {
                    // Add to queue
                    queue[tail++] = n;
                    distance[n] = nextDistance;
                }

            }
        }

        return pathFound;
    }

    private static int buildEscapeDistanceMap( int cur, int from, float factor, boolean[] passable ) {

        System.arraycopy(maxVal, 0, distance, 0, maxVal.length);

        int destDist = Integer.MAX_VALUE;

        int head = 0;
        int tail = 0;

        // Add to queue
        queue[tail++] = from;
        distance[from] = 0;

        int dist = 0;

        while (head < tail) {

            // Remove from queue
            int step = queue[head++];
            dist = distance[step];

            if (dist > destDist) {
                return destDist;
            }

            if (step == cur) {
                destDist = (int)(dist * factor) + 1;
            }

            int nextDistance = dist + 1;

            int start = (step % width == 0 ? 3 : 0);
            int end   = ((step+1) % width == 0 ? 3 : 0);
            for (int i = start; i < dirLR.length - end; i++) {

                int n = step + dirLR[i];
                if (n >= 0 && n < size && passable[n] && distance[n] > nextDistance) {
                    // Add to queue
                    queue[tail++] = n;
                    distance[n] = nextDistance;
                }

            }
        }

        return dist;
    }

    public static void buildDistanceMap( int to, boolean[] passable ) {

        System.arraycopy(maxVal, 0, distance, 0, maxVal.length);

        int head = 0;
        int tail = 0;

        // Add to queue
        queue[tail++] = to;
        distance[to] = 0;

        while (head < tail) {

            // Remove from queue
            int step = queue[head++];
            int nextDistance = distance[step] + 1;


            int start = (step % width == 0 ? 3 : 0);
            int end   = ((step+1) % width == 0 ? 3 : 0);
            for (int i = start; i < dirLR.length - end; i++) {

                int n = step + dirLR[i];
                if (n >= 0 && n < size && passable[n] && (distance[n] > nextDistance)) {
                    // Add to queue
                    queue[tail++] = n;
                    distance[n] = nextDistance;
                }

            }
        }
    }

    @SuppressWarnings("serial")
    public static class Path extends LinkedList<Integer> {
    }



    public static Path findPathWithIce(int from, int to, boolean[] passable , boolean[] ice , boolean ignoreIce)
    // this is to avoid some condition like this:
    // Wall   Wall   Wall
    // Wall   Ice    Empty
    // Wall   Ice    Empty
    // Wall   Empty  Empty
    // In this case,from (4,2) to (3,2) lower ice,it's available from (4,2) - (3,3) - (3,2) but different from reserve direction:(3,2) - (4,2)
    {
        if(!buildDistanceMapWithIce( from, to, passable , ice ,ignoreIce))//first we build a distance from start to stop
        {return null;}

        Path attempt = new Path();

        int s = to;//then we start from target pos,every step try to find step with -1 distance value,plus with that,we need to verify if it's available from both side,if not,then dismiss it

        int round = 0;

        do {//somehow like the original way,but with reserve direction.And we have to verify if each step with ice is available in the direction of from start to stop
            int minD = distance[s];

            int required = minD - 1;

            int mins = s;

            round++;
            if(round++>300)
            {
                System.out.println( from + " " + to + " " + s + " " + minD);
                round /= 0 ;
            }

            //Log.i("Map from", String.valueOf(from) );
            //Log.i("Map to", String.valueOf(to) );


            for (int i = 0 ; i < dir.length; i++) {
                int n = s + dir[i];

                int actualStep = n;
                boolean needActual = false;

                if(!ignoreIce && ice[n] && passable[n])
                {
                    //Log.i("Ignore", "No" );
                    if(!verifyDirection(s,n,passable,ice)) {
                        //Log.i("verify", "fail");
                        continue;//if verify function found it's different for two direction,then it means this tile is unavailable from start to stop,then it's abandoned
                    }
                    else {
                        n = getStopPos(from, s, n, passable, ice);
                        stop[s][i] = n;
                        //Log.i("Stop", String.valueOf(n));
                        needActual = true;
                    }
                }

                int thisD = distance[n];

                //Log.i("This", String.valueOf(thisD));
                //Log.i("Raw", String.valueOf(minD));

                if (thisD == minD - 1) {
                    s = n;

                    if(needActual)
                    {
                        //Log.i("Actual S", String.valueOf(s) );
                        //Log.i("Actual mins", String.valueOf(mins) );
                        //Log.i("Actual step", String.valueOf(actualStep) );

                        actualStep = s - (actualStep - mins);
                        //Log.i("Actual Step", String.valueOf(actualStep) );
                        attempt.add( actualStep );
                        break;
                    }
                    else{
                        //Log.i("S added", String.valueOf(s) );
                        attempt.add( s );
                        break;
                    }
                }
            }

        } while (s != from);

        if(attempt!=null && !attempt.isEmpty()) {//this path is from stop to start,so have to reserve it
            PathFinder.Path result = new PathFinder.Path();
            result.clear();

            if(!attempt.isEmpty()) attempt.removeLast();

            while (!attempt.isEmpty()) {
                int x = attempt.removeLast();
                result.add(x);
                //Log.i("Added", String.valueOf(x) );
            }

            result.addLast(to);
            //Log.i("AddLast", String.valueOf(to) );

            return result;
        }
        else return attempt;
    }

    private static boolean buildDistanceMapWithIce( int from, int to, boolean[] passable ,boolean[] ice , boolean ignoreIce ) {//mostly like raw function only reserve start and stop point,but I don't want to mess with old things

        if (from == to) {
            return false;
        }

        System.arraycopy(maxVal, 0, distance, 0, maxVal.length);

        System.arraycopy(largeMaxVal, 0, stop, 0, stop.length);

        boolean pathFound = false;

        int head = 0;
        int tail = 0;

        queue[tail++] = from;
        distance[from] = 0;

        while (head < tail) {
            int step = queue[head++];
            if (step == to) {
                pathFound = true;
                break;
            }

            int nextDistance = distance[step] + 1;

            int start = (step % width == 0 ? 3 : 0);
            int end   = ((step+1) % width == 0 ? 3 : 0);

            for (int i = start; i < dir.length - end; i++) {

                int n = step + dir[i];

                if(!ignoreIce && ice[n] && passable[n])
                {
                    n = getStopPos(Integer.MIN_VALUE,step,n,passable,ice);
                    stop[step][i] = n;
                }

                if (n == to || (n >= 0 && n < size && passable[n] && (distance[n] > nextDistance))) {
                    queue[tail++] = n;
                    distance[n] = nextDistance;
                }
            }
        }

        return pathFound;
    }

    public static boolean verifyDirection(int from,int to,boolean[] passable ,boolean[] ice)
    {
        if(!ice[from]) return true;
        else if(!passable[from + (from - to )]) return true;
        else return false;
    }
}
