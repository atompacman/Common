package com.fxguild.common.gui;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.fxguild.common.Log;

/**
 * Various swing GUI utilities.
 */
public final class GUIUtils {

    //
    //  ~  INIT  ~  //
    //

    private GUIUtils() {
        
    }
    
    
    //
    //  ~  DISPLAY IMAGE  ~  //
    //

    public static void displayImageInWindow(BufferedImage image) {
        JFrame frame = new CenteredJFrame(image.getWidth(), image.getHeight());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setVisible(true);
    }

    
    //
    //  ~  LABELS  ~  //
    //

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


    //
    //  ~  TEXT AREAS  ~  //
    //

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


    //
    //  ~  MISC  ~  //
    //

    public static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Log.error("Couldn't set system look and feel");
        }
    }
}
