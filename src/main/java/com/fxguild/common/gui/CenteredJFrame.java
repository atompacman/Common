package com.fxguild.common.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

/**
 * Wraps the {@link JFrame} class with a window centering mechanism.
 */
public class CenteredJFrame extends JFrame {

    //
    //  ~  CONSTANTS  ~  //
    //

    private static final long serialVersionUID = 8302624826512646560L;


    //
    //  ~  INIT  ~  //
    //

    public CenteredJFrame(int width, int height) {
        this(new Dimension(width, height));
    }

    public CenteredJFrame(Dimension winDim) {
        setSize(winDim);
        center();
    }


    //
    //  ~  UTILS  ~  //
    //

    protected void center() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int)((dimension.getWidth()  - getWidth ()) / 2);
        int y = (int)((dimension.getHeight() - getHeight()) / 2);
        setLocation(x, y);
    }
}
