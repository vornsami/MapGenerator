/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import consepts.Pair;
import consepts.Region;

/**
 *
 * @author Sami
 */
public class RegionDivider {
    
    public static List<Region> divide(double[][] map) {
        
        ArrayList<Region> regs = new ArrayList<>();
        boolean[][] boolMap = new boolean[map.length][map[0].length];
        
        for (boolean[] boolMap1 : boolMap) {
            for (int j = 0; j < boolMap[0].length; j++) {
                boolMap1[j] = false;
            }
        }
        
        // setup adjacents
        
        List<Pair<Region, List<Pair<Integer,Integer>>>> adjacents = new ArrayList<>();
        
        // add continent centres
        
        int contCount = 20;
        
        for (int i = 0; i < contCount; i++) {
            int x = 1 + (int) (Math.random() * map.length - 1);
            int y = 1 + (int) (Math.random() * map[0].length - 1);
            
            Region r = new Region();
            r.addTile(x, y);
            regs.add(r);
            boolMap[x][y] = true;
            ArrayList<Pair<Integer,Integer>> adj = new ArrayList<>();
            
            addAdjacents(map,adj,new Pair(x,y));
            
            adjacents.add(new Pair<>(r, adj));
        }
        
        // loop
        
        while(true){
            if (adjacents.isEmpty()) break;
            int l = (int) (Math.random() * (adjacents.size()));
            if(l == adjacents.size()) l = adjacents.size() - 1;
            
            List<Pair<Integer,Integer>> adjs = adjacents.get(l).getY();
            
            if (adjs.isEmpty()) {
                adjacents.remove(l);
                continue;
            } 
            
            while (adjs.size() > 0) {
                
                int p = (int) (Math.random() * (adjs.size()));
                if(p == adjs.size()) p = adjs.size() - 1;
                int x = adjs.get(p).getX();
                int y = adjs.get(p).getY();
                if (!boolMap[x][y]) {
                    boolMap[x][y] = true;
                    adjacents.get(l).getX().addTile(x, y);
                    addAdjacents(map,adjs,adjs.get(p));
                    adjs.remove(p);
                    break;
                }
                adjs.remove(p);
            }
        }
        System.out.println("Merging regions...");
        while(mergeLongestBorderRegions(map, regs) > map.length / 1.4 && regs.size() > contCount / 2);
        System.out.println(contCount + " -> "+regs.size());
        
        return regs;
    }
    
    private static void addAdjacents(double[][] map, List<Pair<Integer,Integer>> adjs, Pair<Integer,Integer> tile){
        int x = tile.getX();
        int y = tile.getY();
        
        for (int a = -1; a <= 1; a++) {
            for (int b = 0; b <= 1; b+=2) {
                if (a == 0 && b == 0) b = -1;
                
                if(     
                        x + a < map.length && 
                        x + a >= 0 && 
                        y + b < map[0].length && 
                        y + b >= 0
                        ) {
                    adjs.add(new Pair(x + a, y + b));
                
                } else if (
                        (   
                            x + a >= map.length || 
                            x + a < 0
                        ) && 
                        y + b < map[0].length && 
                        y + b >= 0
                        ) {
                    if(x > 0) {
                        adjs.add(new Pair(0, y + b));
                    } else {
                        adjs.add(new Pair(map.length - 1, y + b));
                    }
                }
            }
        }
    }
    public static int mergeLongestBorderRegions(double[][] map, List<Region> regions) {
        int[][] regionMap = createRegionMap(map, regions);
        
        for (int i = 0; i < regions.size(); i++) {
            for(Pair<Integer,Integer> tile : regions.get(i).getTiles()) {
                regionMap[tile.getX()][tile.getY()] = i;
            }
        }
        
        int[][] borderCount = new int[regions.size()][regions.size()];
        
        for (int x = 0; x < regionMap.length; x++) {
            for (int y = 0; y < regionMap[0].length; y++) {
                
                for (int ix = -1; ix <= 1; ix++) {
                    for (int iy = 0; iy <= 1; iy +=2) {
                        if (ix == iy) iy = -1;
                        
                        if (x + ix >= 0 && x + ix < regionMap.length && y + iy >= 0 && y + iy < regionMap[0].length) {
                            borderCount[regionMap[x][y]][regionMap[x + ix][y + iy]] += 1;
                        }
                    }
                }
            }
        }
        PriorityQueue<Pair<Pair<Region,Region>,Double>> pq = new PriorityQueue();
        
        for (int x = 0; x < borderCount.length; x++) {
            for (int y = 0; y < borderCount[0].length; y++) {
                if (x == y) continue;
                pq.add(new Pair(new Pair(regions.get(x), regions.get(y)),(double)- borderCount[x][y]));
            }
        }
        Pair<Region,Region> regionPair = pq.poll().getX();
        
        Region r = mergeRegions(regionPair.getX(), regionPair.getY());
        
        regions.remove(regionPair.getX());
        regions.remove(regionPair.getY());
        
        regions.add((int) (Math.random() * regions.size()), r);
        
        return -pq.poll().getY().intValue();
    }
    private static Region mergeRegions(Region r1, Region r2) {
        
        Region region = new Region();
        r1.getTiles().forEach(a -> region.addTile(a.getX(), a.getY()));
        r2.getTiles().forEach(a -> region.addTile(a.getX(), a.getY()));
        
        return region;
    }
    
