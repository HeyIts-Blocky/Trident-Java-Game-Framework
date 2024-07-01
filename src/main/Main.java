package main;

import javax.swing.*;
import project.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
public class Main {
    public static Image icon = new ImageIcon("data/icon.png").getImage();
    public static void main(String[] args){

        Project.loadRegistry();

        JFrame selWindow = new JFrame("Trident");
        selWindow.setSize(400, 200);
        selWindow.setResizable(false);
        selWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        selWindow.setIconImage(icon);
        selWindow.setLayout(new GridLayout(2, 3));
        selWindow.setLocation(400, 300);

        JButton loadButton = new JButton("Load Project");
        loadButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                selWindow.dispose();
                selection(0);
            }
        });
        loadButton.setBackground(new Color(0, 0, 50));
        loadButton.setForeground(Color.white);
        selWindow.add(loadButton);
        JButton newButton = new JButton("New Project");
        newButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                selWindow.dispose();
                selection(1);
            }
        });
        newButton.setBackground(new Color(0, 0, 50));
        newButton.setForeground(Color.white);
        selWindow.add(newButton);
        JButton importButton = new JButton("Import Project");
        importButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                selWindow.dispose();
                selection(2);
            }
        });
        importButton.setBackground(new Color(0, 0, 50));
        importButton.setForeground(Color.white);
        selWindow.add(importButton);

        JTextArea loadText = new JTextArea("Load a project");
        loadText.setEditable(false);
        loadText.setBackground(new Color(0, 0, 100));
        loadText.setForeground(Color.white);
        selWindow.add(loadText);
        JTextArea newText = new JTextArea("Create a new project");
        newText.setEditable(false);
        newText.setBackground(new Color(0, 0, 100));
        newText.setForeground(Color.white);
        selWindow.add(newText);
        JTextArea importText = new JTextArea("Get a project put\ninto the registry");
        importText.setEditable(false);
        importText.setBackground(new Color(0, 0, 100));
        importText.setForeground(Color.white);
        selWindow.add(importText);

        selWindow.setVisible(true);

    }

    public static int loadSel = 0;

    public static void selection(int sel){
        if(sel == 0){ // Load project

            if(Project.projRegistry.length == 0){
                JOptionPane.showMessageDialog(null, "You don't have any projects!", "Trident", JOptionPane.ERROR_MESSAGE);
                main(new String[0]);
                return;
            }

            JFrame selWindow = new JFrame("Trident");
            selWindow.setSize(500, 200);
            selWindow.setResizable(false);
            selWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            selWindow.setIconImage(icon);
            selWindow.setLayout(new GridLayout(2, 3));
            selWindow.setLocation(400, 300);

            JTextField text = new JTextField(Project.projRegistry[0] + "\n(1/" + Project.projRegistry.length + ")");
            text.setEditable(false);
            text.setBackground(new Color(0, 0, 100));
            text.setForeground(Color.white);
            JButton prevButton = new JButton("<");
            prevButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    loadSel--;
                    if(loadSel < 0){
                        loadSel = Project.projRegistry.length - 1;
                    }
                    text.setText(Project.projRegistry[loadSel] + "\n(" + (loadSel + 1) + "/" + Project.projRegistry.length + ")");
                }
            });
            prevButton.setBackground(new Color(0, 0, 50));
            prevButton.setForeground(Color.white);
            selWindow.add(prevButton);
            selWindow.add(text);
            JButton nextButton = new JButton(">");
            nextButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    loadSel++;
                    if(loadSel > Project.projRegistry.length - 1){
                        loadSel = 0;
                    }
                    text.setText(Project.projRegistry[loadSel] + "\n(" + (loadSel + 1) + "/" + Project.projRegistry.length + ")");
                }
            });
            nextButton.setBackground(new Color(0, 0, 50));
            nextButton.setForeground(Color.white);
            selWindow.add(nextButton);
            JButton loadButton = new JButton("Load");
            loadButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    selWindow.dispose();
                    loadProject(loadSel);
                }
            });
            loadButton.setBackground(new Color(0, 0, 50));
            loadButton.setForeground(Color.white);
            selWindow.add(loadButton);
            JButton deleteButton = new JButton("Delete Project");
            deleteButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){

                    int sel = JOptionPane.showConfirmDialog(selWindow, "Are you sure you want to delete this project?", "Trident", JOptionPane.YES_NO_OPTION);

                    if(sel == 0){
                        deleteDir(new File("data/projects/" + Project.projRegistry[loadSel]));
                        
                        String[] newRegistry = new String[Project.projRegistry.length - 1];
                        for(int i = 0, j = 0; i < Project.projRegistry.length; i++){
                            if(i != loadSel){
                                newRegistry[j] = Project.projRegistry[i];
                                j++;
                            }
                        }
                        Project.projRegistry = newRegistry;
                        loadSel = 0;
                        Project.saveRegistry();

                        JOptionPane.showMessageDialog(selWindow, "Deleted the project.", "Trident", JOptionPane.INFORMATION_MESSAGE);

                        if(Project.projRegistry.length == 0){
                            selWindow.dispose();
                            main(new String[0]);
                            return;
                        }
                        text.setText(Project.projRegistry[loadSel] + "\n(" + (loadSel + 1) + "/" + Project.projRegistry.length + ")");
                    }

                    
                }
            });
            deleteButton.setBackground(new Color(0, 0, 50));
            deleteButton.setForeground(Color.white);
            selWindow.add(deleteButton);
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    selWindow.dispose();
                    main(new String[0]);
                }
            });
            cancelButton.setBackground(new Color(0, 0, 50));
            cancelButton.setForeground(Color.white);
            selWindow.add(cancelButton);
            

            selWindow.setVisible(true);
        }
        if(sel == 1){ // New project
            String[] options = {"Cancel", "Blank 2D", "Blank 3D", "Custom"};
            int selection = JOptionPane.showOptionDialog(null, "Select the type of project:", "Trident", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if(selection == 0){
                main(new String[0]);
                return;
            }
            String customTemp = "";
            if(selection == options.length - 1){
                customTemp = JOptionPane.showInputDialog(null, "Enter the custom template name.", "Trident", JOptionPane.QUESTION_MESSAGE);
                if(customTemp == null){
                    main(new String[0]);
                    return;
                }
            }
            String name = JOptionPane.showInputDialog(null, "Enter the project name. If a project already exists with that name, keep in mind that it will replace it.", "Trident", JOptionPane.QUESTION_MESSAGE);
            if(name == null){
                main(new String[0]);
                return;
            }
            String[] newRegistry = new String[Project.projRegistry.length + 1];
            for(int i = 0; i < Project.projRegistry.length; i++){
                newRegistry[i] = Project.projRegistry[i];
            }
            newRegistry[newRegistry.length - 1] = name;
            Project.projRegistry = newRegistry;
            Project.saveRegistry();
            new Project(name, selection, customTemp);
            JOptionPane.showMessageDialog(null, "Project successfully created.", "Trident", JOptionPane.INFORMATION_MESSAGE);
            main(new String[0]);
        }
        if(sel == 2){ // Import project
            String name = JOptionPane.showInputDialog(null, "Enter the project name. Make sure it's in the projects folder before you open it.", "Trident", JOptionPane.QUESTION_MESSAGE);
            if(name == null){
                main(new String[0]);
                return;
            }
            String[] newRegistry = new String[Project.projRegistry.length + 1];
            for(int i = 0; i < Project.projRegistry.length; i++){
                newRegistry[i] = Project.projRegistry[i];
            }
            newRegistry[newRegistry.length - 1] = name;
            Project.projRegistry = newRegistry;
            Project.saveRegistry();
            main(new String[0]);
        }
    }

    public static void deleteDir(File file) {
        if(!file.exists()) return;

        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (! Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        file.delete();
    }

    public static JFrame window;
    
    public static void loadProject(int proj){
        window = new JFrame("Trident Editor");
        window.setSize(700, 500);
        window.setMinimumSize(new Dimension(700, 500));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setIconImage(icon);
        // panel
        MainPanel panel = new MainPanel(new Project(proj));
        window.add(panel);
        //
        window.setVisible(true);
    }
}
