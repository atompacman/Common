package com.atompacman.toolkat.module;

import org.apache.logging.log4j.Level;

public class Anomaly extends Observation {

    //======================================= FIELDS =============================================\\

    private final AnomalyDescription description;
    private final String             details;



    //======================================= METHODS ============================================\\

    //--------------------------------- PACKAGE CONSTRUCTORS -------------------------------------\\

    Anomaly(AnomalyDescription desc, int stackTrackLvlModifier) {
        this(desc, null, stackTrackLvlModifier + 1);
    }

    Anomaly(AnomalyDescription desc, int stackTrackLvlModifier, Object...detailsArgs) {
        this(desc, String.format(desc.detailsFormat(), detailsArgs), stackTrackLvlModifier + 1);
    }


    //--------------------------------- PRIVATE CONSTRUCTORS -------------------------------------\\

    private Anomaly(AnomalyDescription desc, String details, int stackTrackLvlModifier) {
        super(stackTrackLvlModifier);
        this.description = desc;
        this.details     = details;
    }


    //--------------------------------------- GETTERS --------------------------------------------\\

    public AnomalyDescription getDescription() {
        return description;
    }

    public String getDetails() {
        return details;
    }


    //---------------------------------------- STATE ---------------------------------------------\\

    public boolean hasDetails() {
        return details != null;
    }


    //--------------------------------------- FORMAT ---------------------------------------------\\

    public Level verbose() {
        switch (description.severity()) {
        case FATAL:     return Level.FATAL;
        case CRITIC:    return Level.ERROR;
        case MODERATE: 	return Level.WARN;
        case MINIMAL:   return Level.INFO;
        case NONE:      return Level.DEBUG;
        default:        return null;
        }
    }

    public String format() {
        String conseq = description.consequences();
        return "{ANOMALY} " + description.name() + (details == null ? "" : " - " + details) 
                + ": " + conseq.substring(0, 1).toLowerCase() + conseq.substring(1) + ".";
    }
}
