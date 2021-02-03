/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package consepts;

import algorithms.HumidityCalculator;
import algorithms.RegionDivider;
import algorithms.DiamondSquare;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Sami
 */
public class Map {
    
    private final double[][] heightMap, humidityMap;
    final double averageHeight, waterLevel, hillLevel, topHeight, topHumidity;
    private final List<Pair<Integer,Integer>> rivers;
    
    public Map(int mul) {
        System.out.println("Generating map...");
        heightMap = DiamondSquare.generate(mul); 
        System.out.println("Dividing regions...");
        List<Region> regions = RegionDivider.divide(heightMap);
        System.out.println("Raising land...");
        RegionDivider.raiseRegions(heightMap, regions);
        RegionDivider.raiseRegionBorders(heightMap, regions, 0.2);
        RegionDivider.raiseRegionBorders(heightMap, regions, 0.1);
        RegionDivider.raiseRegionBorders(heightMap, RegionDivider.divide(heightMap), 0.2);
        System.out.println("Counting constants...");
        double topHt = 0;
        double topHum = 0;
        double averageH = 0;
        int size = heightMap.length * heightMap[0].length;
        ArrayList<Double> med = new ArrayList<>();
        for (int x = 0; x < heightMap.length; x++) {
            for(int y = 0; y < heightMap[0].length; y++) {
                if (heightMap[x][y] > topHt) topHt = heightMap[x][y];
                med.add(heightMap[x][y]);
                averageH += heightMap[x][y] / size;
            }
        }
        Collections.sort(med);
        waterLevel =  med.get((2*med.size())/3);
        hillLevel = med.get((18*med.size())/20);
        System.out.println("Calculating humidity...");
        double[][] tempHumidityMap = new double[heightMap.length][heightMap[0].length];
        System.out.println("Making rivers...");
        rivers = HumidityCalculator.calcRivers(heightMap, tempHumidityMap, waterLevel, hillLevel);
        
        HumidityCalculator.calculate(heightMap, tempHumidityMap, waterLevel);
        
        for (int x = 0; x < heightMap.length; x++) {
            for(int y = 0; y < heightMap[0].length; y++) {
                if (tempHumidityMap[x][y] > topHum && heightMap[x][y] >= waterLevel) topHum = tempHumidityMap[x][y];
            }
        }
        
        System.out.println("Diffusing...");
        
        humidityMap = HumidityCalculator.diffuse(tempHumidityMap);
        
        averageHeight = averageH;
        topHeight = topHt;
        topHumidity = topHum;
        
        System.out.println("Done.");
    }
    
    public double getHeight(int x, int y) {
        return heightMap[x][y];
    }
    public double getHumidity(int x, int y){
        return humidityMap[x][y];
    }
    public double[][] getHeightMap(){
        return heightMap;
    }
    public double[][] getHumidityMap(){
        return humidityMap;
    }
    
    public int getMapWidth(){
        return heightMap.length;
    }
    public int getMapHeight(){
        return heightMap[0].length;
    }
    public double getWaterLevel(){
        return waterLevel;
    }
    public double getHillLevel(){
        return hillLevel;
    }
    public double getMaxHeight(){
        return topHeight;
    }
    public double getTemperature(int x, int y) {
        
        if (y < heightMap[0].length / 2){
            return (100.0 / heightMap[0].length) * y - this.getHeight(x, y) * 2;
        }
        return (100.0 / heightMap[0].length) * (heightMap[0].length - y) - this.getHeight(x, y) * 2;
    }
    public List<Pair<Integer,Integer>> getRivers(){
        return rivers;
    }
    
}
