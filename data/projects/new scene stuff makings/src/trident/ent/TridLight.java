package trident.ent;

import blib.util.*;
import java.awt.*;
import javax.swing.*;

import trident.*;
public class TridLight extends TridEntity{
    
    public int radius;

    private ImageIcon engineImg = new ImageIcon("data/images/trident/light.png");
    
    public TridLight(Position pos, int r){
        super(pos);
        radius = r;
    }   
    public TridLight(){
        super("light", false, 1);
    }
    public TridEntity construct(Position pos, Dimension collision, int[] data){
        return new TridLight(pos, data[0]);
    }

    public void engineRender(Graphics g, JPanel panel, int x, int y){
        engineImg.paintIcon(panel, g, x - engineImg.getIconWidth() / 2, y - engineImg.getIconHeight() / 2);
    }
}
