package trident;

import java.awt.image.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import blib.util.*;
import java.awt.*;
import blib.game.*;
import javax.swing.*;
import ent.*;
public class RenderingThread extends Thread {

    public BufferedImage lastFrame = null;
    private long elapsedTime, previousStartTime = -1;

    private ImageIcon consoleBg = new ImageIcon("data/images/trident/consolebg.png");
    private long errorFlashTime = 0;

    public RenderingThread(){
        BTools.resizeImgIcon(consoleBg, Trident.getFrameWidth(), 483);
    }
    
    public void run(){
        while(true){
            try{
                
                long now = System.currentTimeMillis();
                elapsedTime = previousStartTime != -1 ? now - previousStartTime : 0;
                previousStartTime = now;
                int WIDTH = Trident.getFrameWidth(), HEIGHT = Trident.getFrameHeight();
                int offX, offY;
                offX = Trident.camShake.offX;
                offY = Trident.camShake.offY;
                BufferedImage newFrame = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
                Graphics g = newFrame.getGraphics();

                if(Trident.engineDraw){
                    for(TridEntity e: Trident.currentScene.entities){
                        Point p = Trident.player.camera.worldToScreen(e.position);
                        e.engineRender(g, null, p.x, p.y);
                    }
                }

                ArrayList<ArrayList<Entity>> splitEnt = Trident.player.camera.splitEntities(Trident.tridArrToEntArr(Trident.currentScene.entities), 16);
                if(!Rend3D.enabled){
                    if(!Trident.engineDraw) Trident.player.camera.render(g, splitEnt.get(2), offX, offY);
                    if(!Trident.engineDraw) Trident.player.camera.render(g, splitEnt.get(0), offX, offY);
                    if(Trident.drawPlayer){
                        Trident.player.render(null, g, WIDTH / 2 - offX, HEIGHT / 2 - offY);
                    }
                    if(!Trident.engineDraw) Trident.player.camera.render(g, splitEnt.get(1), offX, offY);

                    Trident.lightManager.render(Trident.player.camera, Trident.lights, g, offX, offY);
                }
                

                if(!Trident.engineDraw) Trident.player.camera.render(g, splitEnt.get(3), offX, offY);


                

                if(Trident.drawCollision){
                    g.setColor(Color.red);
                    ArrayList<Rectangle> collision = Trident.currentScene.getCollision();
                    collision.add(Trident.player.getCollision());
                    for(Rectangle r: collision){
                        Point p = Trident.player.camera.worldToScreen(new Position(r.x, r.y));
                        g.drawRect(p.x, p.y, r.width, r.height);
                        g.drawLine(p.x, p.y, p.x + r.width, p.y + r.height);
                    }
                }

                if(Trident.drawPos){
                    g.setColor(Trident.debugColor);
                    g.setFont(new Font("Arial", Font.ITALIC, 10));
                    TextBox.draw(Trident.player.getPos().toStringSimple(), g, 10, 20);
                }
                if(Trident.drawFrames){
                    g.setColor(Trident.debugColor);
                    g.setFont(new Font("Arial", Font.ITALIC, 10));
                    TextBox.draw("TPS: " + (1000 / Math.max(MainPanel.server.getElapsedTime(), 1)) + " (" + MainPanel.server.getElapsedTime() + " ms)", g, 10, 30);
                    TextBox.draw("FPS: " + (1000 / Math.max(elapsedTime, 1)) + " (" + elapsedTime + " ms)", g, 10, 40); 
                }
                
                // Apply Post Processing
                if(Trident.enableExposure){
                    Trident.exposure.filter(newFrame, newFrame);
                }
                if(Trident.enableBloom){
                    Trident.bloom.filter(newFrame, newFrame);
                }
                
                if(MainPanel.inIntro){
                    g.setColor(Color.black);
                    g.fillRect(0, 0, 700, 500);

                    if(Trident.splash != null && BTools.hasImage(Trident.splash)){
                        // Trident splash + custom splash
                        MainPanel.splash.paintIcon(null, g, WIDTH / 2 - 80, 40);
                        Trident.splash.paintIcon(null, g, WIDTH / 2 - 80, HEIGHT - 200);
                    }else{
                        // Trident splash only
                        MainPanel.splash.paintIcon(null, g, WIDTH / 2 - 80, HEIGHT / 2 - 80);
                    }

                    float alpha = (float)MainPanel.introPos.x;
                    g.setColor(new Color(0f, 0f, 0f, alpha));
                    g.fillRect(0, 0, 700, 500);
                }

                if(Trident.consoleOpen){
                    // bg
                    consoleBg.paintIcon(null, g, 0, -252);
                    g.setColor(new Color(0f, 0f, 0f, 0.7f));
                    g.fillRect(0, 0, Trident.getFrameWidth(), Trident.getFrameHeight() / 2);

                    // text
                    g.setColor(Color.white);
                    g.setFont(new Font("Arial", Font.PLAIN, 10));
                    for(int i = 0; i < Trident.consoleLines.size(); i++){
                        int index = BTools.flip(i, Trident.consoleLines.size() - 1);
                        TextBox.draw(Trident.consoleLines.get(index), g, 5, Trident.getFrameHeight() / 2 - 25 - (i * 10));
                    }

                    // borders
                    g.setColor(new Color(32, 12, 121));
                    g.drawRect(0, 0, Trident.getFrameWidth() - 1, Trident.getFrameHeight() / 2);
                    g.drawLine(0, Trident.getFrameHeight() / 2 - 10, Trident.getFrameWidth(), Trident.getFrameHeight() / 2 - 10);

                    // text entry box
                    g.setColor(Color.white);
                    g.setFont(new Font("Arial", Font.PLAIN, 10));
                    TextBox.draw("> " + Trident.consoleType + "_", g, 3, Trident.getFrameHeight() / 2 - 5);
                }
                if(Trident.consoleError){
                    errorFlashTime += elapsedTime;
                    g.setColor(new Color(1f, (float)(Math.min(1, Math.sin(errorFlashTime / 500.0) / 2 + 0.5)), (float)(Math.min(1, Math.sin(errorFlashTime / 500.0) / 2 + 0.5))));
                    g.setFont(new Font("Arial", Font.BOLD, 20));
                    TextBox.draw("ERROR\nOpen console with [~](tilde)", g, Trident.getFrameWidth() / 2, 10, TextBox.CENTER);
                }

                lastFrame = newFrame;
            }catch(Exception e){
                if(!(e instanceof ConcurrentModificationException)){
                    e.printStackTrace();
                }
            }
            

        }
    }
}
