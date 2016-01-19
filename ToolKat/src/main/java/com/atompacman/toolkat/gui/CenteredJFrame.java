package com.atompacman.toolkat.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class CenteredJFrame extends JFrame {

    //======================================= METHODS ============================================\\

    //--------------------------------- PUBLIC CONSTRUCTORS --------------------------------------\\

    public CenteredJFrame(int width, int height) {
        this(new Dimension(width, height));
    }

    public CenteredJFrame(Dimension winDim) {
        setSize(winDim);
        centerFrame();
    }

    
    //------------------------------------- CENTER FRAME -----------------------------------------\\

    protected void centerFrame() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth()  - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setLocation(x, y);
    }
}
