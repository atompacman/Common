package com.atompacman.toolkat.module;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.atompacman.toolkat.exception.Throw;

public final class Profiler {

    //======================================= FIELDS =============================================\\

    // Report
    private Report report;

    // Temporary
    private Map<Module, Procedure> currProc;
    private boolean                paused;



    //======================================= METHODS ============================================\\

    //---------------------------------- PACKAGE CONSTRUCTOR -------------------------------------\\

    Profiler() {
        // Report
        this.report   = new Report();

        // Temporary
        this.currProc = new HashMap<>();
        this.paused   = false;
    }


    //-------------------------------------- PROCEDURE -------------------------------------------\\

    <T extends Module> void startProcedure(ProcedureDescription procDesc, 
                                           T                    callingModule, 
                                           int                  stackTrackLvlModifier) {
        // Use root parent procedure
        startProcedure(procDesc, getParentProcedure(callingModule), 
                       callingModule, stackTrackLvlModifier + 1);
    }

    <T extends Module> void startProcedure(ProcedureDescription parentProcDesc,
                                           ProcedureDescription procDesc, 
                                           T                    callingModule, 
                                           int                  stackTrackLvlModifier) {

        // Get specified parent procedure
        Procedure parent = getParentProcedure(callingModule);
        while (parent != null && parent.getDescription() != parentProcDesc) {
            parent = parent.getLastChildProcedure();
        }
        startProcedure(procDesc, parent, callingModule, stackTrackLvlModifier + 1);
    }

    private <T extends Module> void startProcedure(ProcedureDescription newProcDesc,
                                                   Procedure            parentProc,
                                                   T                    callingModule,
                                                   int                  stackTrackLvlModifier) {

        if (paused) {
            Throw.aRuntime(TimerException.class, "Cannot start procedure: profiler is paused");
        }

        // Stop previous procedure if its not already paused
        Procedure prevProc = currProc.get(callingModule);
        if (prevProc != null) {
            if (!paused) {
                prevProc.clickHierarchy();
            }

            // Add previous procedure to report
            report.addCompletedProcedure(prevProc);
        }

        // Create new procedure and assign it to his parent
        Procedure newProc = new Procedure(newProcDesc, callingModule.getClass(), parentProc);
        currProc.put(callingModule, newProc);
        if (parentProc != null) {
            parentProc.addChild(newProc);
        }

        // Start new procedure
        newProc.click();

        // Log new procedure start
        if (!newProcDesc.name().equals(Module.DEFAULT_PROC_NAME)) {
            LogEntry entry = new LogEntry(newProcDesc.name(), Level.INFO, 
                    newProc.getGeneration(), stackTrackLvlModifier + 1);
            recordObservation(entry, stackTrackLvlModifier + 1);
        }
    }

    private <T extends Module> Procedure getParentProcedure(T callingModule) {
        // Check if a procedure is already running for this module
        Procedure proc = currProc.get(callingModule);

        if (proc == null) {
            // Parent procedure is the youngest procedure
            return getCurrentProcedure();
        } else {
            // Parent procedure is simply the parent of current procedure
            return proc.getParentProcedure();
        }
    }

    private Procedure getCurrentProcedure() {
        if (currProc.isEmpty()) {
            return null;
        } else {
            return currProc.values().iterator().next().getYoungestChildProcedure();
        }
    }

    private Procedure getRootProcedure() {
        if (currProc.isEmpty()) {
            return null;
        } else {
            return currProc.values().iterator().next().getAncesterProcedure();
        }
    }


    //------------------------------------- PAUSE/RESUME -----------------------------------------\\

    void pause() {
        if (paused) {
            Throw.aRuntime(TimerException.class, "Procedures are already paused");
        }
        Procedure rootProc = getRootProcedure();
        if (rootProc == null) {
            Throw.aRuntime(TimerException.class, "A procedure must be running before pausing");
        }
        paused = true;
        rootProc.clickHierarchy();
    }

    void resume() {
        if (!paused) {
            Throw.aRuntime(TimerException.class, "Procedures must be paused before resuming");
        }
        paused = false;
        getRootProcedure().clickHierarchy();
    }    


    //---------------------------------- RECORD OBSERVATION --------------------------------------\\

    void recordObservation(Observation obs, int stackTrackLvlModifier) {
        Procedure currProc = getCurrentProcedure();
        if (currProc == null) {
            throw new IllegalStateException("A procedure must be "
                    + "started before recording observations");
        }
        currProc.addObservation(obs);
        obs.log(stackTrackLvlModifier + 1);
    }


    //-------------------------------------- GET REPORT ------------------------------------------\\

    Report getReport() {
        Procedure root = getRootProcedure();
        if (root != null) {
            report.addCompletedProcedure(root);
        }
        return report;
    }
}
