package update;

import trident.*;
import trident.ent.*;
import ent.*;
import java.util.ArrayList;

import blib.b3d.*;
import blib.game.*;
import blib.util.*;
import custom.*;
import java.awt.*;
import java.awt.event.*;
public class Update {

    public static final String[] fonts = {}; // add fonts here

    public static void setup(){
        // Add custom entities to the registry here. Required in order to load them properly
        Trident.addCustomEntity(new ExampleEntity()); // Use the empty constructor
        Trident.addCustomEntity(new Rend3D());
        Trident.addCustomEntity(new TexWall());
        Trident.addCustomEntity(new TexWallT());
        Trident.addCustomEntity(new HUD());
        Trident.addCustomEntity(new NPC());
        Trident.addCustomEntity(new Prop());

        // Set settings
        Trident.setShortCollision(false);
        WallManager.camera.size = new Dimension(Trident.getFrameWidth(), Trident.getFrameHeight());
        WallManager.camera.rendDist = 2000;
        WallManager.camera.fhp = -25; 

        // Post Processing
        Trident.setBloom(0.2);
        Trident.setExposure(1);
        Trident.enableBloom = false;
        Trident.enableExposure = false;
        Trident.setLightBlur(1);
    }

    public static void sceneStart(String scene){
        WallManager.walls = new ArrayList<Wall>();
        for(TridEntity e: Trident.getEntities()){
            if(e instanceof BoxColl){
                BoxColl box = (BoxColl)e;
                Wall[] walls = Wall.rectToWalls(box.getCollision(), box.color);
                for(Wall w: walls) WallManager.walls.add(w);
            }
            if(e instanceof TexWall){
                TexWall box = (TexWall)e;
                Wall[] walls = Wall.rectToWalls(box.getCollision(), Color.magenta, box.texture, box.texLen);
                for(Wall w: walls) WallManager.walls.add(w);
            }
            if(e instanceof TexWallT){
                TexWallT box = (TexWallT)e;
                Wall[] walls = Wall.rectToWalls(box.getCollision(), Color.magenta, box.texture, box.texLen, true); 
                for(Wall w: walls) WallManager.walls.add(w);
            }
        }

        Wall.lightWalls(WallManager.walls, Math.toRadians(50));

        int plrDir = Trident.getCurrentScene().plrDir;
        if(plrDir == Player.EAST){
            WallManager.camera.direction = 0;
        }
        if(plrDir == Player.SOUTH){
            WallManager.camera.direction = Math.toRadians(90);
        }
        if(plrDir == Player.NORTH){
            WallManager.camera.direction = Math.toRadians(-90);
        }
        if(plrDir == Player.WEST){
            WallManager.camera.direction = Math.toRadians(180); 
        }

        Trident.resetKeys();
    }
    
    public static void update(long elapsedTime){
        if(Rend3D.enabled){
            int mouseDelta = 0;
            if(Trident.captureCursor){
                int startX = Trident.panel.km.getMousePos().x;
                Point p = new Point(Main.window.getWidth() / 2 + Main.window.getX(), Main.window.getHeight() / 2 + Main.window.getY());
                mouseDelta = startX - Main.window.getWidth() / 2;
                if(!Trident.getFullscreen()) mouseDelta += 8;
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice[] gs = ge.getScreenDevices();

                // Search the devices for the one that draws the specified point.
                for (GraphicsDevice device: gs) { 
                    GraphicsConfiguration[] configurations =
                        device.getConfigurations();
                    

                    boolean gtfo = false;
                    for (GraphicsConfiguration config: configurations) {
                        Rectangle bounds = config.getBounds();
                        if(bounds.contains(p)) {
                            // Set point to screen coordinates.

                            try {
                                Robot r = new Robot(device);
                                r.mouseMove(p.x, p.y);
                            } catch (AWTException e) {
                                e.printStackTrace();
                            }

                            gtfo = true;
                            break;
                        }
                    }

                    if(gtfo) break;
                }

                Trident.panel.setCursor(BTools.getBlankCursor());
            }else{
                Trident.panel.setCursor(Cursor.getDefaultCursor());
            }

            Trident.setPlrSpeed(0);
            if(HUD.currentDialog == null){ // only move when there's no dialog

                if(Trident.captureCursor) WallManager.camera.direction += Math.toRadians(mouseDelta * 0.007) * elapsedTime;
        
                Position startPos = Trident.getPlrPos().copy();
                Point move = new Point();
                if(Trident.getKeyDown(KeyEvent.VK_W)) move.y++;
                if(Trident.getKeyDown(KeyEvent.VK_S)) move.y--;
                if(Trident.getKeyDown(KeyEvent.VK_A)) move.x++;
                if(Trident.getKeyDown(KeyEvent.VK_D)) move.x--;
                if(!move.equals(new Point())){
                    Position mov = new Position(move.x, move.y);
                    double dir = BTools.vectorToAngle(mov) - Math.toRadians(90);
                    dir += WallManager.camera.direction;
                    mov = BTools.angleToVector(dir);
                    Trident.getPlr().move(mov.x * 0.1 * elapsedTime, mov.y * 0.1 * elapsedTime);
                }
                if(!Trident.noclip){
                    Rectangle plrRect = Trident.getPlr().getCollision();
                    for(Rectangle rect: Trident.getCollision()){
                        // check x
                        if(rect.intersects(plrRect)){
                            Position pos = Trident.getPlr().getPos().copy();
                            Trident.getPlr().goToPos(startPos.x, Trident.getPlr().getY());
                            plrRect = Trident.getPlr().getCollision();
                            if(rect.intersects(plrRect)){
                                Trident.getPlr().goToPos(pos);
                                plrRect = Trident.getPlr().getCollision();
                            }
                        }
                        // check y
                        if(rect.intersects(plrRect)){
                            Trident.getPlr().goToPos(Trident.getPlr().getX(), startPos.y);
                            plrRect = Trident.getPlr().getCollision();
            
                            // check x
                            if(rect.intersects(plrRect)){
                                Trident.getPlr().goToPos(startPos.x, Trident.getPlr().getY());
                                plrRect = Trident.getPlr().getCollision();
                            }
                        }
                    }
                }
                

                double moveDist = BTools.getDistance(startPos, Trident.getPlrPos()) * 0.1;
                Rend3D.totalMovement += moveDist;
                Rend3D.offset = (int)(Math.sin(Rend3D.totalMovement) * 2);
            }
        }else{
            Trident.setPlrSpeed(0.2);
        }

    }

    public static void trigger(int id){
        
    }

    public static void tridentEvent(int id){
        if(id == Trident.EVENT_SCREENSHOT){
            Trident.printConsole("Took a screenshot!");
        }
    }

    public static int command(ArrayList<String> cmdParts){ // cmdParts.get(0) is the command, while the rest are arguments for the command.
        switch(cmdParts.get(0)){
        case "helloWorld":
            Trident.printConsole("Hello, World!");
            return 0;
        case "ping":
            Trident.printConsole("pong");
            return 0;
        }
        return 1; // return 1 if command is not recognized
    }
    public static String[] commands = { // Fill this with the format for all custom commands
        "helloWorld",
        "ping",
    };
}
