package com.atompacman.toolkat.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Anomaly {

    //===================================== INNER TYPES ==========================================\\

    public enum Impact {
        NONE, MINIMAL, MODERATE, CRITIC, FATAL;
    }

    public enum Recoverability {
        UNKNOWN, TRIVIAL, NORMAL, HARD, IMPOSSIBLE;
    }


    
    //======================================= FIELDS =============================================\\

    String                  name();
    String                  description();
    String                  consequences();
    Impact                  impact();
    Recoverability          recoverability();
}
