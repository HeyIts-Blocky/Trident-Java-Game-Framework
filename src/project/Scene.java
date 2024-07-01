package project;

import java.util.ArrayList;
import java.awt.*;
import java.io.*;
import blib.bson.*;
import blib.util.*;
import blib.game.*;
import project.ent.*;
public class Scene {
    
    public final String name;
    public ArrayList<TridEntity> entities;
    protected Position plrStart = new Position();
    public int plrDir = Player.SOUTH;
    public Color bgColor = Color.white;
    private TridEntity[] entRegistry = {
        new BoxColl(),
        new BoxNoColl(),
        new InvisColl(),
        new PlrStart(),
        new Trigger(),
        new project.ent.Light(),
    };
    public int defaultLight = 255;

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
            for(TridEntity e: entRegistry){
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
            if(!foundEnt){
                double x, y;
                x = asList.list.get(i).getDouble();
                i++;
                y = asList.list.get(i).getDouble();
                i++;
                int w = 0, h = 0;
                boolean hasColl = asList.list.get(i).getBoolean();
                i++;
                if(hasColl){
                    w = asList.list.get(i).getInt();
                    i++;
                    h = asList.list.get(i).getInt();
                    i++;
                }
                
                int[] data = new int[0];
                while(i < asList.list.size() && asList.list.get(i).type == BSonObject.INT){
                    int[] newData = new int[data.length + 1];
                    for(int j = 0; j < data.length; j++) newData[j] = data[j];
                    newData[data.length] = asList.list.get(i).getInt();
                    data = newData;
                    i++;
                }
                entities.add(new CustomEntity(new Position(x, y), new Dimension(w, h), data, objName));
                i--;
            }
        }
    }

    public String getDir(){
        if(plrDir == Player.SOUTH) return "south";
        if(plrDir == Player.EAST) return "east";
        if(plrDir == Player.WEST) return "west";
        if(plrDir == Player.NORTH) return "north";
        return "unknown direction";
    }

    public void save(String projDir){
        try{
            File file = new File(projDir + "/data/scenes/" + name + ".bson");
            file.createNewFile();
            PrintWriter writer = new PrintWriter(file);
            writer.println("string name " + name);
            writer.println("string dir " + getDir());
            writer.println("int light " + defaultLight);
            writer.println("{ bgColor");
            writer.println("int " + bgColor.getRed());
            writer.println("int " + bgColor.getGreen());
            writer.println("int " + bgColor.getBlue());
            writer.println("}");
            writer.println("{ entities");
            for(TridEntity e: entities){
                writer.println("string " + e.name);
                writer.println("double " + e.position.x);
                writer.println("double " + e.position.y);
                writer.println("boolean " + e.HASCOLLISION);
                if(e.HASCOLLISION){
                    writer.println("int " + e.getCollision().width);
                    writer.println("int " + e.getCollision().height);
                }
                if(e instanceof BoxNoColl){
                    BoxNoColl box = (BoxNoColl)e;
                    writer.println("int " + box.width);
                    writer.println("int " + box.height);
                    writer.println("int " + box.color.getRed());
                    writer.println("int " + box.color.getGreen());
                    writer.println("int " + box.color.getBlue());
                }
                if(e instanceof BoxColl){
                    BoxColl box = (BoxColl)e;
                    writer.println("int " + box.color.getRed());
                    writer.println("int " + box.color.getGreen());
                    writer.println("int " + box.color.getBlue());
                }
                if(e instanceof Trigger){
                    Trigger trig = (Trigger)e;
                    writer.println("int " + trig.box.width);
                    writer.println("int " + trig.box.height);
                    writer.println("int " + trig.id);
                }
                if(e instanceof project.ent.Light){
                    project.ent.Light light = (project.ent.Light)e;
                    writer.println("int " + light.radius);
                }
                if(e instanceof CustomEntity){
                    CustomEntity custom = (CustomEntity)e;
                    for(int i: custom.data){
                        writer.println("int " + i);
                    }
                }
            }
            writer.println("}");
            writer.close();
        }catch(Exception e){}
    }

}
