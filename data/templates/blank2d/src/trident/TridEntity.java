package trident;

import blib.game.*;
import blib.util.*;

import javax.swing.*;
import java.awt.*;
public class TridEntity extends Entity {

    public final boolean HASCOLLISION;
    private final Dimension collision;
    public String engineRenderData = "";
    private ImageIcon engineImg = new ImageIcon("data/images/trident/customEnt.png");
    protected String name;
    protected int numData;
    
    public TridEntity(Position pos){
        super(pos);
        HASCOLLISION = false;
        collision = null;
    }
    public TridEntity(Position pos, Dimension collision){
        super(pos);
        if(collision.equals(new Dimension(0, 0))) HASCOLLISION = false;
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

    public Rectangle getCollision(){
        return new Rectangle((int)position.x - (collision.width / 2), (int)position.y - (collision.height / 2), collision.width, collision.height);
    }
    
    public void render(Graphics g, JPanel panel, int x, int y){

    }

    protected void engineRender(Graphics g, JPanel panel, int x, int y){
        if(HASCOLLISION){
            g.setColor(Color.red);
            g.drawRect(x - getCollision().width / 2, y - getCollision().height / 2, getCollision().width, getCollision().height);
            g.drawLine(x - getCollision().width / 2, y - getCollision().height / 2, x + getCollision().width / 2, y + getCollision().height / 2);
        }
        engineImg.paintIcon(panel, g, x - engineImg.getIconWidth() / 2, y - engineImg.getIconHeight() / 2);
    }

    public void update(long elapsedTime){

    }

    public void sceneStart(String scene){
        
    }

    public int getRendDatSort(){
        if(renderType == Entity.TALL){
            return 2;
        }
        if(renderType == Entity.UNDER){
            return 1;
        }
        if(renderType == Entity.ABOVE){
            return 3;
        }
        if(renderType == Entity.BOTTOMPRIORITY){
            return 0;
        }
        if(renderType == Entity.TOPPRIORITY){
            return 4;
        }
        return 2;
    }

    public int compareSort(TridEntity e){
        if(e.getRendDatSort() > getRendDatSort()){
            // e should be above
            return 1;
        }else if(e.getRendDatSort() < getRendDatSort()){
            return -1;
        }

        if(e.position.y > position.y){
            return 1;
        }else if(e.position.y < position.y){
            return -1;
        }

        return 0;
    }

}