    public static int[][] createRegionMap(double[][] map, List<Region> regions) {
        int[][] regionMap = new int[map.length][map[0].length];
        
        for (int i = 0; i < regions.size(); i++) {
            for(Pair<Integer,Integer> tile : regions.get(i).getTiles()) {
                regionMap[tile.getX()][tile.getY()] = i;
            }
        }
        return regionMap;
    }
    
    
    public static void raiseRegions(double[][] map, List<Region> regions) {
        for (int i = 0; i < regions.size(); i++) {
            raiseTileArea(map, regions.get(i).getTiles(), 3, (i * 1.3) / regions.size() * map.length / 256, Math.random() * 0.5);
        }
    }
    
    public static void raiseRegionBorders(double[][] map, List<Region> regions, double mul){
        int[][] regionMap = createRegionMap(map, regions);
        List<Pair<Integer,Integer>> borderTiles = new ArrayList<>();
        double highestPoint = Double.MIN_VALUE;
        double lowestPoint = Double.MAX_VALUE;
        for (int x = 0; x < regionMap.length; x++) {
            for (int y = 0; y < regionMap[0].length; y++) {
                
                for (int ix = -1; ix <= 1; ix++) {
                    for (int iy = (ix == 0)? -1: 0; iy <= 1; iy +=2) {
                        int xcoord = (x + ix >= regionMap.length)? 0 : (x + ix < 0)? regionMap.length-1 : x + ix;
                        
                        if (y + iy >= 0 && y + iy < regionMap[0].length) {
                            if (regionMap[x][y] != regionMap[xcoord][y + iy] ) {
                                Pair<Integer,Integer> tile = new Pair(x,y);
                                
                                if(!borderTiles.contains(tile)){
                                    borderTiles.add(tile);
                                }
                            }
                        }
                        
                        if(map[x][y] > highestPoint) highestPoint = map[x][y];
                        if(map[x][y] < lowestPoint) lowestPoint = map[x][y];
                    }
                }
            }
        }
        raiseTileArea(map, borderTiles, 4, - (highestPoint-lowestPoint) * mul * 0.15, mul * (map[0].length / 100) * (highestPoint-lowestPoint));
        
    } 
    
    
    public static void raiseTileArea(double[][] heightMap, List<Pair<Integer,Integer> > tiles, double r, double add, double amp){

        double range = r * heightMap.length / 200;
        double raise = 0.01/range;
        tiles.forEach((tile) -> {
            for (int ix = (int) -range; ix <= range; ix++) {
                for(int iy = (int) -range;  iy <= range; iy++) {
                    int xcoord = 
                            (ix + tile.getX() >= heightMap.length)? ix + tile.getX() - heightMap.length :
                            (ix + tile.getX() < 0)? heightMap.length + ix + tile.getX() : 
                            ix + tile.getX(); 
                    
                    if (tile.getY() + iy < heightMap[0].length && tile.getY() + iy >= 0 && (ix * ix + iy * iy - range * range <= 0)) {
                        heightMap[xcoord][tile.getY() + iy] += raise + ((range) / ((Math.abs(ix) + Math.abs(iy) + range/3.0))) * amp * ((Math.random() + add) / (range * range * 3.14)) * 1.05 - 0.3/ (range * range * 3.14);
                    }
                }
            }
        });
    }
}
