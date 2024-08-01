package trident.ent;

import blib.util.*;
import java.awt.*;
import javax.swing.*;

import trident.*;
public class InvisColl extends TridEntity{
    
    public Color color = Color.red;

    private ImageIcon engineImg = new ImageIcon("data/images/trident/collision.png");
    
    public InvisColl(Position pos, Dimension size){
        super(pos, size);
    }   
    public InvisColl(){
        super("inviscoll", true, 0);
    }
    public TridEntity construct(Position pos, Dimension collision, int[] data){
        return new InvisColl(pos, collision);
    }

    public void engineRender(Graphics g, JPanel panel, int x, int y){
        g.setColor(color);
        g.drawRect(x - getCollision().width / 2, y - getCollision().height / 2, getCollision().width, getCollision().height);
        g.drawLine(x - getCollision().width / 2, y - getCollision().height / 2, x + getCollision().width / 2, y + getCollision().height / 2);
        engineImg.paintIcon(panel, g, x - engineImg.getIconWidth() / 2, y - engineImg.getIconHeight() / 2);
    }
}
