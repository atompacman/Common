package com.atompacman.toolkat.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public abstract class AbstractJFrame extends JFrame {

    //======================================= METHODS ============================================\\

    //--------------------------------------- HELPERS --------------------------------------------\\

    protected void centerFrame() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth()  - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setLocation(x, y);
    }
}
