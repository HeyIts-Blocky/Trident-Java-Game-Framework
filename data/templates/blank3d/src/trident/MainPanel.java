package trident;

import javax.swing.*;
import java.awt.*;
import blib.game.*;
import blib.util.*;
import java.awt.event.*;
import blib.input.*;
import java.util.ArrayList;
import blib.anim.*;
import trident.ent.*;
import update.*;
public class MainPanel extends JPanel {

    protected FrameManager frameManager = new FrameManager();
    public static Server server;
    public KeyManager km = new InputListener(this);
    private Animator introAnim;
    public static Position introPos = new Position();
    public static ImageIcon splash = new ImageIcon("data/images/trident/splash.png");
    public static boolean inIntro = false;
    private RenderingThread rendThread = new RenderingThread();
    
    public MainPanel(){

        Trident.panel = this;

        System.setProperty("sun.java2d.opengl", "true"); // hardware acceleration?

        setBackground(Color.black);

        Trident.player = new Player(new Position(), km, 0.1, this, "data/images/player", 16, 16);
        Trident.player.camera.setDimension = new Dimension(frameManager.WIDTH, frameManager.HEIGHT);
        Trident.player.resizeImages(32, 32);
        Trident.currentScene = new Scene("Test Scene");
        Trident.camShake = new CamShake(Trident.player.camera);
        Trident.lightManager.blur.setRadius(100);

        Trident.addCustomEntity(new BoxColl());
        Trident.addCustomEntity(new BoxNoColl());
        Trident.addCustomEntity(new InvisColl());
        Trident.addCustomEntity(new PlrStart());
        Trident.addCustomEntity(new Trigger());
        Trident.addCustomEntity(new TridLight());

        setFocusTraversalKeysEnabled(false);

        Update.setup();

        Trident.setupScenes();

        Trident.loadScene(Trident.defaultScene);

        for(String s: Update.fonts){
            BTools.addFont(s, this);
        }

        try{
            ArrayList<Animation> anims = new ArrayList<Animation>();
            anims.add(new Animation("data/animations/intro"));

            introAnim = new Animator(introPos, anims);
        }catch(Exception e){
            Trident.intro = false;
        }
        if(Trident.intro){
            introAnim.play("intro");
            inIntro = true;
        }
        if(Trident.splash != null && BTools.hasImage(Trident.splash)){
            BTools.resizeImgIcon(Trident.splash, 160, 160);
        }

        Trident.consoleError = false;
        rendThread.start();

        server = new Server(new ServerListener(), this);
    }

    public void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        Graphics g = frameManager.newFrame();

        frameManager.bgColor = Trident.currentScene.bgColor;
        
        if(rendThread.lastFrame != null){
            g.drawImage(rendThread.lastFrame, 0, 0, null);
        }
        
