package com.fxguild.common.gui;

import java.awt.GridLayout;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Utility class that allows prompting the user for {@link Login} credentials via a pop-up window.
 */
public final class LoginDialog {

    //
    //  ~  INIT  ~  //
    //

    private LoginDialog() {
        
    }
    
    
    //
    //  ~  ASK FOR LOGIN  ~  //
    //

    public static Optional<Login> askForLogin() {
        return askForLogin("Login", null);
    }

    public static Optional<Login> askForLogin(String windowName) {
        return askForLogin(windowName, null);
    }

    public static Optional<Login> askForLogin(String windowName, @Nullable String imageIconPath) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2,2));
        panel.add(new JLabel("Username:"));
        
        JTextField usernameField = new JTextField();
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        
        JPasswordField passwordField = new JPasswordField();
        panel.add(passwordField);

        int selected = JOptionPane.CANCEL_OPTION;

        if (imageIconPath == null) {
            selected = JOptionPane.showConfirmDialog(null, panel, windowName, 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        } else {
            Icon icon = new ImageIcon(imageIconPath);
            selected = JOptionPane.showConfirmDialog(null, panel, windowName, 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
        }

        if (selected == JOptionPane.CANCEL_OPTION) {
            return Optional.empty();
        }

        Login login = Login.of(usernameField.getText(), new String(passwordField.getPassword()));
        return Optional.of(login);
    }
}
