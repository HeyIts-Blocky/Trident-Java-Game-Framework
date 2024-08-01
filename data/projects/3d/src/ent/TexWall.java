package ent;

import blib.util.*;
import trident.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import blib.bson.*;
public class TexWall extends TridEntity {

    private static ImageIcon[] textures;
    public ImageIcon texture;
    public int texLen;
    private static boolean loadedTextures = false;

    // Constructor, runs when the entity is created
    public TexWall(Position pos, Dimension coll, int tex, int length){
        super(pos, coll);
        if(!loadedTextures) loadTextures();
        texture = textures[tex];
        texLen = length;
    }
    // Registry constructor, used only for adding to the registry
    public TexWall(){
        super("texWall", true, 2);
    }
    // Custom constructor, used by the engine when building a scene
    public TridEntity construct(Position pos, Dimension collision, int[] data){
        return new TexWall(pos, collision, data[0], data[1]);
    }

    public static void loadTextures(){
        ArrayList<BSonObject> objects = BSonParser.readFile("data/resources.bson");
        BSonObject obj = BSonParser.getObject("wallTextures", objects);
        BSonList asList = (BSonList)obj;
        textures = new ImageIcon[asList.list.size()];
        for(int i = 0; i < asList.list.size(); i++){
            textures[i] = new ImageIcon("data/images/textures/" + asList.list.get(i).getString());
        }
        loadedTextures = true;
    }
}
