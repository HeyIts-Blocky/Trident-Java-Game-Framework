package update;

import java.awt.*;
import java.awt.event.*;
import blib.util.*;
import trident.*;
import ent.*;
import custom.*;
public class Inputs { 
    
    public static void keyPressed(int key){
        if(HUD.currentDialog != null){
            if(key == KeyEvent.VK_E){
                HUD.currentDialog.next();
            }
        }else{
            if(key == KeyEvent.VK_E){
                // look for npc
                for(TridEntity e: Trident.getEntities()){
                    if(e instanceof NPC){
                        NPC npc = (NPC)e;
                        
                        if(BTools.getDistance(Trident.getPlrPos(), npc.position) < 200){
                            double dir = BTools.normalizeRadians(BTools.getAngle(Trident.getPlrPos(), npc.position));
                            double dirDiff = Math.abs(dir - WallManager.camera.direction);
                            if(dirDiff < Math.toRadians(45)){
                                if(npc.dialog >= 0){
                                    HUD.setDialog(npc.dialog);
                                }else{
                                    Update.trigger(npc.dialog);
                                }
                                
                            }
                        }
                    }
                }
            }
            if(key == KeyEvent.VK_ESCAPE){
                Trident.captureCursor = false;
            }
            // if(key == KeyEvent.VK_DOWN) Rend3D.offset++;
            // if(key == KeyEvent.VK_UP) Rend3D.offset--;
        }
    }

    public static void mousePressed(int mb, Point mousePos, Position worldPos){
        Trident.captureCursor = true;
    }

    public static void onScroll(int scroll){
        
    }
}
