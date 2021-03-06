package com.fxguild.common.task;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.logging.log4j.Level;

/**
 * Used to tag <b> enum fields </b> that represents a kind of anomaly that can be triggered at one
 * or multiple places in the code.
 */
@Retention (RetentionPolicy.RUNTIME)
@Target    (ElementType.FIELD)
public @interface AnomalyDescription {

    //
    //  ~  INNER TYPES  ~  //
    //

    public enum Severity {
        
        UNSPECIFIED (Level.WARN), 
        MINIMAL     (Level.INFO), 
        MODERATE    (Level.WARN), 
        CRITIC      (Level.ERROR), 
        FATAL       (Level.FATAL);
        
        
        //
        //  ~  FIELDS  ~  //
        //
        
        private final Level verbLvl;
        
        
        //
        //  ~  INIT  ~  //
        //
        
        private Severity(Level verbLvl) {
            this.verbLvl = verbLvl;
        }
        
        
        //
        //  ~  GETTERS  ~  //
        //
        
        public Level getVerboseLevel() {
            return verbLvl;
        }
    }
    
    
    //
    //  ~  FIELDS  ~  //
    //
    
    String   name();
    String   detailsFormat() default "%s";
    String   description()   default "UNSPECIFIED";
    String   consequences()  default "UNSPECIFIED";
    Severity severity()      default Severity.UNSPECIFIED;
}