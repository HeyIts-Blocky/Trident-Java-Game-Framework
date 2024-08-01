package trident;

import blib.game.*;
import blib.util.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;
import update.*;
import java.io.*;
import trident.ent.*;
import com.jhlabs.image.GlowFilter;
import com.jhlabs.image.ExposureFilter;
public class Trident {

    // Debug Settings
    public static boolean drawPos = false;
    public static boolean drawCollision = false;
    public static boolean noclip = false;
    public static boolean engineDraw = false;
    public static Color debugColor = Color.red;
    public static boolean intro = true;
    public static ImageIcon splash = null;
    public static boolean drawFrames = false;
    public static boolean consoleEnabled = true;
    public static boolean consoleOpen = false;
    public static boolean consoleError = false;
    public static String lastCommand = "";
    
    // Public Variables
    public static Point mousePos;
    public static Point mouseDelta;
    public static boolean drawPlayer = true;
    public static Position mouseWorldPos = new Position();
    public static boolean enableBloom = true, enableExposure = true;


    // Trident Variables
    protected static Player player;
    protected static Scene currentScene;
    protected static boolean fullscreen = false;
    protected static ArrayList<Scene> loadedScenes = new ArrayList<Scene>();
    protected static ArrayList<TridEntity> entRegistry = new ArrayList<TridEntity>();
    protected static String defaultScene = "default";
    protected static boolean m1 = false, m2 = false, m3 = false, m4 = false, m5 = false;
    protected static boolean[] keys = new boolean[255];
    protected static CamShake camShake;
    protected static ArrayList<Entity> lights = new ArrayList<Entity>();
    protected static LightManager lightManager = new LightManager(255);
    protected static GlowFilter bloom = new GlowFilter();
    protected static ExposureFilter exposure = new ExposureFilter();
    protected static boolean reset = false;
    protected static String newSprite = null;

    // Trident events
    public static final int EVENT_SCREENSHOT = 0, EVENT_FULLSCREEN = 1;


