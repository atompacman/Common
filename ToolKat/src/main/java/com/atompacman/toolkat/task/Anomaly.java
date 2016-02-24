package com.atompacman.toolkat.task;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.atompacman.toolkat.EnumUtils;

public final class Anomaly extends Observation {

    //
    //  ~  INNER TYPES  ~  //
    //

    public enum Severity {
        UNSPECIFIED, MINIMAL, MODERATE, CRITIC, FATAL;
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Description {
        String   name();
        String   detailsFormat()    default "%s";
        String   description()      default "UNSPECIFIED";
        String   consequences()     default "UNSPECIFIED";
        Severity severity()         default Severity.UNSPECIFIED;
    }

    
    //
    //  ~  FIELDS  ~  //
    //

    private final Description desc;

    
    //
    //  ~  INIT  ~  //
    //

    static Anomaly of(Enum<?> anomaly, int indentLvl, int stackTraceMod, Object...detailsArgs) {
        // Extract description annotation
        Description desc = EnumUtils.extractAnnotation(anomaly, Description.class);
        
        // Select appropriate verbose level
        Level verbLvl = null;
        switch (desc.severity()) {
        case FATAL:       verbLvl = Level.FATAL; break;
        case CRITIC:      verbLvl = Level.ERROR; break;
        case MODERATE:    verbLvl = Level.WARN;  break;
        case UNSPECIFIED: verbLvl = Level.WARN;  break;
        case MINIMAL:     verbLvl = Level.INFO;  break;
        }
        
        // Create message
        StringBuilder sb = new StringBuilder();
        sb.append(":::: ANOMALY :::: ").append(desc.name()).append(" :::: ");
        sb.append(StringUtils.capitalize(desc.consequences())).append('.');
        sb.append(String.format(desc.detailsFormat(), detailsArgs));
        
        // Create anomaly
        return new Anomaly(desc, sb.toString(), indentLvl, verbLvl, stackTraceMod + 1);
    }
    
    private Anomaly(Description desc, 
                    String      msg, 
                    int         indentLvl, 
                    Level       verbLvl, 
                    int         stackTraceMod) {
        
        super(msg, indentLvl, verbLvl, true, stackTraceMod);
        this.desc = desc;
    }


    //
    //  ~  GETTERS  ~  //
    //
    
    public Description getDescription() {
        return desc;
    }
}
