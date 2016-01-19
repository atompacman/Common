package com.atompacman.toolkat.module;

import org.apache.logging.log4j.Level;

import com.atompacman.toolkat.misc.Log;
import com.atompacman.toolkat.module.Report.OutputFormat;

public abstract class Observation {

    //======================================= FIELDS =============================================\\

    private final String              moduleID;
    private final StackTraceElement[] stack;
    private final long                timeNano;



    //=================================== ABSTRACT METHODS =======================================\\

    //--------------------------------------- FORMAT ---------------------------------------------\\

    public abstract Level verbose();

    public abstract String format(OutputFormat format);


    //======================================= METHODS ============================================\\

    //------------------------------------- CONSTRUCTORS -----------------------------------------\\

    Observation(String moduleID, int stackTrackLvlModifier) {
        this.moduleID = moduleID;
        
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int stackLen = stackTrace.length - stackTrackLvlModifier - 2;
        this.stack = new StackTraceElement[stackLen];
        System.arraycopy(stackTrace, stackTrackLvlModifier + 2, stack, 0, stackLen);
        
        this.timeNano = System.nanoTime();
    }


    //--------------------------------------- GETTERS --------------------------------------------\\

    public String getModuleID() {
        return moduleID;
    }
    
    public StackTraceElement[] getStack() {
        return stack;
    }

    public long getTime() {
        return timeNano;
    }


    //----------------------------------------- LOG ----------------------------------------------\\

    void log(int stackTrackLvlModifier) {
        String formatted = format(OutputFormat.CONSOLE);
        if (formatted != null) {
            Log.log(verbose(), stackTrackLvlModifier + 1, formatted);
        }
    }
}