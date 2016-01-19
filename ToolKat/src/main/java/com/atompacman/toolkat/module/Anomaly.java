package com.atompacman.toolkat.module;

import org.apache.logging.log4j.Level;

import com.atompacman.toolkat.misc.StringHelper;
import com.atompacman.toolkat.module.Report.OutputFormat;

public class Anomaly extends Observation {

    //======================================= FIELDS =============================================\\

    private final AnomalyDescription description;
    private final String             details;



    //======================================= METHODS ============================================\\

    //------------------------------------- CONSTRUCTORS -----------------------------------------\\

    Anomaly(AnomalyDescription desc, String moduleID, int stackTraceMod) {
        this(desc, moduleID, null, stackTraceMod + 1);
    }

    Anomaly(AnomalyDescription desc, String moduleID, int stackTraceMod, Object...detailsArgs) {
        this(desc, moduleID, String.format(desc.detailsFormat(), detailsArgs), stackTraceMod + 1);
    }

    private Anomaly(AnomalyDescription desc, String moduleID, String details, int stackTraceMod) {
        super(moduleID, stackTraceMod);
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
        case FATAL:       return Level.FATAL;
        case CRITIC:      return Level.ERROR;
        case MODERATE:    return Level.WARN;
        case UNSPECIFIED: return Level.WARN;
        case MINIMAL:     return Level.INFO;
        default:          return null;
        }
    }

    public String format(OutputFormat format) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ANOMALY} ").append(description.name());
        if (details != null) {
            sb.append(" - ").append(details);
        }
        sb.append(StringHelper.capitalize(description.consequences())).append('.');
        return sb.toString();
    }
}
