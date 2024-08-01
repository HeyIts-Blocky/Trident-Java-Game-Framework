package trident.ent;

import blib.util.*;
import java.awt.*;
import javax.swing.*;

import trident.*;
public class BoxColl extends TridEntity{

    public Color color = Color.white;

    private ImageIcon engineImg = new ImageIcon("data/images/trident/boxColl.png");
    
    public BoxColl(Position pos, Dimension size, Color c){
        super(pos, size);
        color = c;
    }
    public BoxColl(Position pos, Dimension size){
        super(pos, size);
    }
    public BoxColl(){
        super("boxcoll", true, 3);
    }
    public TridEntity construct(Position pos, Dimension collision, int[] data){
        return new BoxColl(pos, collision, new Color(data[0], data[1], data[2]));
    }

    public void render(Graphics g, JPanel panel, int x, int y){
        g.setColor(color);
        g.fillRect(x - getCollision().width / 2, y - getCollision().height / 2, getCollision().width, getCollision().height);
    }

    public void engineRender(Graphics g, JPanel panel, int x, int y){
        render(g, panel, x, y);
        engineImg.paintIcon(panel, g, x - engineImg.getIconWidth() / 2, y - engineImg.getIconHeight() / 2);
    }
}