    // Setting methods
    public static void setPlrSpeed(double speed){
        player.speed = speed;
    }
    public static void setPlrPos(Position pos){
        player.goToPos(pos);
    }
    public static void setShortCollision(boolean b){
        player.shortCollision = b;
    }
    public static void setWindowTitle(String title){
        Main.window.setTitle(title);
    }
    public static void setupScenes(){
        try{
            loadedScenes = new ArrayList<Scene>();
            File dir = new File("data/scenes");
            File[] files = dir.listFiles();
            ArrayList<File> sceneFiles = new ArrayList<File>();
            for(File f: files){
                if(BTools.hasExtension(f, "bson")) sceneFiles.add(f);
            }

            for(File f: sceneFiles){
                loadedScenes.add(new Scene(f));
            }
        }catch(Exception e){
            printException("Error setting up scenes", e);
        }
        
    }
    public static void loadScene(String name){
        loadScene(name, false);
    }
    public static void loadScene(String name, boolean unload){
        lights = new ArrayList<Entity>();
        for(Scene s: loadedScenes){
            if(s.name.equals(name)){
                Scene oldScene = currentScene;

                if(!s.loaded) s.preload();
                currentScene = s;
                if(unload) oldScene.unload();
                player.goToPos(s.plrStart);
                player.setDirection(s.plrDir);
                for(TridEntity e: s.entities){
                    if(e instanceof TridLight){
                        TridLight asLight = (TridLight)e;
                        lights.add(new Light(asLight.position, asLight.radius));
                    }
                    e.sceneStart(s.name);
                }
                lightManager.defaultLight = s.defaultLight;
                Update.sceneStart(name);
                return;
            }
        }
        printConsole("***********************************************************************************");
        printConsole("Error loading scene: No scene with name '" + name + "' found.");
        printError("***********************************************************************************");
    }
    public static void unloadScene(String name){
        for(Scene s: loadedScenes){
            if(s.name.equals(name)){
                if(s.equals(currentScene)){
                    printError("ERROR: tried to unload current scene");
                    return;
                }else{
                    s.unload();
                    return;
                }
            }
        }
        printConsole("***********************************************************************************");
        printConsole("Error unloading scene: No scene with name '" + name + "' found.");
        printError("***********************************************************************************");
    }
    public static void preloadScene(String name){
        for(Scene s: loadedScenes){
            if(s.name.equals(name)){
                s.preload();
                return;
            }
        }
        printConsole("***********************************************************************************");
        printConsole("Error preloading scene: No scene with name '" + name + "' found.");
        printError("***********************************************************************************");
    }
    public static void addCustomEntity(TridEntity e){ // Add a cutsom entity to the registry
        entRegistry.add(e);
    }
    public static void spawnEntity(TridEntity e){
        currentScene.entities.add(e);
    }
    public static void setDefaultScene(String s){
        defaultScene = s;
    }
    public static void destroy(TridEntity object){
        getEntities().remove(object);
    }
    public static void shakeCam(double intensity){
        camShake.addTrauma(intensity);
    }
    public static void removeShake(){
        camShake.trauma = 0;
    }
    public static void setShakeStrength(int str){
        camShake.strength = str;
    }
    public static void setShakeLoss(double loss){
        camShake.traumaLoss = loss;
    }
    public static void setBloom(double amount){
        bloom.setAmount((float)amount);
    }
    public static void setExposure(double exp){
        exposure.setExposure((float)exp);
    }
    public static void setLightBlur(int level){
        lightManager.blur.setIterations(level);
    }
    public static void addLight(Light l){
        lights.add(l);
    }
    public static void resetKeys(){
        reset = true;
    }
    public static void setPlrSprite(String path){
        newSprite = path;
    }
    public static void removeLight(Light l){
        lights.remove(l);
    }
    public static void setDefaultLight(int level){
        lightManager.defaultLight = level;
    }
    public static void stopShake(){
        camShake.trauma = 0;
    }

    // Getting methods
    public static double getPlrSpeed(){
        return player.speed;
    }
    public static Position getPlrPos(){
        return player.getPos().copy();
    }
    public static Scene getCurrentScene(){
        return currentScene;
    }
    public static boolean getFullscreen(){
        return fullscreen; 
    }
    public static ArrayList<Entity> tridArrToEntArr(ArrayList<TridEntity> entities){
        ArrayList<Entity> newEntities = new ArrayList<Entity>();
        for(TridEntity e: entities){
            newEntities.add((Entity)e);
        }
        return newEntities;
    }
    public static ArrayList<TridEntity> entArrToTridArr(ArrayList<Entity> entities){
        ArrayList<TridEntity> newEntities = new ArrayList<TridEntity>();
        for(Entity e: entities){
            newEntities.add((TridEntity)e);
        }
        return newEntities;
    }
    public static ArrayList<TridEntity> getEntities(){
        return currentScene.entities;
    }
    public static ArrayList<Rectangle> getCollision(){
        return currentScene.getCollision();
    }
    public static boolean getMouseDown(int mb){
        if(mb == 1){
            return m1;
        }
        if(mb == 2){
            return m2;
        }
        if(mb == 3){
            return m3;
        }
        if(mb == 4){
            return m4;
        }
        if(mb == 5){
            return m5;
        }
        return false;
    }
    public static boolean getKeyDown(int key){
        return keys[key];
    }
    public static Player getPlr(){
        return player;
    }
    public static int getFrameWidth(){
        return 684;
    }
    public static int getFrameHeight(){
        return 462;
    }
    public static Point getShakeOffset(){
        return new Point(camShake.offX, camShake.offY);
    }


    // Commands
    public static ArrayList<String> consoleLines = new ArrayList<String>();
    public static String consoleType = ""; // What the user is typing

