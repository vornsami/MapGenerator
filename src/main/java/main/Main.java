/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import consepts.Map;
import consepts.Pair;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.stage.Stage;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
/**
 *
/**
 *
 * @author Sami
 */
public class Main extends Application {
    
    public int startPoint;
    
    @Override
    public void start(Stage primaryStage) {
        Pane pane = new Pane();
        //BorderPane pohja = new BorderPane();
        Camera camera = new PerspectiveCamera();
        Scene scene = new Scene(pane, 1000,500);
        scene.setCamera(camera);
        
        long s = System.currentTimeMillis();
        
        Map map = new Map(9);
        
        camera.translateZProperty().set(-map.getMapHeight()/4);
        camera.translateXProperty().set(-map.getMapHeight());
        camera.translateYProperty().set(-map.getMapHeight()/2);
        System.out.println(System.currentTimeMillis() - s + "ms");
        System.err.println("Creating mesh...");
        TriangleMesh mesh = prepareMesh(map);
        MeshView meshMap = new MeshView(mesh);
        
        
        pane.getChildren().add(meshMap);
        Canvas canvas = new Canvas(map.getMapWidth(), map.getMapHeight());
        
        
        startPoint = 0;
        
        s = System.currentTimeMillis();
        drawAll(canvas, map, startPoint);
        System.out.println(System.currentTimeMillis() - s + "ms");
        
        WritableImage wi = new WritableImage((int)canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, wi);
        map = null;
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(wi);
        meshMap.setDrawMode(DrawMode.FILL);
        meshMap.setMaterial(material);
        wi = null;
        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, e -> { 
            Transform t = null;
            switch(e.getCode()) {
                case R: 
                    camera.translateZProperty().set(camera.getTranslateZ() + 50);
                    break;
                case F: 
                    camera.translateZProperty().set(camera.getTranslateZ() - 50);
                    break;
                case W: 
                    camera.translateYProperty().set(camera.getTranslateY() - 50);
                    break;
                case S: 
                    camera.translateYProperty().set(camera.getTranslateY() + 50);
                    break;
                case A: 
                    camera.translateXProperty().set(camera.getTranslateX() - 50);
                    break;
                case D: 
                    camera.translateXProperty().set(camera.getTranslateX() + 50);
                    break;
                case UP: 
                    t = new Rotate(-5, new Point3D(1,0,0));
                    meshMap.getTransforms().add(t);
                    break;
                case DOWN: 
                    t = new Rotate(5, new Point3D(1,0,0));
                    meshMap.getTransforms().add(t);
                    break;
                case LEFT: 
                    t = new Rotate(5, new Point3D(0,0,1));
                    meshMap.getTransforms().add(t);
                    break;
                case RIGHT: 
                    t = new Rotate(-5, new Point3D(0,0,1));
                    meshMap.getTransforms().add(t);
                    break;
                    
                case M: 
                    t = new Rotate(-5, new Point3D(0,1,0));
                    meshMap.getTransforms().add(t);
                    break;    
                    
                case N: 
                    t = new Rotate(+5, new Point3D(0,1,0));
                    meshMap.getTransforms().add(t);
                    break;    
                    
                case ESCAPE: 
                    meshMap.getTransforms().clear();
                    break;
            }
            
            
        });
        
        primaryStage.setTitle("terrain");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void drawAll(Canvas canvas, Map map, int startPoint){
        
        GraphicsContext drawer = canvas.getGraphicsContext2D();
        
        
        //drawTerrain(Color.DARKOLIVEGREEN, map,startPoint,drawer); // hot dry
        
        drawTerrain(Color.DARKKHAKI, map,startPoint,drawer); // dry
        drawTerrain(Color.OLIVE, map,startPoint,drawer); // temperate humid
        drawTerrain(Color.YELLOWGREEN, map,startPoint,drawer); // hot humid
        drawTerrain(Color.FORESTGREEN, map,startPoint,drawer);// hot wet
        drawTerrain(Color.OLIVEDRAB, map,startPoint,drawer); // temperate wet
        drawTerrain(Color.CHOCOLATE, map,startPoint,drawer); // very dry
        drawTerrain(Color.SNOW, map,startPoint,drawer); // cold
        drawTerrain(Color.BLUE, map,startPoint,drawer); // water
        drawTerrain(Color.DARKBLUE, map,startPoint,drawer); // deep water
        //drawTerrain(Color.DARKGREY, map,startPoint,drawer); // hills
        drawTerrain(Color.DIMGREY, map,startPoint,drawer); // mountains
        drawRivers(map, startPoint, drawer); // rivers
    }
    
    public static void drawTerrain(Color color, Map map, int startPoint, GraphicsContext drawer){
        drawer.setFill(color);
        int tx = 0;
        for(int y=0;y<map.getMapHeight();y++){
            drawPoint(startPoint,y,tx,map,drawer);
        }
        
        tx = 1;
        
        for(int y=0;y<map.getMapHeight();y++){
            for(int x=startPoint + 1;x != startPoint; x++){
                drawPoint(x,y,tx,map,drawer);
                if (x >= map.getMapWidth() -1) x = -1;
                tx++;
            }
            tx = 0;
        }
    }
    
