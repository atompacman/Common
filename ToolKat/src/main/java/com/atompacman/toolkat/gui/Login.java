package com.atompacman.toolkat.gui;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Login {

    //
    //  ~  FIELDS  ~  //
    //

    public abstract String getUsername();
    public abstract String getPassword();


    //
    //  ~  INIT  ~  //
    //

    public static Login of(String username, String password) {
        return new AutoValue_Login(username, password);
    }
}
