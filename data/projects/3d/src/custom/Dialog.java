package custom;

import java.awt.Font;

import javax.swing.*;
import blib.util.*;
public class Dialog {

    public String text, name;
    long scrollTime = 25;
    Font customFont = new Font("Porky's", Font.PLAIN, 25);
    
    public Dialog(String txt, String name){
        text = txt;
        this.name = name;
    }
    public Dialog(String txt, String name, long t){
        text = txt;
        this.name = name;
        scrollTime = t;
    }
    public Dialog(String txt, String name, Font f){
        text = txt;
        this.name = name;
        customFont = f;
    }
    public Dialog(String txt, String name, long t, Font f){
        text = txt;
        this.name = name;
        scrollTime = t;
        customFont = f;
    }

    public String getText(){
        return text;
    }
}
