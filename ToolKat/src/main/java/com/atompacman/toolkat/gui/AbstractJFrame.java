package com.atompacman.toolkat.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public abstract class AbstractJFrame {

	//------------ HELPER METHODS ------------\\

	protected static void centerFrame(JFrame frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
	    frame.setLocation(x, y);
	}
}
