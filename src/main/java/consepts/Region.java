/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package consepts;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sami
 */
public class Region {
    List<Pair<Integer, Integer>> tiles = new ArrayList<>();
    
    public void addTile(int x, int y){
        tiles.add(new Pair(x,y));
    }
    public List<Pair<Integer, Integer>> getTiles(){
        return tiles;
    }
    
    
}
