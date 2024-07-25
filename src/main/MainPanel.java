package main;

import javax.swing.*;
import project.*;
import blib.util.*;
import blib.input.*;
import java.awt.event.*;
import java.awt.*;
import blib.game.*;
import java.io.*;
import project.ent.*;
import project.ent.Light;
import java.util.ArrayList;
public class MainPanel extends JPanel {

    Project project;
    Server server;
    FrameManager frameManager = new FrameManager();
    KeyManager km = new InputListener(this);
    Camera cam;
    ImageIcon icon = new ImageIcon("data/icon.png");
    int tool = 0;
    boolean drawLight = false;

    Cursor[] cursors = {
        BTools.getCustomCursor(new ImageIcon("data/images/cursors/select.png")),
        BTools.getCustomCursor(new ImageIcon("data/images/cursors/move.png")),
        BTools.getCustomCursor(new ImageIcon("data/images/cursors/resize.png")),
    };
    Point mouse = new Point(); // only used for better delta

    boolean dropDown = false;
    int dropType = 0;
    /*
     * 0 = trident button
     * 1 = add new object
     * 2 = delete object
     */
    Rectangle dropRect = null;
    ImageIcon selectImg = new ImageIcon("data/images/dropdown/select.png"), moveImg = new ImageIcon("data/images/dropdown/move.png"), resizeImg = new ImageIcon("data/images/dropdown/resize.png"), resetImg = new ImageIcon("data/images/dropdown/reset.png"), quitImg = new ImageIcon("data/images/dropdown/quit.png");
    ImageIcon deleteImg = new ImageIcon("data/images/dropdown/delete.png");
    ImageIcon boxImg = new ImageIcon("data/images/dropdown/box.png");
    ImageIcon boxCollImg = new ImageIcon("data/images/dropdown/boxColl.png");
    ImageIcon collisionImg = new ImageIcon("data/images/dropdown/collision.png");
    ImageIcon plrStartImg = new ImageIcon("data/images/dropdown/plrStart.png");
    ImageIcon customImg = new ImageIcon("data/images/dropdown/customEnt.png");
    ImageIcon duplicateImg = new ImageIcon("data/images/dropdown/duplicate.png");
    ImageIcon editImg = new ImageIcon("data/images/dropdown/edit.png");
    ImageIcon triggerImg = new ImageIcon("data/images/dropdown/trigger.png");
    ImageIcon lightImg = new ImageIcon("data/images/dropdown/light.png");

    ImageIcon crosshair = new ImageIcon("data/images/crosshair.png");
    boolean drawCross = true;

    long saveTime = 0;

    TridEntity selectedEntity = null;

    public MainPanel(Project proj){
        project = proj;

        BTools.resizeImgIcon(icon, 32, 32);
        cam = new Camera(new Position(), this);
        setBackground(Color.black);
        frameManager.bgColor = Color.white;
        cam.setDimension = new Dimension(frameManager.WIDTH, frameManager.HEIGHT);

        server = new Server(new ServerListener());
    }

    public void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        Graphics g = frameManager.newFrame();

        frameManager.bgColor = project.currentScene.bgColor;
        
        ArrayList<Entity> ents = new ArrayList<Entity>();
        for(TridEntity e: project.currentScene.entities) ents.add((Entity)e);
        cam.render(g, ents);
        for(TridEntity e: project.currentScene.entities){
            Point p = cam.worldToScreen(e.position);
            if(e.equals(selectedEntity)){
                g.setColor(new Color(0f, 0.2f, 1f, 0.5f));
                g.fillOval((int)((p.x / cam.getZoom()) - 32), (int)((p.y / cam.getZoom()) - 32), 64, 64);
            }
        }

        if(drawLight){
            int darkness = -project.currentScene.defaultLight + 255;
            frameManager.fillScreen(new Color(0f, 0f, 0f, (darkness / 255f)));
        }

