package trident.ent;

import blib.util.*;
import java.awt.*;
import javax.swing.*;

import trident.*;
public class Trigger extends TridEntity{
    
    public Color color = Color.blue;
    public Dimension box;
    public int id;

    private ImageIcon engineImg = new ImageIcon("data/images/trident/trigger.png");
    
    public Trigger(Position pos, Dimension size, int i){
        super(pos);
        name = "trigger";
        box = size;
        id = i;
    }   
    public Trigger(){
        super("trigger", false, 3);
    }
    public TridEntity construct(Position pos, Dimension collision, int[] data){
        return new Trigger(pos, new Dimension(data[0], data[1]), data[2]);
    }

    public boolean containsPos(Position pos){
        Point p = new Point((int)pos.x, (int)pos.y);
        Rectangle rect = new Rectangle((int)position.x - box.width / 2, (int)position.y - box.height / 2, box.width, box.height);
        return rect.contains(p);
    }

    public void engineRender(Graphics g, JPanel panel, int x, int y){
        g.setColor(color);
        g.drawRect(x - box.width / 2, y - box.height / 2, box.width, box.height);
        g.drawLine(x - box.width / 2, y - box.height / 2, x + box.width / 2, y + box.height / 2);
        engineImg.paintIcon(panel, g, x - engineImg.getIconWidth() / 2, y - engineImg.getIconHeight() / 2);
    }
}
