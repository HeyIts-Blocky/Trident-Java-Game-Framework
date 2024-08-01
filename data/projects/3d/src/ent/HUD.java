package ent;

import blib.util.*;
import trident.*;
import javax.swing.*;
import java.awt.*;
import custom.*;
public class HUD extends TridEntity {

    public static DialogString currentDialog = null;

    // Constructor, runs when the entity is created
    public HUD(Position pos){
        super(pos);
        renderType = TOPPRIORITY;
    }
    // Registry constructor, used only for adding to the registry
    public HUD(){
        super("hud", false, 0);
    }
    // Custom constructor, used by the engine when building a scene
    public TridEntity construct(Position pos, Dimension collision, int[] data){
        return new HUD(pos);
    }

    // Render while in game
    public void render(Graphics g, JPanel panel, int x, int y){
        try{
            if(currentDialog != null && currentDialog.getText() != null){
                g.setColor(Color.black);
                g.fillRect(0, Trident.getFrameHeight() - 150, Trident.getFrameWidth(), 150);
                g.setColor(Color.white);
                g.setFont(currentDialog.getFont());
                TextBox.draw(currentDialog.getText(), g, 10, Trident.getFrameHeight() - 150 + currentDialog.getFont().getSize() / 2, TextBox.LEFT, (int)(Trident.getFrameWidth() * 0.75));
                g.setFont(new Font("Arial", Font.BOLD, 20));
                int width = g.getFontMetrics().stringWidth(currentDialog.getName());
                g.setColor(Color.black);
                g.fillRect(5, Trident.getFrameHeight() - 175, width + 10, 25);
                g.setColor(Color.white);
                TextBox.draw(currentDialog.getName(), g, 10, Trident.getFrameHeight() - 160);

                if(currentDialog.scrollDone()){
                    TextBox.draw("V", g, Trident.getFrameWidth() - 5, Trident.getFrameHeight() - 15, TextBox.RIGHT);
                }
            }else{
                for(TridEntity e: Trident.getEntities()){
                    if(e instanceof NPC){
                        NPC npc = (NPC)e;
                        if(BTools.getDistance(Trident.getPlrPos(), npc.position) < 200){
                            double dir = BTools.normalizeRadians(BTools.getAngle(Trident.getPlrPos(), npc.position));
                            double dirDiff = Math.abs(dir - WallManager.camera.direction);
                            if(dirDiff < Math.toRadians(45)){
                                g.setColor(Color.white);
                                g.setFont(new Font("Arial", Font.PLAIN, 25));
                                TextBox.draw("Talk\n(E)", g, Trident.getFrameWidth() / 2, Trident.getFrameHeight() / 2 + 15, TextBox.CENTER);
                                break;
                            }
                            
                        }
                    }
                }
            }
        }catch(Exception e){}
        
    }

    // Runs every tick while the game is running
    public void update(long elapsedTime){
        position = new Position(Trident.getPlrPos().x, Trident.getPlrPos().y + 10);
        if(currentDialog != null){
            currentDialog.update(elapsedTime);
            if(!currentDialog.moreText()){
                currentDialog.reset();
                currentDialog = null;
            }
        }
    }

    public static void setDialog(int dia){
        currentDialog = DialogString.get(dia);
    }
}