    public static void runCommand(String command){
        if(command != null && command.length() > 0){
            lastCommand = command;
            printConsole(" > " + command);

            ArrayList<String> cmdParts = new ArrayList<String>();
            Scanner scanner = new Scanner(command);
            while(scanner.hasNext()){
                cmdParts.add(scanner.next());
            }
            scanner.close();

            if(cmdParts.size() == 0) return;
            try{
            switch(cmdParts.get(0)){
            case "drawCollision":
                if(cmdParts.size() == 1){
                    printConsole("drawCollision is " + Trident.drawCollision);
                    break;
                }
                if(cmdParts.get(1).equals("1") || cmdParts.get(1).equals("true")){
                    Trident.drawCollision = true;
                    printConsole("set drawCollision to " + Trident.drawCollision);
                }
                if(cmdParts.get(1).equals("0") || cmdParts.get(1).equals("false")){
                    Trident.drawCollision = false;
                    printConsole("set drawCollision to " + Trident.drawCollision);
                }
                break;
            case "engineDraw":
                if(cmdParts.size() == 1){
                    printConsole("engineDraw is " + Trident.engineDraw);
                    break;
                }
                if(cmdParts.get(1).equals("1") || cmdParts.get(1).equals("true")){
                    Trident.engineDraw = true;
                    printConsole("set engineDraw to " + Trident.engineDraw);
                }
                if(cmdParts.get(1).equals("0") || cmdParts.get(1).equals("false")){
                    Trident.engineDraw = false;
                    printConsole("set engineDraw to " + Trident.engineDraw);
                }
                break;
            case "drawPos":
                if(cmdParts.size() == 1){
                    printConsole("drawPos is " + Trident.drawPos);
                    break;
                }
                if(cmdParts.get(1).equals("1") || cmdParts.get(1).equals("true")){
                    Trident.drawPos = true;
                    printConsole("set drawPos to " + Trident.drawPos);
                }
                if(cmdParts.get(1).equals("0") || cmdParts.get(1).equals("false")){
                    Trident.drawPos = false;
                    printConsole("set drawPos to " + Trident.drawPos);
                }
                break;
            case "noclip":
                if(cmdParts.size() == 1){
                    printConsole("noclip is " + Trident.noclip);
                    break;
                }
                if(cmdParts.get(1).equals("1") || cmdParts.get(1).equals("true")){
                    Trident.noclip = true;
                    printConsole("set noclip to " + Trident.noclip);
                }
                if(cmdParts.get(1).equals("0") || cmdParts.get(1).equals("false")){
                    Trident.noclip = false;
                    printConsole("set noclip to " + Trident.noclip);
                }
                break;
            case "tp":
                int x = Integer.parseInt(cmdParts.get(1));
                int y = Integer.parseInt(cmdParts.get(2));
                Trident.setPlrPos(new Position(x, y));
                printConsole("teleported player to (" + x + ", " + y + ")");
                break;
            case "loadMap":
                String map = "";
                for(int i = 1; i < cmdParts.size(); i++){
                    map += cmdParts.get(i);
                    if(i != cmdParts.size() - 1) map += " ";
                }
                Trident.loadScene(map);
                break;
            case "drawFrames":
                if(cmdParts.size() == 1){
                    printConsole("drawFrames is " + Trident.drawFrames);
                    break;
                }
                if(cmdParts.get(1).equals("1") || cmdParts.get(1).equals("true")){
                    Trident.drawFrames = true;
                    printConsole("set drawFrames to " + Trident.drawFrames);
                }
                if(cmdParts.get(1).equals("0") || cmdParts.get(1).equals("false")){
                    Trident.drawFrames = false;
                    printConsole("set drawFrames to " + Trident.drawFrames);
                }
                break;
            case "debugColor":
                int r, g, b;
                float alpha = -1;
                r = Integer.parseInt(cmdParts.get(1));
                g = Integer.parseInt(cmdParts.get(2));
                b = Integer.parseInt(cmdParts.get(3));
                if(cmdParts.size() == 5){
                    alpha = Float.parseFloat(cmdParts.get(4));
                }
                if(alpha != -1){
                    Trident.debugColor = new Color(r / 255f, g / 255f, b / 255f, alpha);
                    printConsole("set debugColor to (" + r + ", " + g + ", " + b + ", " + alpha + ")");
                }else{
                    Trident.debugColor = new Color(r, g, b);
                    printConsole("set debugColor to (" + r + ", " + g + ", " + b + ")");
                }
                break;
            case "enableBloom":
                if(cmdParts.size() == 1){
                    printConsole("enableBloom is " + Trident.enableBloom);
                    break;
                }
                if(cmdParts.get(1).equals("1") || cmdParts.get(1).equals("true")){
                    Trident.enableBloom = true;
                    printConsole("set enableBloom to " + Trident.enableBloom);
                }
                if(cmdParts.get(1).equals("0") || cmdParts.get(1).equals("false")){
                    Trident.enableBloom = false;
                    printConsole("set enableBloom to " + Trident.enableBloom);
                }
                break;
            case "enableExposure":
                if(cmdParts.size() == 1){
                    printConsole("enableExposure is " + Trident.enableExposure);
                    break;
                }
                if(cmdParts.get(1).equals("1") || cmdParts.get(1).equals("true")){
                    Trident.enableExposure = true;
                    printConsole("set enableExposure to " + Trident.enableExposure);
                }
                if(cmdParts.get(1).equals("0") || cmdParts.get(1).equals("false")){
                    Trident.enableExposure = false;
                    printConsole("set enableExposure to " + Trident.enableExposure);
                }
                break;
            case "setBloom":
                double amount = Double.parseDouble(cmdParts.get(1));
                Trident.setBloom(amount);
                printConsole("set bloom to " + amount);
                break;
            case "setExposure":
                double expo = Double.parseDouble(cmdParts.get(1));
                Trident.setExposure(expo);
                printConsole("set exposure to " + expo);
                break;
            case "setLightBlur":
                int blurLevel = Integer.parseInt(cmdParts.get(1)); 
                Trident.setLightBlur(blurLevel);
                printConsole("set lightBlur to " + blurLevel + " (recommended: 1)");
                break;
            case "clear":
                consoleLines = new ArrayList<String>();
                break;
            case "help":
                int page = 1;
                if(cmdParts.size() > 1) page = Integer.parseInt(cmdParts.get(1));
                printHelp(page);
                break;
            case "customHelp":
                int p = 1;
                if(cmdParts.size() > 1) p = Integer.parseInt(cmdParts.get(1));
                printCustomHelp(p);
                break;
            case "credits":
                runCommand("clear");
                printConsole("Trident Engine built in Java by Blocky");
                printConsole("---");
                printConsole("Github: @HeyIts-Blocky");
                printConsole("Insta: @heyits_blocky");
                printConsole("Itch.io: blockmanblue.itch.io  <TYPE 'openItch' TO GO TO SITE>");
                printConsole("---");
                printConsole("");
                break;
            case "openItch":
                BTools.openWebsite("https://blockmanblue.itch.io/");
                break;
            case "errorTest":
                consoleOpen = false;
                printError("Error test");
                break;
            case "roll":
                printConsole("You got " + BTools.randInt(0, 101) + " points");
                break;
            case "trigger":
                int trig = Integer.parseInt(cmdParts.get(1));
                Update.trigger(trig);
                printConsole("ran trigger " + trig);
                break;
            case "mapList":
                page = 1;
                if(cmdParts.size() > 1) page = Integer.parseInt(cmdParts.get(1));
                printMaps(page);
                break;
            case "unloadMap":
                unloadScene(cmdParts.get(1));
                break;
            case "preloadMap":
                preloadScene(cmdParts.get(1));
                break;
            case "currentMap":
                printConsole("Current map: " + currentScene.name);
                break;
            default:
                int cmd = Update.command(cmdParts);
                if(cmd != 0){
                    printConsole("Unknown command: " + cmdParts.get(0));
                }
                break;
            }
            }catch(Exception e){
                printConsole("Something went wrong while running your command.");
                printException("", e);
            }
        }
    }
    public static void printConsole(String text){
        consoleLines.add(text);
        if(consoleLines.size() > 30) consoleLines.remove(0);
    }
    public static void printError(String text){
        consoleError = true;
        printConsole(text);
    }
    public static void printException(String text, Exception e){
        consoleError = true;
        printConsole(text);
        printConsole("Error type: " + e.getClass().getName());
    }
    public static void printExceptionSilent(String text, Exception e){
        printConsole(text);
        printConsole("Error type: " + e.getClass().getName());
    }
    private static String[] cmds = {
        "credits",
        "drawCollision [0/1, false/true]",
        "engineDraw [0/1, false/true]",
        "drawPos [0/1, false/true]",
        "noclip [0/1, false/true]",
        "tp <x> <y>",
        "loadMap <name>",
        "drawFrames [0/1, false/true]",
        "debugColor <r> <g> <b> [a]",
        "enableBloom [0/1, false/true]",
        "enableExposure [0/1, false/true]",
        "setBloom <amount>",
        "setExposure <amount>",
        "setLightBlur <amount>",
        "clear",
        "help [page]",
        "customHelp [page]",
        "errorTest",
        "roll",
        "trigger <trig>",
        "mapList [page]",
        "unloadMap <name>",
        "preloadMap <name>",
        "currentMap",
    };

