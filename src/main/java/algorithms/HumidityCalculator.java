/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import consepts.Pair;

/**
 *
 * @author Sami
 */
public class HumidityCalculator {
    
    public static void calculate(double[][] heightMap, double[][] humidityMap, double waterLevel) {
        double currVal = 0;
        
        calculateWaterHumidity(heightMap, humidityMap, waterLevel);
        
        int x = 0;
        int y = 0;
        
        for (int sx = 0; sx < humidityMap.length; sx++) {
            x = sx;
            for (double sy = 0; (int) sy < humidityMap[0].length; sy += (humidityMap[0].length -1) / 3.0) {
                y = (int) sy;
                for (int add = -1; add <= 1; add +=2) {
                    if (!((sy == 0 && add < 0) || (sy == humidityMap[0].length - 1 && add >= 1))) {
                        for (int i = 0; i < (humidityMap[0].length - 1) / 6; i++) {
                            currVal += humidityMap[x][y];
                            if (x != sx && x < heightMap.length - 1 && x > 0) { // tiles
                                if (heightMap[x][y] > heightMap[x - add][y - add] && heightMap[x][y] > waterLevel){ // check if higher than previous
                                    humidityMap[x][y] = currVal;
                                    currVal -= (heightMap[x][y] - heightMap[x - add][y - add]) * 8;
                                    if (currVal < 0) currVal = 0;
                                } else {
                                    humidityMap[x][y] = currVal * 0.1;
                                    currVal *= 0.9;
                                }
                            }
                            x+= add;
                            y+= add;
                            if (x >= humidityMap.length) x = 0;
                            else if (x < 0) x = humidityMap.length - 1;
                        }
                        
                        while (currVal > 0.5 && y > 0 && y < humidityMap[0].length){
                            currVal /= 1.3;
                            humidityMap[x][y] += currVal;
                            x+=add;
                            if(x >= humidityMap.length) x = 0;
                            else if (x < 0) x = humidityMap.length -1;
                            y+=add;
                        }
                        x = sx;
                        y = (int)sy;
                        currVal = 0;
                    } else if (add != -1 && add != 1) {
                        System.out.println(sx + ", " + sy + " " + add);
                    }
                    
                }
            }
        }
    }
    public static void calculateWaterHumidity(double[][] heightMap, double[][] humidityMap, double waterLevel){
        
        for(int x = 0; x < heightMap.length; x++){
            for(int y = 0; y < heightMap[0].length; y++){
                if (heightMap[x][y] <= waterLevel) { // water tiles

                    // add humidity to nearby tiles

                    calculateWaterPoint(x,y,heightMap,humidityMap,waterLevel);
                }
            }
        }
    }
    
    public static void calculateWaterPoint(int x, int y, double[][] heightMap, double[][] humidityMap, double waterLevel){
        
        int range = heightMap.length / 100;

        for (int ix = -range; ix <= range; ix++) {
            for(int iy = -range;  iy <= range; iy++) {
                int xcoord = ix + x; 
                if (xcoord >= humidityMap.length) xcoord = xcoord - humidityMap.length;
                else if (xcoord < 0) xcoord = humidityMap.length + xcoord;
                if (y + iy < humidityMap[0].length && y + iy >= 0 && (ix * ix + iy * iy - range * range <= 0 )) {
                    if (heightMap[xcoord][y + iy] <= waterLevel) {
                        humidityMap[xcoord][y + iy] += 1;
                    } else {
                        humidityMap[xcoord][y + iy] += 1/(heightMap[xcoord][y + iy] * 1.5);
                    }
                }
            }
        }
    } 
    
    
    public static double[][] diffuse(double[][] humidityMap){
        
        double doubleMap[][] = new double[humidityMap.length][humidityMap[0].length];
        
        for(int x = 0; x < humidityMap.length; x++){
            for(int y = 0; y < humidityMap[0].length; y++){
                int count = 0;
                double sum = 0;
                int range = humidityMap.length / 200;

                for (int ix = -range; ix <= range; ix++) {
                    for(int iy = -range;  iy <= range; iy++) {
                        int xcoord = ix + x; 
                        if (xcoord >= humidityMap.length) xcoord = xcoord - humidityMap.length;
                        else if (xcoord < 0) xcoord = humidityMap.length + xcoord;
                        if (y + iy < humidityMap[0].length && y + iy >= 0 && (ix * ix + iy * iy - range * range <= 0 )) {
                            sum += humidityMap[xcoord][y + iy];
                            count++;
                        }
                    }
                }
                doubleMap[x][y] = sum / count;

                
            }
        }
        return doubleMap;
        
    }
    
