package custom;

import java.awt.Font;
import java.util.ArrayList;
import blib.util.*;
import javax.swing.*;
public class DialogString {

    public static DialogString[] dialogStrings = {
        new DialogString(new Dialog[]{ // 0
            new Dialog("Hello World!", "Test"),
            new Dialog("I am a wall", "Wall"),
            new Dialog("HELP I'M TRAPPED IN THIS WALL I CAN'T BREATHE AAAAAAAAAAAAAAAAAAAAAAAAA", "Oland"),
        }),
        new DialogString(new Dialog[]{ // 1
            new Dialog("Yoooo no way its me I'm in the game", "Blocky"),
            new Dialog("I feel a little s t r e t c h e d though", "Blocky"),
            new Dialog("idk it's prolly fine", "Blocky"),
            new Dialog("also why is transparency so hard :<", "Blocky"),
        }),
        new DialogString(new Dialog[]{ // 2
            new Dialog("Hello I'm OLand", "OLand"),
            new Dialog("I took forever to get to render properly because Blocky kept running into infinite loops", "OLand"),
        }),
        new DialogString(new Dialog[]{ // 3
            new Dialog("Hai hai cafegoer!", "Blocky"),
            new Dialog("Yo what's good", "Oland"),
            new Dialog("Welcome to chat-1", "Blocky"),
            new Dialog("Don't let the crowd scare you off, I promise it's chill once you get used to it!", "Blocky"),
        }),
    };

    public ArrayList<Dialog> dialogs = new ArrayList<Dialog>();
    private int currentDialog = 0;
    private int dialogPos = 0;
    private long dialogTime = 0;

    public DialogString(Dialog[] dia){
        for(Dialog d: dia){
            dialogs.add(d);
        }
    }
    public static DialogString get(int i){
        return dialogStrings[i];
    }

    public void addDialog(Dialog d){
        dialogs.add(d);
    }

    public void update(long elapsedTime){
        if(currentDialog >= dialogs.size()) return;
        if(dialogPos >= dialogs.get(currentDialog).getText().length()){
            dialogPos = dialogs.get(currentDialog).getText().length();
            return;
        }
        dialogTime += elapsedTime;
        while(dialogTime >= dialogs.get(currentDialog).scrollTime){
            dialogTime -= dialogs.get(currentDialog).scrollTime;
            dialogPos++;
            if(dialogPos >= dialogs.get(currentDialog).getText().length()){
                dialogPos = dialogs.get(currentDialog).getText().length();
                return;
            }
        }
    }

    public void next(){
        if(dialogPos < dialogs.get(currentDialog).getText().length()){
            dialogPos = dialogs.get(currentDialog).getText().length();
            return;
        }
        dialogPos = 0;
        dialogTime = 0;
        currentDialog++;
    }

    public String getText(){
        if(currentDialog >= dialogs.size()) return "";
        String text = dialogs.get(currentDialog).getText();
        String scrolledText = "";
        for(int i = 0; i < dialogPos; i++){
            scrolledText += text.charAt(i);
        }
        return scrolledText;
    }

    public boolean scrollDone(){
        return dialogPos == dialogs.get(currentDialog).getText().length();
    }

    public String getName(){
        return dialogs.get(currentDialog).name;
    }

    public Font getFont(){
        if(currentDialog >= dialogs.size()) return null;
        return dialogs.get(currentDialog).customFont;
    }

    public boolean moreText(){
        if(currentDialog >= dialogs.size()) return false;
        return true;
    }

    public void reset(){
        dialogPos = 0;
        dialogTime = 0;
        currentDialog = 0;
    }
}
