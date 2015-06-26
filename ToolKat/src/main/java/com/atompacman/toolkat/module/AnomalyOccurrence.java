package com.atompacman.toolkat.module;

import org.apache.logging.log4j.Level;

public class AnomalyOccurrence extends Observation {

    //======================================= FIELDS =============================================\\

    private final Anomaly anomaly;
    private final String  details;



    //======================================= METHODS ============================================\\

    //--------------------------------- PACKAGE CONSTRUCTORS -------------------------------------\\

    AnomalyOccurrence(Object anomaly, int stackTrackLvlModifier) {
        this(anomaly, null, stackTrackLvlModifier + 1);
    }

    AnomalyOccurrence(Object anomaly, String details, int stackTrackLvlModifier) {
        super(stackTrackLvlModifier);
        try {
            this.anomaly = anomaly.getClass().getField(anomaly.toString())
                    .getAnnotation(Anomaly.class);
        } catch (Exception e) {
            throw new RuntimeException(anomaly + " is not an enum field with "
                    + "the " + Anomaly.class.getSimpleName() + " annotation", e);
        }
        this.details = details;
    }


    //--------------------------------------- GETTERS --------------------------------------------\\

    public Anomaly getAnomalyInfo() {
        return anomaly;
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
        switch (anomaly.impact()) {
        case FATAL:     return Level.FATAL;
        case CRITIC: 	return Level.WARN;
        case MODERATE: 	return Level.INFO;
        case MINIMAL:   return Level.DEBUG;
        case NONE: 		return Level.TRACE;
        }
        return null;
    }

    public String format() {
        String conseq = anomaly.consequences();
        return "{ANOMALY} " + anomaly.name() + (details == null ? "" : " - " + details) 
                + ": " + conseq.substring(0, 1).toLowerCase() + conseq.substring(1) + ".";
    }
}
