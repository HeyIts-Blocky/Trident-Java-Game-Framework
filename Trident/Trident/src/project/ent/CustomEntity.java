package project.ent;

import blib.util.*;
import project.*;
import java.awt.*;
public class CustomEntity extends TridEntity{

    public int[] data;
    
    public CustomEntity(Position pos, Dimension d, int[] dat, String n){
        super(pos, d);
        data = dat;
        name = n;
    }
    public CustomEntity(){
        super("custom", false, 0);
    }
}
