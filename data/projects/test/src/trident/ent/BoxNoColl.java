package trident.ent;

import blib.util.*;
import java.awt.*;
import javax.swing.*;
import blib.game.*;

import trident.*;
public class BoxNoColl extends TridEntity{
    
    public Color color = Color.white;
    public int width, height;

    private ImageIcon engineImg = new ImageIcon("data/images/trident/box.png");
    
    public BoxNoColl(Position pos, Color c, int w, int h){
        super(pos);
        color = c;
        width = w;
        height = h;
        renderType = Entity.UNDER;
    }
    public BoxNoColl(Position pos, int w, int h){
        super(pos);
        width = w;
        height = h;
        renderType = Entity.UNDER;
    }
    public BoxNoColl(){
        super("boxnocoll", false, 5);
    }
    public TridEntity construct(Position pos, Dimension collision, int[] data){
        return new BoxNoColl(pos, new Color(data[2], data[3], data[4]), data[0], data[1]);
    }

    public void render(Graphics g, JPanel panel, int x, int y){
        g.setColor(color);
        g.fillRect(x - width / 2, y - height / 2, width, height);
    }

    public void engineRender(Graphics g, JPanel panel, int x, int y){
        render(g, panel, x, y);
        engineImg.paintIcon(panel, g, x - engineImg.getIconWidth() / 2, y - engineImg.getIconHeight() / 2);
    }
}
