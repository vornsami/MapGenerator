/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms;

/**
 *
 * @author Sami
 */
public class DiamondSquare {
    
    private static final double MUL = 4;
    private static final double MIN = 2;
    
    public static double[][]  generate(int mul) {
        int w = (int) Math.pow(2, mul);
        
        double[][] heightMap = new double[w * 2 + 1][w + 1];
        
        double m = 5;
        
        heightMap[0][0] = m/2;//Math.random() * m;
        heightMap[w][0] = m/2;//Math.random() * m;
        heightMap[0][w] = m/2;//Math.random() * m;
        heightMap[w][w] = m/2;//Math.random() * m;
        
        for (int i = 1; i < w - 1; i *= 2) {
            diamondStep(heightMap, i);
            squareStep(heightMap, i);
        }
        double[][] returned = new double[heightMap.length - 1][heightMap[0].length];
        
        for (int x = 0; x < returned.length; x++) {
            System.arraycopy(heightMap[x], 0, returned[x], 0, returned[0].length);
        }
        
        return returned;
    }
    
    public static void diamondStep(double[][] heightMap, int round) {
        int r = round;
        int v = (heightMap[0].length - 1) / r;
        
        for (int x = 0; x < heightMap.length - 1; x += v) {
            for (int y = 0; y < heightMap[0].length - 1; y += v) {
                int xv = x + v;
                if(x + v >= heightMap.length - 1){
                    xv = 0;
                }
                
                double a =  heightMap[x][y];
                double b =  heightMap[xv][y]; 
                double c =  heightMap[x][y + v];
                double d =  heightMap[xv][y + v];
                heightMap   [ x + v/2 ] [  y + v/2 ] 
                        = ((a + b + c + d) / 4) + Math.random() * MUL/round - MIN/round;
            }
        }
        
        
    }
    public static void squareStep(double[][] heightMap, int round) {
        
        int v = (heightMap[0].length - 1) / (round);
        
        for (int x = 0; x < heightMap.length - 1; x += v) {
            for (int y = 0; y < heightMap[0].length - 1; y += v) {
                
                for (int ix = 0; ix <=2; ix++) {
                    for (int iy = (ix == 1)? 0: 1; iy <= 2; iy +=2) {
                        int xv = (x + v == heightMap.length - 1 && ix == 2)? 0 :  x + (v * ix) / 2;
                        int div = (y + iy == 0 || y + iy >= heightMap[0].length -1)? 3 : 4;
                        
                        double add = 0;
                        if (ix == 1 && (y + iy == 0 || iy != 0)) {
                            add += heightMap[x][y + ((v * iy) / 2)];
                            add += heightMap[(x + v == heightMap.length - 1)? 0 : x + v][y + ((v * iy) / 2)];
                        } else if (ix == 2) {
                            add += heightMap[xv][y];
                            add += heightMap[xv][y + v];
                        }
                        add /= div;
                        heightMap[xv][y + (v * iy) / 2] += add;
                        
                        heightMap[xv][y + (v * iy) / 2] += (heightMap[x + (v / 2)][y + (v / 2)] / div)
                                                            + Math.random() * MUL/round - MIN/round;
                        
                    }
                }
            }
        }
    }
}