    public static List<Pair<Integer,Integer>> calcRivers(double[][] heightMap, double[][] humidityMap, double waterLevel, double hillLevel) {
        List<Pair<Integer,Integer>> rivers = new ArrayList<>();
        
        List<Pair<Integer,Integer>> mountainPoints = new ArrayList<>();
        
        for(int x = 0; x < heightMap.length; x++){
            for(int y = 0; y < heightMap[0].length; y++){
                if(heightMap[x][y] >= hillLevel){
                    mountainPoints.add(new Pair(x,y));
                }
            }
        }
        
        for (int i = 0; i < heightMap.length / 50 && !mountainPoints.isEmpty(); i++) {
            int point = ((int) (Math.random() * (mountainPoints.size() -1)));
            Pair<Integer, Integer> p = mountainPoints.get(point);
            List<Pair<Integer,Integer>> river = riverPather(p.getX(), p.getY(), heightMap,waterLevel);
            if(river.size() > heightMap.length / 70) {
                rivers.addAll(river);
            } else {
                i--;
            }
            mountainPoints.remove(point);
        }
        
        rivers = rivers.stream().distinct().collect(Collectors.toList());
        
        rivers.forEach(a -> calculateWaterPoint(a.getX(),a.getY(),heightMap, humidityMap, waterLevel));
        
        return rivers;
        
    }
    public static List<Pair<Integer,Integer>> riverPather(int x,int y, double[][] heightMap, double waterLevel) {
        List<Pair<Integer,Integer>> river = new ArrayList<>();
        PriorityQueue<Pair<Pair<Integer,Integer>, Double>> adjacents = new PriorityQueue<>();
        Pair<Integer, Integer> p = new Pair(x,y);
        river.add(p);
        addAdjacents(p.getX(),p.getY(),adjacents,heightMap);
        
        
        int cx = x;
        int cy = y;
        double previous = Double.MAX_VALUE;
        double memoryPoint = Double.MAX_VALUE;
        
        while(heightMap[cx][cy] > waterLevel){
            Pair<Pair<Integer,Integer>, Double> point = adjacents.poll();
            cx = point.getX().getX();
            cy = point.getX().getY();
            int cxMinus = (cx <= 0)? heightMap.length -1 : cx - 1;
            int cxPlus = (cx >= heightMap.length -1)? 0: cx + 1;
            if(point.getY() < waterLevel || point.getY() == Double.MAX_VALUE) break;
            
            if(previous >= point.getY()) {
                if (memoryPoint == Double.MAX_VALUE) {
                    memoryPoint = point.getY();
                } else if (point.getY() > memoryPoint * 1.02) {
                    break;
                } else if (point.getY() < memoryPoint) {
                    memoryPoint = point.getY();
                }
            } else if (memoryPoint != Double.MAX_VALUE) {
                raiseToPoint(river,previous,heightMap);
            }
            
            previous = point.getY();
            
            if(!river.contains(point.getX())) {
                river.add(point.getX());
                adjacents.add(new Pair(new Pair(cxMinus,cy),(cy >= 0 && cy < heightMap[0].length && !river.contains(new Pair(cxMinus,cy)))? heightMap[cxMinus][cy]: Double.MAX_VALUE));
                adjacents.add(new Pair(new Pair(cx,cy +1),(cy >= 0 && cy < heightMap[0].length -1 && !river.contains(new Pair(cx,cy + 1)))? heightMap[cx][cy + 1]: Double.MAX_VALUE));
                adjacents.add(new Pair(new Pair(cx,cy -1),(cy > 0 && cy < heightMap[0].length && !river.contains(new Pair(cx,cy -1)))? heightMap[cx][cy -1]: Double.MAX_VALUE));
                adjacents.add(new Pair(new Pair(cxPlus,cy),(cy >= 0 && cy < heightMap[0].length && !river.contains(new Pair(cxPlus,cy)))? heightMap[cxPlus][cy]: Double.MAX_VALUE));
            }
        }
        return river;
        
    }
    
    public static void raiseToPoint(List<Pair<Integer,Integer>> river, double h, double[][] heightMap){
        river.forEach(a -> {   
            if(heightMap[a.getX()][a.getY()] < h){
                heightMap[a.getX()][a.getY()] = h;
            }
        });
    }
    
    public static void addAdjacents(int x,int y, PriorityQueue<Pair<Pair<Integer,Integer>, Double>> pq, double[][] heightMap){
        for(int ix = -1; ix < 1; ix++){
            for (int iy = 0; iy < 1; iy +=2) {
                if(ix == 0 && iy == 0) {
                    iy--;
                }
                if(     
                        x + ix < heightMap.length && 
                        x + ix >= 0 && 
                        y + iy < heightMap[0].length && 
                        y + iy >= 0
                        ) {
                    pq.add(new Pair(new Pair(x + ix, y + iy), heightMap[x + ix][y + iy]));
                
                } else if (
                        (   
                            x + ix >= heightMap.length || 
                            x + ix < 0
                        ) && 
                        y + iy < heightMap[0].length && 
                        y + iy >= 0
                        ) {
                    if(x > 0) {
                        pq.add(new Pair(new Pair(0, y + iy), heightMap[0][y + iy]));
                    } else {
                        pq.add(new Pair(new Pair(heightMap.length -1, y + iy), heightMap[heightMap.length -1][y + iy]));
                    }
                }
                
            }
        }
    }
    
    public List<Pair<Integer,Integer>> createOceanNodeMap(double[][] map, double waterLevel){
        
        List<Pair<Integer,Integer>> oceanPoints = getOceanPoints(map,  waterLevel);
        
        
        
        
        
        return null;
    }
    
    public List<Pair<Integer,Integer>> getOceanPoints(double[][] map, double waterLevel){
        List<Pair<Integer,Integer>> oceanPoints = new ArrayList<>();
        
        for(int x = 0; x < map.length; x++){
            for(int y = 0; y < map[0].length; y++){
                if(map[x][y] <= waterLevel){
                    oceanPoints.add(new Pair(x,y));
                }
            }
        }
        return oceanPoints;
    }
    
    
}
