package trident.ent;

import blib.util.*;
import java.awt.*;
import javax.swing.*;

import trident.*;
public class PlrStart extends TridEntity{
    public Color color = Color.red;

    private ImageIcon engineImg = new ImageIcon("data/images/trident/plrStart.png");
    
    public PlrStart(Position pos){
        super(pos);
    }   
    public PlrStart(){
        super("plrstart", false, 0);
    }
    public TridEntity construct(Position pos, Dimension collision, int[] data){
        return new PlrStart(pos);
    }

    public void engineRender(Graphics g, JPanel panel, int x, int y){
        engineImg.paintIcon(panel, g, x - engineImg.getIconWidth() / 2, y - engineImg.getIconHeight() / 2);
    }
}
