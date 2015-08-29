package com.atompacman.toolkat.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AnomalyDescription {

    //===================================== INNER TYPES ==========================================\\

    public enum Severity {
        NONE, MINIMAL, MODERATE, CRITIC, FATAL;
    }



    //======================================= FIELDS =============================================\\

    String   name();
    String   detailsFormat()    default "%s";
    String   description()      default "UNSPECIFIED";
    String   consequences()     default "UNSPECIFIED";
    Severity severity()         default Severity.NONE;
}
