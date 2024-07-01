package project;

import java.io.*;
import blib.util.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import blib.bson.*;
public class Project {

    public static String[] projRegistry = null;

    public static final int BLANK2D = 1, BLANK3D = 2, CUSTOM = 3;
    
    public String name;
    public ArrayList<Scene> loadedScenes = new ArrayList<Scene>();
    public Scene currentScene = new Scene("Empty Scene");

    public Project(int proj){ // Load from file
        name = projRegistry[proj];
        setupScenes();
        currentScene = loadedScenes.get(0);
    }
    public Project(String name, int t, String customTemp){ // New project

        this.name = name;

        String template = "";
        switch(t){
        case BLANK2D:
            template = "blank2d";
            break;
        case BLANK3D:
            template = "blank3d";
            break;
        case CUSTOM:
            template = customTemp;
            File f = new File("data/templates/" + template);
            if(!f.exists()){
                JOptionPane.showMessageDialog(null, "Template '" + customTemp + "' not found. Falling back to blank2d.", "Trident", JOptionPane.ERROR_MESSAGE);
                template = "blank2d";
            }
            break;
        }
        

        try{
            // libraries
            copyDirectory(new File("data/libraries/"), new File("data/projects/" + name + "/lib/"));

            // src
            copyDirectory(new File("data/templates/" + template + "/src/"), new File("data/projects/" + name + "/src/"));
            
            // data
            copyDirectory(new File("data/templates/" + template + "/data/"), new File("data/projects/" + name + "/data/"));
        }catch(Exception e){
            System.out.println("ruh roh raggy");
            e.printStackTrace();
            System.exit(1);
        }
        
        
        
        setupScenes();
        currentScene = loadedScenes.get(0);
    }

    public static void loadRegistry(){
        ArrayList<BSonObject> objects = BSonParser.readFile("data/projectRegistry.bson");
        BSonObject obj = BSonParser.getObject("projects", objects);
        BSonList asList = (BSonList)obj;
        projRegistry = new String[asList.list.size()];
        for(int i = 0; i < asList.list.size(); i++){
            projRegistry[i] = asList.list.get(i).getString();
        }
    }

    public static void saveRegistry(){
        try{
            File file = new File("data/projectRegistry.bson");
            file.createNewFile();
            PrintWriter writer = new PrintWriter(file);
            writer.println("{ projects");
            for(String s: projRegistry){
                writer.println("string " + s);
            }
            writer.println("}");
            writer.close();
        }catch(Exception e){e.printStackTrace();}
    }

    public void setupScenes(){
        try{
            loadedScenes = new ArrayList<Scene>();
            File dir = new File("data/projects/" + name + "/data/scenes");
            File[] files = dir.listFiles();
            ArrayList<File> sceneFiles = new ArrayList<File>();
            for(File f: files){
                if(BTools.hasExtension(f, "bson")) sceneFiles.add(f);
            }

            for(File f: sceneFiles){
                loadedScenes.add(new Scene(f));
            }
        }catch(Exception e){
            System.out.println("Error setting up scenes.");
            e.printStackTrace();
        }
        
    }

    public boolean loadScene(String name){
        for(Scene s: loadedScenes){
            if(s.name.equals(name)){
                currentScene = s;
                return true;
            }
        }
        System.out.println("***********************************************************************************");
        System.out.println("Error loading scene: No scene with name '" + name + "' found.");
        System.out.println("***********************************************************************************");
        return false;
    }





    // you made this? i made this.
    private static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdirs();
        }
        for (String f : sourceDirectory.list()) {
            copyDirectoryCompatibityMode(new File(sourceDirectory, f), new File(destinationDirectory, f));
        }
    }
    public static void copyDirectoryCompatibityMode(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            copyDirectory(source, destination);
        } else {
            copyFile(source, destination);
        }
    }
    private static void copyFile(File sourceFile, File destinationFile) throws IOException {
        try (InputStream in = new FileInputStream(sourceFile); 
        OutputStream out = new FileOutputStream(destinationFile)) {
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        }
    }

}
