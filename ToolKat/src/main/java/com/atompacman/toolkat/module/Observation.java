package com.atompacman.toolkat.module;

import org.apache.logging.log4j.Level;

import com.atompacman.toolkat.misc.Log;

public abstract class Observation {

    //======================================= FIELDS =============================================\\

    private final StackTraceElement[] stack;
    private final long				  time;



    //=================================== ABSTRACT METHODS =======================================\\

    //--------------------------------------- FORMAT ---------------------------------------------\\

    public abstract Level verbose();

    public abstract String format();



    //======================================= METHODS ============================================\\

    //--------------------------------- PACKAGE CONSTRUCTORS -------------------------------------\\

    Observation(int stackTrackLvlModifier) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int stackLen = stackTrace.length - stackTrackLvlModifier - 2;
        this.stack = new StackTraceElement[stackLen];

        System.arraycopy(stackTrace, stackTrackLvlModifier + 2, stack, 0, stackLen);
        this.time = System.nanoTime();
    }


    //--------------------------------------- GETTERS --------------------------------------------\\

    public StackTraceElement[] getStack() {
        return stack;
    }

    public long getTime() {
        return time;
    }


    //----------------------------------------- LOG ----------------------------------------------\\

    void log(int stackTrackLvlModifier) {
        String formatted = format();
        if (formatted != null) {
            Log.log(verbose(), stackTrackLvlModifier + 1, formatted);
        }
    }
}