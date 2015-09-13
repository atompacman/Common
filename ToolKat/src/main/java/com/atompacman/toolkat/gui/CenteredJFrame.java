package com.atompacman.toolkat.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

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

    
    //------------------------------------ CENTER WINDOW -----------------------------------------\\

    protected void centerFrame() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth()  - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setLocation(x, y);
    }


    //--------------------------------------- LABELS ---------------------------------------------\\

    public static JLabel createLabel(String text, int size) {
        return createLabel(text, Color.BLACK, size, SwingConstants.LEFT);
    }

    public static JLabel createLabel(String text, int size, int horizAlignement) {
        return createLabel(text, Color.BLACK, size, horizAlignement);
    }

    public static JLabel createLabel(String text, Color color, int size, int horizAlignement) {
        JLabel label = new JLabel(text, horizAlignement);
        label.setForeground(color);
        label.setFont(label.getFont().deriveFont((float) size));
        return label;
    }

    public static JPanel createLabelPanel(String text, int size, int horizAlignement) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(createLabel(text, Color.BLACK, size, horizAlignement));
        return panel;
    }


    //------------------------------------- Text area --------------------------------------------\\

    public static JTextArea createTextArea(String text, int size) {
        return createTextArea(text, Color.BLACK, size);
    }

    public static JTextArea createTextArea(String text, Color color, int size) {
        JTextArea textArea = new JTextArea(text);
        textArea.setWrapStyleWord(true);
        textArea.setForeground(color);
        textArea.setFont(textArea.getFont().deriveFont((float) size));
        return textArea;
    }
}
