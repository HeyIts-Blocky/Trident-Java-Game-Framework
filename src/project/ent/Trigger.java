package project.ent;

import blib.util.*;
import java.awt.*;
import javax.swing.*;

import project.*;
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

    public void engineRender(Graphics g, JPanel panel, int x, int y){
        g.setColor(color);
        g.drawRect(x - box.width / 2, y - box.height / 2, box.width, box.height);
        g.drawLine(x - box.width / 2, y - box.height / 2, x + box.width / 2, y + box.height / 2);
        engineImg.paintIcon(panel, g, x - engineImg.getIconWidth() / 2, y - engineImg.getIconHeight() / 2);
        
        String str = "" + id;
        g.setColor(Color.white);
        int width = g.getFontMetrics().stringWidth(str);
        g.fillRect(x - width / 2, y + 32 - 5, width, 10);
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        TextBox.draw(str, g, x, y + 32, TextBox.CENTER);
    }
}
