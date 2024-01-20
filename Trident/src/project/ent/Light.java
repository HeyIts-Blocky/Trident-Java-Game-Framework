package project.ent;

import blib.util.*;
import java.awt.*;
import javax.swing.*;

import project.*;
public class Light extends TridEntity{
    
    public int radius;

    private ImageIcon engineImg = new ImageIcon("data/images/trident/light.png");
    
    public Light(Position pos, int r){
        super(pos);
        name = "light";
        radius = r;
    }   
    public Light(){
        super("light", false, 1);
    }
    public TridEntity construct(Position pos, Dimension collision, int[] data){
        return new Light(pos, data[0]);
    }

    public void engineRender(Graphics g, JPanel panel, int x, int y){
        engineImg.paintIcon(panel, g, x - engineImg.getIconWidth() / 2, y - engineImg.getIconHeight() / 2);
        g.setColor(Color.red);
        g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
    }
}