    private static void drawRivers(Map map, int delta, GraphicsContext drawer){
        List<Pair<Integer,Integer>> rivers = map.getRivers();
        drawer.setFill(Color.AQUAMARINE);
        rivers.forEach(a -> {
            if(a.getX() - delta >= 0){
                drawer.fillRect(a.getX() - delta, a.getY(), 1, 1);
            } else {
                drawer.fillRect(map.getMapWidth() + a.getX() - delta, a.getY(), 1, 1);
            }
            
        
        });
        
    }
    private static void drawPoint(int x, int y, int tx, Map map, GraphicsContext drawer) {
        
        if (drawer.getFill().equals(Color.BLUE) && map.getHeight(x, y) <= map.getWaterLevel()) {
            drawer.fillRect(tx, y, 1, 1);
        } else if (drawer.getFill().equals(Color.DARKBLUE) && map.getHeight(x, y) / map.getWaterLevel() <= 0.82) {
            drawer.fillRect(tx, y, 1, 1);
        } else if (drawer.getFill().equals(Color.SNOW) && (map.getTemperature(x, y) < 0)) {
            drawer.fillRect(tx, y, 1, 1);
        } else if (drawer.getFill().equals(Color.CHOCOLATE) && map.getHumidity(x, y) < 1){
            drawer.fillRect(tx, y, 1, 1);
        }  else if (map.getTemperature(x, y) < 32 &&(drawer.getFill().equals(Color.DARKKHAKI) || drawer.getFill().equals(Color.OLIVEDRAB)  || drawer.getFill().equals(Color.OLIVE) )) {
            if(drawer.getFill().equals(Color.DARKKHAKI) && map.getHumidity(x, y) < 10) {
                drawer.fillRect(tx, y, 1, 1);
            } else if (drawer.getFill().equals(Color.OLIVE) && map.getHumidity(x, y) >= 10 && map.getHumidity(x, y) < 70) {
                drawer.fillRect(tx, y, 1, 1);
            } else if (drawer.getFill().equals(Color.OLIVEDRAB) && map.getHumidity(x, y) >= 70){
                drawer.fillRect(tx, y, 1, 1);
            }
        }  else if (drawer.getFill().equals(Color.FORESTGREEN) && map.getHumidity(x, y) > 100){
            drawer.fillRect(tx, y, 1, 1);
        } else if (drawer.getFill().equals(Color.YELLOWGREEN)  && map.getHumidity(x, y) <= 100 && map.getHumidity(x, y) > 10 && map.getTemperature(x, y) >= 32){
            drawer.fillRect(tx, y, 1, 1);
        } else if (drawer.getFill().equals(Color.DARKGREY) && map.getHeight(x, y) >= map.getMaxHeight() * 0.96){
            drawer.fillRect(tx, y, 1, 1);
        } else if (drawer.getFill().equals(Color.DIMGREY) && map.getHeight(x, y) >= map.getHillLevel() * 1.12){
            drawer.fillRect(tx, y, 1, 1);
        } else if (drawer.getFill().equals(Color.DARKKHAKI)){
            drawer.fillRect(tx, y, 1, 1);
        }
        
    }
    public static TriangleMesh prepareMesh(Map map) {
        TriangleMesh mesh = new TriangleMesh(VertexFormat.POINT_TEXCOORD);
        
        
        for (int y = 0; y < map.getMapHeight(); y++) {
            for (int x = 0; x < map.getMapWidth(); x++) {
                if(map.getHeight(x, y) > map.getWaterLevel()){
                    mesh.getPoints().addAll(x - (float) (map.getMapWidth()*1.0 / 2) , y- (float) (map.getMapHeight()*1.0 / 2), (float) -map.getHeight(x, y) * 7);
                } else {
                    mesh.getPoints().addAll(x- (float) (map.getMapWidth()*1.0 / 2) , y- (float) (map.getMapHeight()*1.0 / 2), (float) -map.getWaterLevel() * 7);
                }
                mesh.getTexCoords().addAll((float)(x * 1.0/ map.getMapWidth()),(float)(y * 1.0 / map.getMapHeight()));
            }
        }
        int pos = 0;
        for (int y = 0; y < map.getMapHeight() -1; y++) {
            for (int x = 0; x < map.getMapWidth() - 1; x++) {
                mesh.getFaces().addAll(
                    (x + map.getMapWidth() * y),pos , (x + map.getMapWidth() * (y + 1)),pos, (x + 1 + map.getMapWidth() * y), pos,
                        (x + map.getMapWidth() * (y + 1)),pos  , (x + 1 + map.getMapWidth() * (y + 1)), pos , (x + 1 + map.getMapWidth() * y), pos
                
                );
                pos++;
            }
            pos++;
        }
        System.out.println(mesh.getFaces().size() + " tris");
        return mesh;
    }
    
    
    
    
    
    public static void main(String[] args) {
        
        launch(args);
    }

    
}
