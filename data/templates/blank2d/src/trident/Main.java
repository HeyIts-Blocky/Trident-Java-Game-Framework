package trident;

import javax.swing.*;
import java.awt.*;
public class Main{
    protected static JFrame window = new JFrame("blank2d");
    public static void main(String[] args){
        window.setSize(700, 500);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setMinimumSize(new Dimension(700, 500));
        window.setIconImage(new ImageIcon("data/icon.png").getImage());
        // panel
        MainPanel panel = new MainPanel();
        window.add(panel);
        //
        window.setVisible(true);
    }
}
