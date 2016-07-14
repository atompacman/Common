package com.fxguild.common.gui;

import com.fxguild.common.gui.AutoValue_Login;
import com.google.auto.value.AutoValue;

/**
 * Immutable class that represents basic login information (user name and password) in a trivial
 * non-encrypted form.
 */
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
