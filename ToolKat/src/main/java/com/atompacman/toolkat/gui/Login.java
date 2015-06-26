package com.atompacman.toolkat.gui;

public class Login {

    //======================================= FIELDS =============================================\\

    private final String username;
    private final String password;



    //======================================= METHODS ============================================\\

    //---------------------------------- PACKAGE CONSTRUCTOR -------------------------------------\\

    Login(String username, String password) {
        this.username = username;
        this.password = password;
    }


    //--------------------------------------- GETTERS --------------------------------------------\\

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }
}
