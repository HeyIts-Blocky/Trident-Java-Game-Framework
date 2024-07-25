package ent;

import blib.util.*;
import trident.*;
import javax.swing.*;
import java.awt.*;
public class ExampleEntity extends TridEntity {

    // Constructor, runs when the entity is created
    public ExampleEntity(Position pos){
        super(pos);
    }
    // Registry constructor, used only for adding to the registry
    public ExampleEntity(){
        super("example", false, 0);
    }
    // Custom constructor, used by the engine when building a scene
    public TridEntity construct(Position pos, Dimension collision, int[] data){
        return new ExampleEntity(pos);
    }

    // Render while in game
    public void render(Graphics g, JPanel panel, int x, int y){

    }

    // Runs every tick while the game is running
    public void update(long elapsedTime){
        
    }

    // Runs at the beginning of the scene
    public void sceneStart(String scene){

    }
}