        g.setColor(new Color(0f, 0f, 0f, 0.5f));
        g.fillRect(0, 0, frameManager.WIDTH, 40);
        g.fillRect(0, frameManager.HEIGHT - 40, frameManager.WIDTH, 40);
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        TextBox.draw("Code   Load Scene   New Scene   Save   PlrDir: " + project.currentScene.getDir(), g, 40, 20);
        icon.paintIcon(this, g, 4, 4);
        TextBox.draw("[L] Show Darkness: " + drawLight + "    Default light level: " + project.currentScene.defaultLight, g, frameManager.WIDTH - 10, frameManager.HEIGHT - 20, TextBox.RIGHT);
        g.setColor(new Color(1f, 1f, 1f, (saveTime / 1000f)));
        TextBox.draw("Saved!", g, 10, frameManager.HEIGHT - 20);
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 15));
        TextBox.draw("(" + cam.pos.toStringSimple() + ")\nx" + BTools.simplifyDouble(cam.getZoom()), g, frameManager.WIDTH, 10, TextBox.RIGHT);

        if(dropDown){
            g.setColor(Color.lightGray);
            g.fillRect(dropRect.x, dropRect.y, dropRect.width, dropRect.height);
            g.setColor(Color.gray);
            g.drawRect(dropRect.x, dropRect.y, dropRect.width, dropRect.height);
            for(int i = 0; i < dropRect.height / 32; i++){
                g.drawLine(dropRect.x, dropRect.y + (i * 32), dropRect.x + dropRect.width, dropRect.y + (i * 32));
            }
            
            g.setColor(Color.black);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            if(dropType == 0){
                selectImg.paintIcon(this, g, dropRect.x, dropRect.y);
                TextBox.draw("Select", g, dropRect.x + 32, dropRect.y + 16);
                moveImg.paintIcon(this, g, dropRect.x, dropRect.y + 32);
                TextBox.draw("Move", g, dropRect.x + 32, dropRect.y + 16 + 32);
                resizeImg.paintIcon(this, g, dropRect.x, dropRect.y + 64);
                TextBox.draw("Resize", g, dropRect.x + 32, dropRect.y + 16 + 64);
                resetImg.paintIcon(this, g, dropRect.x, dropRect.y + 64 + 32);
                TextBox.draw("Return to (0, 0)", g, dropRect.x + 32, dropRect.y + 16 + 64 + 32);
                editImg.paintIcon(this, g, dropRect.x, dropRect.y + 128);
                TextBox.draw("Change bg color", g, dropRect.x + 32, dropRect.y + 16 + 128);
                lightImg.paintIcon(this, g, dropRect.x, dropRect.y + 128 + 32);
                TextBox.draw("Set Default Light", g, dropRect.x + 32, dropRect.y + 16 + 128 + 32);
                quitImg.paintIcon(this, g, dropRect.x, dropRect.y + 128 + 64);
                TextBox.draw("Save and Quit", g, dropRect.x + 32, dropRect.y + 16 + 128 + 64);
            }
            if(dropType == 1){
                deleteImg.paintIcon(this, g, dropRect.x, dropRect.y);
                TextBox.draw("Delete", g, dropRect.x + 32, dropRect.y + 16);
                duplicateImg.paintIcon(this, g, dropRect.x, dropRect.y + 32);
                TextBox.draw("Duplicate", g, dropRect.x + 32, dropRect.y + 16 + 32);
                editImg.paintIcon(this, g, dropRect.x, dropRect.y + 64);
                TextBox.draw("Edit", g, dropRect.x + 32, dropRect.y + 16 + 64);
                resetImg.paintIcon(this, g, dropRect.x, dropRect.y + 64 + 32);
                TextBox.draw("Bring here", g, dropRect.x + 32, dropRect.y + 16 + 64 + 32);
            }
            if(dropType == 2){
                boxImg.paintIcon(this, g, dropRect.x, dropRect.y);
                TextBox.draw("Add BoxNoColl", g, dropRect.x + 32, dropRect.y + 16);
                boxCollImg.paintIcon(this, g, dropRect.x, dropRect.y + 32);
                TextBox.draw("Add BoxColl", g, dropRect.x + 32, dropRect.y + 16 + 32);
                collisionImg.paintIcon(this, g, dropRect.x, dropRect.y + 64);
                TextBox.draw("Add InvisColl", g, dropRect.x + 32, dropRect.y + 16 + 64);
                plrStartImg.paintIcon(this, g, dropRect.x, dropRect.y + 64 + 32);
                TextBox.draw("Add PlrStart", g, dropRect.x + 32, dropRect.y + 16 + 64 + 32);
                triggerImg.paintIcon(this, g, dropRect.x, dropRect.y + 128);
                TextBox.draw("Add Trigger", g, dropRect.x + 32, dropRect.y + 16 + 128);
                customImg.paintIcon(this, g, dropRect.x, dropRect.y + 128 + 32);
                TextBox.draw("Add Custom Entity", g, dropRect.x + 32, dropRect.y + 16 + 128 + 32);
                lightImg.paintIcon(this, g, dropRect.x, dropRect.y + 128 + 64);
                TextBox.draw("Add Light", g, dropRect.x + 32, dropRect.y + 16 + 128 + 64);
            }
        }

        if(drawCross) crosshair.paintIcon(this, g, frameManager.WIDTH / 2 - 4, frameManager.HEIGHT / 2 - 4);

        frameManager.renderFrame(this, graphics);
    }

    public int getDropSel(int y){
        return (y - dropRect.y) / 32;
    }
    

    private class InputListener extends InputAdapter{
        public InputListener(JPanel panel){
            super(panel);
        }

        public void onKeyPressed(int key){
            if(key == KeyEvent.VK_R){
                cam.pos = new Position();
            }
            if(key == KeyEvent.VK_1){
                tool = 0;
            }
            if(key == KeyEvent.VK_2){
                tool = 1;
            }
            if(key == KeyEvent.VK_3){
                tool = 2;
            }
            if(key == KeyEvent.VK_DELETE && selectedEntity != null){
                project.currentScene.entities.remove(selectedEntity);
                selectedEntity = null;
            }
            if(key == KeyEvent.VK_M && km.getKeyDown(KeyEvent.VK_CONTROL)){
                project.currentScene.save("data/projects/" + project.name);
            }
            if(key == KeyEvent.VK_C && km.getKeyDown(KeyEvent.VK_CONTROL)){
                if(selectedEntity instanceof BoxColl){
                    BoxColl box = (BoxColl)selectedEntity;
                    project.currentScene.entities.add(new BoxColl(box.position.copy(), new Dimension(box.collision.width, box.collision.height), box.color));
                    selectedEntity = project.currentScene.entities.get(project.currentScene.entities.size() - 1);
                }
                if(selectedEntity instanceof BoxNoColl){
                    BoxNoColl box = (BoxNoColl)selectedEntity;
                    project.currentScene.entities.add(new BoxNoColl(box.position.copy(), box.color, box.width, box.height));
                    selectedEntity = project.currentScene.entities.get(project.currentScene.entities.size() - 1);
                }
                if(selectedEntity instanceof InvisColl){
                    InvisColl box = (InvisColl)selectedEntity;
                    project.currentScene.entities.add(new InvisColl(box.position.copy(), new Dimension(box.collision.width, box.collision.height)));
                    selectedEntity = project.currentScene.entities.get(project.currentScene.entities.size() - 1);
                }
                if(selectedEntity instanceof Trigger){
                    Trigger box = (Trigger)selectedEntity;
                    project.currentScene.entities.add(new Trigger(box.position.copy(), new Dimension(box.box.width, box.box.height), box.id));
                    selectedEntity = project.currentScene.entities.get(project.currentScene.entities.size() - 1);
                }
                if(selectedEntity instanceof PlrStart){
                    PlrStart box = (PlrStart)selectedEntity;
                    project.currentScene.entities.add(new PlrStart(box.position.copy()));
                    selectedEntity = project.currentScene.entities.get(project.currentScene.entities.size() - 1);
                }
                if(selectedEntity instanceof Light){
                    Light box = (Light)selectedEntity;
                    project.currentScene.entities.add(new Light(box.position.copy(), box.radius));
                    selectedEntity = project.currentScene.entities.get(project.currentScene.entities.size() - 1);
                }
                if(selectedEntity instanceof CustomEntity){
                    CustomEntity box = (CustomEntity)selectedEntity;
                    int[] data = new int[box.data.length];
                    for(int i = 0; i < data.length; i++){
                        data[i] = box.data[i];
                    }
                    Dimension collision = null;
                    if(box.collision != null) collision = new Dimension(box.collision.width, box.collision.height);
                    project.currentScene.entities.add(new CustomEntity(box.position.copy(), collision, data, box.name));
                    selectedEntity = project.currentScene.entities.get(project.currentScene.entities.size() - 1);
                }
            }
            if(key == KeyEvent.VK_L){
                drawLight = !drawLight;
            }
            if(key == KeyEvent.VK_C && !km.getKeyDown(KeyEvent.VK_CONTROL)){
                drawCross = !drawCross;
            }
        }

        public void onMousePressed(int mb, Point mousePos){
            mousePos = frameManager.getMousePos(panel, mousePos);
            if(mousePos.equals(new Point())) return; // likely outside of frame

            if(mb == 1){
                if(dropDown){
                    if(dropRect.contains(mousePos)){
                        int sel = getDropSel(mousePos.y);
                        if(dropType == 0){
                            switch(sel){
                            case 0:
                            case 1:
                            case 2:
                                tool = sel;
                                break;
                            case 3:
                                cam.pos = new Position();
                                break;
                            case 4:
                                try{
                                    int r, g, b;
                                    String input = JOptionPane.showInputDialog(panel, "Enter the red value.", "Trident", JOptionPane.QUESTION_MESSAGE);
                                    r = Integer.parseInt(input);
                                    input = JOptionPane.showInputDialog(panel, "Enter the green value.", "Trident", JOptionPane.QUESTION_MESSAGE);
                                    g = Integer.parseInt(input);
                                    input = JOptionPane.showInputDialog(panel, "Enter the blue value.", "Trident", JOptionPane.QUESTION_MESSAGE);
                                    b = Integer.parseInt(input);
                                    Color c = new Color(r, g, b);
                                    project.currentScene.bgColor = c;
                                }catch(Exception e){}
                                break;
                            case 5:
                                try{
                                    int level;
                                    String input = JOptionPane.showInputDialog(panel, "Enter the new default light level", "Trident", JOptionPane.QUESTION_MESSAGE);
                                    level = Integer.parseInt(input);
                                    level = BTools.clamp(level, 0, 255);
                                    project.currentScene.defaultLight = level;
                                }catch(Exception e){}
                                break;
                            case 6:
                                project.currentScene.save("data/projects/" + project.name);
                                Main.window.dispose();
                                Main.main(new String[0]);
                                return;
                            }
                        }
                        if(dropType == 1){
                            switch(sel){
                            case 0:
                                project.currentScene.entities.remove(selectedEntity);
                                selectedEntity = null;
                                break;
                            case 1:
                                if(selectedEntity instanceof BoxColl){
                                    BoxColl box = (BoxColl)selectedEntity;
                                    project.currentScene.entities.add(new BoxColl(box.position.copy(), new Dimension(box.collision.width, box.collision.height), box.color));
                                    selectedEntity = project.currentScene.entities.get(project.currentScene.entities.size() - 1);
                                }
                                if(selectedEntity instanceof BoxNoColl){
                                    BoxNoColl box = (BoxNoColl)selectedEntity;
                                    project.currentScene.entities.add(new BoxNoColl(box.position.copy(), box.color, box.width, box.height));
                                    selectedEntity = project.currentScene.entities.get(project.currentScene.entities.size() - 1);
                                }
                                if(selectedEntity instanceof InvisColl){
                                    InvisColl box = (InvisColl)selectedEntity;
                                    project.currentScene.entities.add(new InvisColl(box.position.copy(), new Dimension(box.collision.width, box.collision.height)));
                                    selectedEntity = project.currentScene.entities.get(project.currentScene.entities.size() - 1);
                                }
                                if(selectedEntity instanceof Trigger){
                                    Trigger box = (Trigger)selectedEntity;
                                    project.currentScene.entities.add(new Trigger(box.position.copy(), new Dimension(box.box.width, box.box.height), box.id));
                                    selectedEntity = project.currentScene.entities.get(project.currentScene.entities.size() - 1);
                                }
                                if(selectedEntity instanceof PlrStart){
                                    PlrStart box = (PlrStart)selectedEntity;
                                    project.currentScene.entities.add(new PlrStart(box.position.copy()));
                                    selectedEntity = project.currentScene.entities.get(project.currentScene.entities.size() - 1);
                                }
                                if(selectedEntity instanceof Light){
                                    Light box = (Light)selectedEntity;
                                    project.currentScene.entities.add(new Light(box.position.copy(), box.radius));
                                    selectedEntity = project.currentScene.entities.get(project.currentScene.entities.size() - 1);
                                }
                                if(selectedEntity instanceof CustomEntity){
                                    CustomEntity box = (CustomEntity)selectedEntity;
                                    int[] data = new int[box.data.length];
                                    for(int i = 0; i < data.length; i++){
                                        data[i] = box.data[i];
                                    }
                                    Dimension collision = null;
                                    if(box.collision != null) collision = new Dimension(box.collision.width, box.collision.height);
                                    project.currentScene.entities.add(new CustomEntity(box.position.copy(), collision, data, box.name));
                                    selectedEntity = project.currentScene.entities.get(project.currentScene.entities.size() - 1);
                                }
                                break;
                            case 2:
                                if(selectedEntity instanceof BoxColl){
                                    try{
                                        int[] data = new int[3];
                                        for(int i = 0; i < 3; i++){
                                            String input = JOptionPane.showInputDialog(panel, "Enter data[" + i + "]", "Trident", JOptionPane.QUESTION_MESSAGE);
                                            data[i] = Integer.parseInt(input);
                                        }
                                        BoxColl box = (BoxColl)selectedEntity;
                                        box.color = new Color(data[0], data[1], data[2]);
                                    }catch(Exception e){}
                                }
                                if(selectedEntity instanceof BoxNoColl){
                                    try{
                                        int[] data = new int[3];
                                        for(int i = 0; i < 3; i++){
                                            String input = JOptionPane.showInputDialog(panel, "Enter data[" + i + "]", "Trident", JOptionPane.QUESTION_MESSAGE);
                                            data[i] = Integer.parseInt(input);
                                        }
                                        BoxNoColl box = (BoxNoColl)selectedEntity;
                                        box.color = new Color(data[0], data[1], data[2]);
                                    }catch(Exception e){}
                                }
                                if(selectedEntity instanceof CustomEntity){
                                    try{
                                        CustomEntity c = (CustomEntity)selectedEntity;
                                        int[] data = new int[c.data.length];
                                        for(int i = 0; i < c.data.length; i++){
                                            String input = JOptionPane.showInputDialog(panel, "Enter data[" + i + "]", "Trident", JOptionPane.QUESTION_MESSAGE);
                                            data[i] = Integer.parseInt(input);
                                        }
                                        c.data = data;
                                    }catch(Exception e){}
                                }
                                if(selectedEntity instanceof Trigger){
                                    try{
                                        int[] data = new int[1];
                                        for(int i = 0; i < 1; i++){
                                            String input = JOptionPane.showInputDialog(panel, "Enter data[" + i + "]", "Trident", JOptionPane.QUESTION_MESSAGE);
                                            data[i] = Integer.parseInt(input);
                                        }
                                        Trigger box = (Trigger)selectedEntity;
                                        box.id = data[0];
                                    }catch(Exception e){}
                                }
                                break;
                            case 3:
                                Position worldPos = cam.mouseToPos(new Point(dropRect.x, dropRect.y));
                                selectedEntity.position = worldPos;
                                break;
                            }
                        }
                        if(dropType == 2){
                            Position worldPos = cam.mouseToPos(new Point(dropRect.x, dropRect.y));
                            switch(sel){
                            case 0:
                                project.currentScene.entities.add(new BoxNoColl(worldPos, Color.black, 100, 100));
                                break;
                            case 1:
                                project.currentScene.entities.add(new BoxColl(worldPos, new Dimension(100, 100), Color.black));
                                break;
                            case 2:
                                project.currentScene.entities.add(new InvisColl(worldPos, new Dimension(100, 100)));
                                break;
                            case 3:
                                project.currentScene.entities.add(new PlrStart(worldPos));
                                break;
                            case 4:
                                try{
                                    String input = JOptionPane.showInputDialog(panel, "Enter the ID for the trigger", "Trident", JOptionPane.QUESTION_MESSAGE);
                                    int id = Integer.parseInt(input);
                                    project.currentScene.entities.add(new Trigger(worldPos, new Dimension(100, 100), id));
                                }catch(Exception e){}
                                break;
                            case 5:
                                String name = JOptionPane.showInputDialog(panel, "Enter the object name", "Trident", JOptionPane.QUESTION_MESSAGE);
                                if(name != null){
                                    int coll = JOptionPane.showConfirmDialog(panel, "Does the object have collision?", "Trident", JOptionPane.YES_NO_CANCEL_OPTION);
                                    if(coll == 2) break;
                                    Dimension collider = null;
                                    if(coll == 0) collider = new Dimension(100, 100);

                                    String input = JOptionPane.showInputDialog(panel, "Enter the amount of data you need", "Trident", JOptionPane.QUESTION_MESSAGE);
                                    try{
                                        int numData = Integer.parseInt(input);
                                        int[] data = new int[numData];
                                        for(int i = 0; i < numData; i++){
                                            String dataStr = JOptionPane.showInputDialog(panel, "Enter data[" + i + "]", "Trident", JOptionPane.QUESTION_MESSAGE);
                                            data[i] = Integer.parseInt(dataStr);
                                        }
                                        project.currentScene.entities.add(new CustomEntity(worldPos, collider, data, name));
                                        System.out.println("created entity");
                                    }catch(Exception e){
                                        e.printStackTrace();
                                        dropDown = false;
                                        return;
                                    }
                                }
                                break;
                            case 6:
                                project.currentScene.entities.add(new project.ent.Light(worldPos, 50));
                                break;
                            }
                        }

                        dropDown = false;
                        return;
                    }else{
                        dropDown = false;
                        return;
                    }
                }

                if(mousePos.y < 40){
                    System.out.println(mousePos.x);
                    // Toolbar
                    if(mousePos.x < 40){
                        dropDown = true;
                        dropType = 0;
                        dropRect = new Rectangle(0, 40, 200, 32 * 7);
                    }else if(mousePos.x < 90){
                        BTools.openHighlightFile(new File("data/projects/" + project.name).getAbsolutePath());
                    }else if(mousePos.x < 212){
                        String name = JOptionPane.showInputDialog(panel, "Enter the scene name", "Trident", JOptionPane.QUESTION_MESSAGE);
                        if(name != null){
                            boolean loaded = project.loadScene(name);
                            if(!loaded){
                                JOptionPane.showMessageDialog(panel, "Error: no scene with name '" + name + "' found.", "Trident", JOptionPane.ERROR_MESSAGE);
                            }else{
                                cam.pos = new Position();
                            }
                        }
                    }else if(mousePos.x < 327){
                        String name = JOptionPane.showInputDialog(panel, "Enter the scene name", "Trident", JOptionPane.QUESTION_MESSAGE);
                        if(name != null){
                            Scene scene = new Scene(name);
                            scene.save("data/projects/" + project.name);
                            project.setupScenes();
                            project.loadScene(name);
                            cam.pos = new Position();
                        }
                    }else if(mousePos.x < 391){
                        project.currentScene.save("data/projects/" + project.name);
                        saveTime = 1000;
                    }else if(mousePos.x < 520){
                        project.currentScene.plrDir++;
                        if(project.currentScene.plrDir > 3) project.currentScene.plrDir = 0;
                    }else{
                        try{
                            double x, y;
                            String inputX = JOptionPane.showInputDialog(panel, "Enter the x position", "Trident", JOptionPane.QUESTION_MESSAGE);
                            if(inputX != null){
                                x = Double.parseDouble(inputX);
                                String inputY = JOptionPane.showInputDialog(panel, "Enter the y position", "Trident", JOptionPane.QUESTION_MESSAGE);
                                if(inputY != null){
                                    y = Double.parseDouble(inputY);
                                    cam.pos = new Position(x, y);
                                }
                            }
                        }catch(Exception e){
                            JOptionPane.showMessageDialog(panel, "Invalid input", "Trident", JOptionPane.ERROR_MESSAGE);
                        }
                        
                        
                    }
                }else{
                    if(tool == 0){
                        if(selectedEntity != null){
                            selectedEntity = null;
                        }
                        Position worldPos = cam.mouseToPos(mousePos);
                        for(TridEntity e: project.currentScene.entities){
                            if(BTools.getDistance(e.position, worldPos) < 32){
                                selectedEntity = e;
                                return;
                            }
                        }
                    }
                }
            }
            if(mb == 2) cam.setZoom(1);
            if(mb == 3){
                if(selectedEntity == null){
                    dropDown = true;
                    dropType = 2;
                    dropRect = new Rectangle(mousePos.x, mousePos.y, 200, 32 * 7);
                }else{
                    dropDown = true;
                    dropType = 1;
                    dropRect = new Rectangle(mousePos.x, mousePos.y, 200, 32 * 4);
                }
            }

        }

        public void onScroll(int scroll){
            double zDelta = scroll * 0.1;
            if(km.getKeyDown(KeyEvent.VK_SHIFT)) zDelta *= 2;
            if(km.getKeyDown(KeyEvent.VK_CONTROL)) zDelta /= 2;
            cam.setZoom(BTools.clamp(cam.getZoom() + zDelta, 0.1, 15));
        }
    }

    MainPanel parent = this;
    private class ServerListener implements ActionListener{
        public void actionPerformed(ActionEvent event){

            Point delta = new Point(-mouse.x + frameManager.getMousePos(parent, km.getMousePos()).x, -mouse.y + frameManager.getMousePos(parent, km.getMousePos()).y);
            mouse = frameManager.getMousePos(parent, km.getMousePos());

            // Movement
            Point dir = new Point();
            if(km.getKeyDown(KeyEvent.VK_W)) dir.y--;
            if(km.getKeyDown(KeyEvent.VK_S)) dir.y++;
            if(km.getKeyDown(KeyEvent.VK_A)) dir.x--;
            if(km.getKeyDown(KeyEvent.VK_D)) dir.x++;
            double speed = 0.5;
            if(km.getKeyDown(KeyEvent.VK_SHIFT)) speed *= 2;
            if(km.getKeyDown(KeyEvent.VK_CONTROL)) speed /= 2;
            cam.pos.x += dir.x * server.getElapsedTime() * speed * cam.getZoom();
            cam.pos.y += dir.y * server.getElapsedTime() * speed * cam.getZoom();



            saveTime -= server.getElapsedTime();
            if(saveTime < 0) saveTime = 0;

            if(tool == 1 && selectedEntity != null && km.getMouseDown(1)){
                selectedEntity.position.x += delta.x;
                selectedEntity.position.y += delta.y;

                
                selectedEntity.position.x += dir.x * server.getElapsedTime() * speed;
                selectedEntity.position.y += dir.y * server.getElapsedTime() * speed;
            }
            if(tool == 2 && selectedEntity != null && km.getMouseDown(1)){
                if(selectedEntity instanceof project.ent.Light){
                    project.ent.Light light = (project.ent.Light)selectedEntity;
                    light.radius += delta.x;
                    if(light.radius < 0) light.radius = 0;
                }
                if(selectedEntity instanceof BoxNoColl){
                    BoxNoColl box = (BoxNoColl)selectedEntity;
                    box.width += delta.x;
                    box.height -= delta.y;
                    if(box.width < 0) box.width = 0;
                    if(box.height < 0) box.height = 0;
                }
                if(selectedEntity instanceof Trigger){
                    Trigger trig = (Trigger)selectedEntity;
                    trig.box.width += delta.x;
                    trig.box.height -= delta.y;
                    if(trig.box.width < 0) trig.box.width = 0;
                    if(trig.box.height < 0) trig.box.height = 0;
                }
                if(selectedEntity.HASCOLLISION){
                    selectedEntity.collision.width += delta.x;
                    selectedEntity.collision.height -= delta.y;
                    if(selectedEntity.collision.width < 0) selectedEntity.collision.width = 0;
                    if(selectedEntity.collision.height < 0) selectedEntity.collision.height = 0;
                }
            }

            setCursor(cursors[tool]);

            repaint();
        }
    }
}
