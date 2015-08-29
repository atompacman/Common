package com.atompacman.toolkat.module;

import org.apache.logging.log4j.Level;

import com.atompacman.toolkat.misc.StringHelper;

public class LogEntry extends Observation {

    //====================================== CONSTANTS ===========================================\\

    public static final int NO_TITLE = -1;



    //======================================= FIELDS =============================================\\

    private final String msg;
    private final Level  verbose;
    private final int    titleSpacing;



    //======================================= METHODS ============================================\\

    //---------------------------------- PACKAGE CONSTRUCTOR -------------------------------------\\

    public LogEntry(String msg, Level verbose, int stackTrackLvlModifier) {
        this(msg, verbose, NO_TITLE, stackTrackLvlModifier + 1);
    }

    public LogEntry(String msg, Level verbose, int titleSpacing, int stackTrackLvlModifier) {
        super(stackTrackLvlModifier + 1);
        this.msg = msg;
        this.verbose = verbose;
        this.titleSpacing = titleSpacing;
    }


    //--------------------------------------- GETTERS --------------------------------------------\\

    public String getMsg() {
        return msg;
    }


    //--------------------------------------- FORMAT ---------------------------------------------\\

    public Level verbose() {
        return verbose;
    }

    public String format() {
        return titleSpacing == NO_TITLE ? msg : StringHelper.title(msg, titleSpacing);
    }
}
