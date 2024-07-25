package trident;

import java.util.ArrayList;
import java.awt.*;
import java.io.*;
import blib.bson.*;
import blib.util.*;
import trident.ent.*;
import blib.game.*;
public class Scene {
    
    public final String name;
    public ArrayList<TridEntity> entities;
    protected Position plrStart = new Position();
    protected int plrDir = Player.SOUTH;
    protected Color bgColor = Color.white;
    protected int defaultLight = 255;

    public Scene(String n){ // Empty scene
        name = n;
        entities = new ArrayList<TridEntity>();
    }
    public Scene(File f) throws IOException{
        entities = new ArrayList<TridEntity>();
        ArrayList<BSonObject> objects = BSonParser.readFile(f.getAbsolutePath());
        BSonObject obj = BSonParser.getObject("name", objects);
        name = obj.getString();
        obj = BSonParser.getObject("dir", objects);
        if(obj != null){
            String str = obj.getString();
            if(str.equals("west")) plrDir = Player.WEST;
            if(str.equals("north")) plrDir = Player.NORTH;
            if(str.equals("east")) plrDir = Player.EAST;
        }
        obj = BSonParser.getObject("bgColor", objects);
        BSonList asList = (BSonList)obj;
        int r, g, b;
        r = asList.list.get(0).getInt();
        g = asList.list.get(1).getInt();
        b = asList.list.get(2).getInt();
        bgColor = new Color(r, g, b);
        obj = BSonParser.getObject("light", objects);
        defaultLight = obj.getInt();
        obj = BSonParser.getObject("entities", objects);
        asList = (BSonList)obj;
        for(int i = 0; i < asList.list.size(); i++){
            String objName = asList.list.get(i).getString();
            i++;
            boolean foundEnt = false;
            for(TridEntity e: Trident.entRegistry){
                if(e.name.equals(objName)){
                    double x, y;
                    x = asList.list.get(i).getDouble();
                    i++;
                    y = asList.list.get(i).getDouble();
                    i++;
                    int w = 0, h = 0;
                    i++;
                    if(e.HASCOLLISION){
                        w = asList.list.get(i).getInt();
                        i++;
                        h = asList.list.get(i).getInt();
                        i++;
                    }
                    int[] data = new int[e.numData];
                    for(int j = 0; j < e.numData; j++){
                        data[j] = asList.list.get(i).getInt();
                        i++;
                    }
                    i--;
                    entities.add(e.construct(new Position(x, y), (e.HASCOLLISION ? (new Dimension(w, h)) : null), data));
                    foundEnt = true;
                    break;
                }
            }
            if(!foundEnt) Trident.printConsole("ERROR: no entity type '" + objName + "' found");
        }

        // Check for plrstart
        for(TridEntity e: entities){
            if(e instanceof PlrStart){
                plrStart = e.position.copy();
                break;
            }
        }
    }

    public static Scene loadScene(String path){ // stub method ::: Load a scene from a file
        return null;
    }

    public ArrayList<Rectangle> getCollision(){
        ArrayList<Rectangle> collision = new ArrayList<Rectangle>();
        for(int i = 0; i < entities.size(); i++){
            TridEntity e = entities.get(i);
            if(e.HASCOLLISION){
                collision.add(e.getCollision());
            }
        }

        return collision;
    }

}
