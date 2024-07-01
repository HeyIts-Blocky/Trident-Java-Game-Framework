package ent;

import blib.util.*;
import custom.WallManager;
import trident.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import blib.b3d.*;
import blib.bson.BSonList;
import blib.bson.BSonObject;
import blib.bson.BSonParser;
public class NPC extends TridEntity {

    private static ImageIcon[] imgs;
    private static boolean loadedTextures = false;

    Wall wall;
    public int dialog;

    boolean addedWalls = false;

    // Constructor, runs when the entity is created
    public NPC(Position pos, int dia, int tex){
        super(pos, new Dimension(64, 64));
        if(!loadedTextures) loadTextures();
        wall = new Wall(new Position(-16, 0), new Position(16, 0), Color.magenta, true, imgs[tex], 64, true);
        dialog = dia;
    }
    // Registry constructor, used only for adding to the registry
    public NPC(){
        super("npc", false, 2);
    }
    // Custom constructor, used by the engine when building a scene
    public TridEntity construct(Position pos, Dimension collision, int[] data){
        return new NPC(pos, data[0], data[1]);
    }

    // Runs every tick while the game is running
    public void update(long elapsedTime){
        if(!addedWalls){
            if(WallManager.walls != null){
                WallManager.walls.add(wall);
                addedWalls = true; 
            }
        }

        double dir = BTools.getAngle(position, Trident.getPlrPos());
        double startDir = dir + Math.PI / 2;
        double endDir = dir - Math.PI / 2;

        Position start = BTools.angleToVector(startDir);
        start.x *= 32;
        start.y *= 32;
        start.x += position.x;
        start.y += position.y;
        Position end = BTools.angleToVector(endDir);
        end.x *= 32;
        end.y *= 32;
        end.x += position.x;
        end.y += position.y;

        wall.start = start;
        wall.end = end;
    }

    private static void loadTextures(){
        ArrayList<BSonObject> objects = BSonParser.readFile("data/resources.bson");
        BSonObject obj = BSonParser.getObject("npcTextures", objects);
        BSonList asList = (BSonList)obj;
        imgs = new ImageIcon[asList.list.size()];
        for(int i = 0; i < asList.list.size(); i++){
            imgs[i] = new ImageIcon("data/images/npc/" + asList.list.get(i).getString());
        }
        loadedTextures = true;
    }

    public void sceneStart(String scene){
        addedWalls = false;
    }
}
