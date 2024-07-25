package update;

import trident.*;
import ent.*;
import java.util.ArrayList;
public class Update {

    public static final String[] fonts = {}; // add fonts here

    public static void setup(){
        // Add custom entities to the registry here. Required in order to load them properly
        Trident.addCustomEntity(new ExampleEntity()); // Use the empty constructor

        // Set settings
        Trident.setPlrSpeed(0.2);
        Trident.setShortCollision(true);

        // Post Processing
        Trident.setBloom(0.2);
        Trident.setExposure(1);
        Trident.enableBloom = false;
        Trident.enableExposure = false;
        Trident.setLightBlur(1);
    }

    public static void sceneStart(String scene){

    }
    
    public static void update(long elapsedTime){

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
