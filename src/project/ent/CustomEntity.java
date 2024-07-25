package project.ent;

import blib.util.*;
import project.*;
import java.awt.*;

import javax.swing.JPanel;
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

    public void engineRender(Graphics g, JPanel panel, int x, int y){
        super.engineRender(g, panel, x, y);String str = "[";
        for(int i = 0; i < data.length; i++){
            str += data[i] + "";
            if(i == data.length - 1){
                str += "]";
            }else{
                str += ", ";
            }
        }
        if(data.length == 0) str = "NO DATA";
        g.setColor(Color.white);
        int width = Math.max(g.getFontMetrics().stringWidth(str), g.getFontMetrics().stringWidth(name));
        g.fillRect(x - width / 2, y + 32 - 5, width, 20);
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        TextBox.draw(name + "\n" + str, g, x, y + 32, TextBox.CENTER);

    }
}
