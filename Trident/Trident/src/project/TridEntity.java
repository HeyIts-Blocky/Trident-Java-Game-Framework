package project;

import blib.game.*;
import blib.util.*;

import javax.swing.*;
import java.awt.*;
public class TridEntity extends Entity {

    public final boolean HASCOLLISION;
    public Dimension collision;
    public String engineRenderData = "";
    private ImageIcon engineImg = new ImageIcon("data/images/trident/customEnt.png");
    public String name;
    protected int numData;
    
    public TridEntity(Position pos){
        super(pos);
        HASCOLLISION = false;
        collision = null;
    }
    public TridEntity(Position pos, Dimension collision){
        super(pos);
        if(collision == null || collision.equals(new Dimension(0, 0))) HASCOLLISION = false;
        else HASCOLLISION = true;
        this.collision = collision;
    }
    public TridEntity(String n, boolean hasColl, int numData){
        super(new Position());
        HASCOLLISION = hasColl;
        collision = null;
        name = n;
        this.numData = numData;
    }
    public TridEntity construct(Position pos, Dimension collision, int[] data){
        System.out.println("Error: tried to create an empty entity");
        return null;
    }

    public final Rectangle getCollision(){
        return new Rectangle((int)position.x - (collision.width / 2), (int)position.y - (collision.height / 2), collision.width, collision.height);
    }

    public void gameStart(){
        
    }
    
    public void render(Graphics g, JPanel panel, int x, int y){

    }

    public void engineRender(Graphics g, JPanel panel, int x, int y){
        if(HASCOLLISION){
            g.setColor(Color.red);
            g.drawRect(x - getCollision().width / 2, y - getCollision().height / 2, getCollision().width, getCollision().height);
            g.drawLine(x - getCollision().width / 2, y - getCollision().height / 2, x + getCollision().width / 2, y + getCollision().height / 2);
        }
        engineImg.paintIcon(panel, g, x - engineImg.getIconWidth() / 2, y - engineImg.getIconHeight() / 2);
    }

    public void update(long elapsedTime){

    }

}