    public static void printHelp(int page){
        int startIndex = (page - 1) * 10;
        if(startIndex > cmds.length - 1){
            printConsole("page beyond bounds: " + page);
            printConsole("number of pages: " + (cmds.length / 10 + ((cmds.length % 10 != 0) ? 1 : 0)));
            return;
        }
        
        printConsole("-- BUILT-IN COMMANDS --");
        printConsole("Page " + page + " of " + (cmds.length / 10 + ((cmds.length % 10 != 0) ? 1 : 0)));
        printConsole("");
        for(int i = startIndex; (i < cmds.length && i < startIndex + 10); i++){
            printConsole("} " + cmds[i]);
        }
        printConsole("");
    }

    public static void printMaps(int page){
        int startIndex = (page - 1) * 10;
        if(startIndex > loadedScenes.size() - 1){
            printConsole("page beyond bounds: " + page);
            printConsole("number of pages: " + (loadedScenes.size() / 10 + ((loadedScenes.size() % 10 != 0) ? 1 : 0)));
            return;
        }
        
        printConsole("-- MAPS --");
        printConsole("Page " + page + " of " + (loadedScenes.size() / 10 + ((loadedScenes.size() % 10 != 0) ? 1 : 0)));
        printConsole("");
        for(int i = startIndex; (i < loadedScenes.size() && i < startIndex + 10); i++){
            Scene s = loadedScenes.get(i);
            String str = "";
            if(!s.loaded) str += " [UNLOADED]";
            if(s.equals(currentScene)) str += " [CURRENT]";
            printConsole("} " + s.name + str);
        }
        printConsole("");
    }

    public static void printCustomHelp(int page){
        int startIndex = (page - 1) * 10;
        if(startIndex > Update.commands.length - 1){
            printConsole("page beyond bounds: " + page);
            printConsole("number of pages: " + (Update.commands.length / 10 + ((Update.commands.length % 10 != 0) ? 1 : 0)));
            return;
        }
        
        printConsole("-- GAME-SPECIFIC COMMANDS --");
        printConsole("Page " + page + " of " + (Update.commands.length / 10 + ((Update.commands.length % 10 != 0) ? 1 : 0)));
        printConsole("");
        for(int i = startIndex; (i < Update.commands.length && i < startIndex + 10); i++){
            printConsole("} " + Update.commands[i]);
        }
        printConsole("");
    }

}
