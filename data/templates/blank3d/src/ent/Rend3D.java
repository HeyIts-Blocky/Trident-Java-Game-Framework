package ent;

import blib.util.*;
import trident.*;
import javax.swing.*;
import java.awt.*;
import custom.*;
public class Rend3D extends TridEntity {

    public static int offset = 0;
    public static double totalMovement = 0;
    public static boolean enabled = true;

    // Constructor, runs when the entity is created
    public Rend3D(Position pos){
        super(pos);
        renderType = TOPPRIORITY;
        alwaysRender = true;
    }
    // Registry constructor, used only for adding to the registry
    public Rend3D(){
        super("rend3d", false, 0);
    }
    // Custom constructor, used by the engine when building a scene
    public TridEntity construct(Position pos, Dimension collision, int[] data){
        return new Rend3D(pos);
    }

    // Render while in game
    public void render(Graphics g, JPanel panel, int x, int y){
        WallManager.camera.render(panel, g, WallManager.walls, offset);
        if(Trident.drawCollision){
            Line leftSide = Line.ray(new Position(x, y), WallManager.camera.direction - WallManager.camera.fov / 2, WallManager.camera.rendDist);
            Line rightSide = Line.ray(new Position(x, y), WallManager.camera.direction + WallManager.camera.fov / 2, WallManager.camera.rendDist);

            g.setColor(Color.blue);
            g.drawLine((int)leftSide.start.x, (int)leftSide.start.y, (int)leftSide.end.x, (int)leftSide.end.y);
            g.setColor(Color.cyan);
            g.drawLine((int)rightSide.start.x, (int)rightSide.start.y, (int)rightSide.end.x, (int)rightSide.end.y);
        }
    }

    // Runs every tick while the game is running
    public void update(long elapsedTime){
        WallManager.camera.position = Trident.getPlrPos();
        position = Trident.getPlrPos();
    }

    // Runs at the beginning of the scene
    public void sceneStart(String scene){
        enabled = true;
    }
}
