package com.atompacman.toolkat.gui;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GUIUtils {
    
    //----------------------------------- DISPLAY IMAGE ------------------------------------------\\

    public static void displayImageInWindow(BufferedImage image) {
        JFrame frame = new CenteredJFrame(image.getWidth(), image.getHeight());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setVisible(true);
    }
}
