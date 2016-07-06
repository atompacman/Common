package com.atompacman.toolkat.task;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.atompacman.toolkat.EnumUtils;

public final class Anomaly extends Observation {

    //
    //  ~  FIELDS  ~  //
    //

    private final AnomalyDescription desc;

    
    //
    //  ~  INIT  ~  //
    //

    static Anomaly of(Enum<?> anomaly, String taskName, int stackTraceMod, Object...detailsArgs) {
        // Extract description annotation
        AnomalyDescription desc = EnumUtils.extractAnnotation(anomaly, AnomalyDescription.class);

        // Create message
        String msg = new StringBuilder()
                            .append(desc.name())
                            .append(" (")
                            .append(StringUtils.capitalize(desc.consequences()))
                            .append(") ")
                            .append(String.format(desc.detailsFormat(), detailsArgs)).toString();
        
        // Create anomaly
        Level verbose = desc.severity().getVerboseLevel();
        return new Anomaly(desc, taskName, msg, verbose, stackTraceMod + 1);
    }
    
    private Anomaly(AnomalyDescription desc,
                    String             taskName,
                    String             msg,
                    Level              verbLvl, 
                    int                stackTraceMod) {
        
        super(taskName, msg, verbLvl, true, stackTraceMod + 1);
        
        this.desc = desc;
    }


    //
    //  ~  GETTERS  ~  //
    //
    
    public AnomalyDescription getDescription() {
        return desc;
    }
}