        frameManager.renderFrame(this, graphics);
    }

    public MainPanel panel = this;
    private class InputListener extends InputAdapter {
        public InputListener(JPanel panel){
            super(panel);
        }

        public void onKeyPressed(int key){
            if(key == KeyEvent.VK_F12){
                frameManager.saveScreenshot("data/screenshots");
                Update.tridentEvent(Trident.EVENT_SCREENSHOT);
            }
            if(key == KeyEvent.VK_F11){
                Trident.fullscreen = !Trident.fullscreen;
                Main.window = BTools.getWindowFullscreen(Main.window, Trident.fullscreen, panel);
                Update.tridentEvent(Trident.EVENT_FULLSCREEN);
                return;
            }
            if(!inIntro){
                if(key == 192 && Trident.consoleEnabled){
                    Trident.consoleOpen = !Trident.consoleOpen;
                    Trident.consoleType = "";
                    Trident.consoleError = false;
                }
                if(Trident.consoleOpen){
                    if(key >= KeyEvent.VK_A && key <= KeyEvent.VK_Z){
                        char c = KeyEvent.getKeyText(key).charAt(0);
                        if(!km.getKeyDown(KeyEvent.VK_SHIFT)){
                            c = Character.toLowerCase(c);
                        }
                        Trident.consoleType += c;
                    }
                    if(key >= KeyEvent.VK_0 && key <= KeyEvent.VK_9){
                        char c = KeyEvent.getKeyText(key).charAt(0);
                        Trident.consoleType += c;
                    }
                    if(key == KeyEvent.VK_SPACE){
                        Trident.consoleType += " ";
                    }
                    if(key == KeyEvent.VK_MINUS){
                        Trident.consoleType += "-";
                    }
                    if(key == KeyEvent.VK_PERIOD){
                        Trident.consoleType += ".";
                    }
                    if(key == KeyEvent.VK_BACK_SPACE){
                        if(Trident.consoleType.length() > 0) Trident.consoleType = Trident.consoleType.substring(0, Trident.consoleType.length() - 1);
                    }
                    if(key == KeyEvent.VK_UP){
                        Trident.consoleType = Trident.lastCommand;
                    }

                    if(key == KeyEvent.VK_ENTER){
                        Trident.runCommand(Trident.consoleType);
                        Trident.consoleType = "";
                    }
                    return;
                }
                Inputs.keyPressed(key);
            }
        }

        public void onMousePressed(int mb, Point mousePos){
            mousePos = frameManager.getMousePos(panel, mousePos);
            Position worldPos = Trident.player.camera.mouseToPos(mousePos);
            if(!inIntro) Inputs.mousePressed(mb, mousePos, worldPos);
        }

        public void onScroll(int scroll){
            if(!inIntro) Inputs.onScroll(scroll);
        }
    }

    private class ServerListener implements ActionListener {
        public void actionPerformed(ActionEvent event){
            if(inIntro){
                introAnim.update(server.getElapsedTime());
                if(!introAnim.isPlaying()){
                    inIntro = false;
                }
                repaint();
                return;
            }

            if(!hasFocus()) km.reset();

            if(!Trident.consoleOpen){
                Trident.mousePos = frameManager.getMousePos(panel, km.getMousePos());
                Trident.mouseDelta = km.getMouseDelta();
                Trident.mouseWorldPos = Trident.player.camera.mouseToPos(Trident.mousePos);

                Trident.camShake.update(server.getElapsedTime());

                if(!Trident.noclip) Trident.player.updateWithCollision(server.getElapsedTime(), Trident.currentScene.getCollision());
                else Trident.player.update(server.getElapsedTime());

                for(int i = 0; i < Trident.getEntities().size(); i++){
                    TridEntity e = Trident.getEntities().get(i);
                    e.update(server.getElapsedTime());
                    if(e instanceof Trigger){
                        Trigger trig = (Trigger)e;
                        if(trig.containsPos(Trident.player.getPos())){
                            Update.trigger(trig.id);
                        }
                    }
                }

                if(Trident.reset){
                    km.reset();
                    Trident.reset = false;
                }
                for(int i = 0; i < 255; i++){
                    Trident.keys[i] = km.getKeyDown(i);
                }
                Trident.m1 = km.getMouseDown(1);
                Trident.m2 = km.getMouseDown(2);
                Trident.m3 = km.getMouseDown(3);
                Trident.m4 = km.getMouseDown(4);
                Trident.m5 = km.getMouseDown(5);

                if(Trident.newSprite != null){
                    Trident.player = new Player(Trident.player.getPos(), km, 0.2, panel, Trident.newSprite, 16, 16);
                    Trident.newSprite = null;
                    Trident.player.resizeImages(32, 32);
                    Trident.player.camera.setDimension = new Dimension(frameManager.WIDTH, frameManager.HEIGHT);
                    Trident.camShake = new CamShake(Trident.player.camera);
                    Trident.player.shortCollision = true;
                }

                Update.update(server.getElapsedTime());
            }

            

            try {Trident.getEntities().sort((o1, o2) -> o2.compareSort(o1));} catch(Exception e){}
        }
    }
}
