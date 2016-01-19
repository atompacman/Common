package com.atompacman.toolkat.module;

import org.apache.logging.log4j.Level;

final class Profiler {

    //===================================== INNER TYPES ==========================================\\

    private enum Status { RUNNING, PAUSED, STOPPED };



    //======================================= FIELDS =============================================\\

    private final Report    report;
    private       Procedure currProc;
    private       Status    status;



    //======================================= METHODS ============================================\\

    //------------------------------------- CONSTRUCTORS -----------------------------------------\\

    Profiler() {
        this.report   = new Report();
        this.currProc = null;
        this.status   = Status.RUNNING;
    }


    //-------------------------------------- PROCEDURE -------------------------------------------\\

    <T extends BaseModule> void startProcedure(ProcedureDescription procDesc, 
                                               String               moduleID, 
                                               int                  stackTrackLvlModifier,
                                               Object...            procNameArgs) {

        // Find last procedure started by current module
        Procedure proc = currProc;
        while (proc != null && !proc.getAssociatedModuleID().equals(moduleID)) {
            proc = proc.getParentProcedure();
        }

        // First parent of first procedure started by current module
        while (proc != null && proc.getAssociatedModuleID().equals(moduleID)) {
            proc = proc.getParentProcedure();
        }

        if (proc == null) {
            if (currProc != null) { 
                Procedure ancester = currProc.getAncesterProcedure();
                if (ancester.getAssociatedModuleID().equals(moduleID)) {
                    report.addCompletedProcedure(ancester);
                } else {
                    proc = currProc;
                }
            } else {
                proc = currProc;
            }
        }
 
        startProcedureImpl(stackTrackLvlModifier + 1, procDesc, proc, moduleID, procNameArgs);
    }

    <T extends BaseModule> void startSubProcedure(ProcedureDescription procDesc, 
                                                  String               moduleID, 
                                                  int                  stackTrackLvlModifier,
                                                  Object...            procNameArgs) {

        if (currProc == null) {
            throw new IllegalStateException("Cannot start a sub-procedure "
                    + "when no procedure at all was started");
        }
        startProcedureImpl(stackTrackLvlModifier + 1, procDesc, currProc, moduleID, procNameArgs);
    }

    <T extends BaseModule> void startSubProcedure(ProcedureDescription parentProcDesc,
                                                  ProcedureDescription procDesc, 
                                                  String               moduleID, 
                                                  int                  stackTrackLvlModifier,
                                                  Object...            procNameArgs) {

        if (currProc == null) {
            throw new IllegalStateException("Cannot start a sub-procedure "
                    + "when no procedure at all was started");
        }
        Procedure parent = currProc.getParentProcedure();
        while (parent.getDescription() != parentProcDesc) {
            parent = parent.getParentProcedure();
            if (parent == null) {
                throw new IllegalArgumentException("Procedure \"" + currProc + "\" has "
                        + "no parent procedure \"" + parentProcDesc.nameFormat() + "\"");
            }
        }
        startProcedureImpl(stackTrackLvlModifier + 1, procDesc, parent, moduleID, procNameArgs);
    }

    private <T extends BaseModule> void startProcedureImpl(int                  stackTraceLvlMod,
                                                           ProcedureDescription procDesc,
                                                           Procedure            parentProc, 
                                                           String               moduleID, 
                                                           Object...            procNameArgs) {

        if (status != Status.RUNNING) {
            throw new IllegalStateException("Cannot start procedures "
                    + "when profiler is paused or stopped");
        }

        currProc = new Procedure(procDesc, moduleID, parentProc, procNameArgs);

        if (!procDesc.nameFormat().equals(BaseModule.DEFAULT_PROC_NAME)) {
            LogEntry entry = new LogEntry(currProc.getName(), moduleID, Level.INFO,
                    currProc.getGeneration() + 1, stackTraceLvlMod + 1);
            currProc.addObservation(entry, stackTraceLvlMod + 1);
        }
    }


    //------------------------------------- PAUSE/RESUME -----------------------------------------\\

    void pause() {
        if (status != Status.RUNNING) {
            throw new IllegalStateException("Procedure must be running to be paused");
        }
        status = Status.PAUSED;
        currProc.getAncesterProcedure().clickHierarchy();
    }

    void resume() {
        if (status != Status.PAUSED) {
            throw new IllegalStateException("Procedures must be paused to resume");
        }
        status = Status.RUNNING;
        currProc.getAncesterProcedure().clickHierarchy();
    }


    //---------------------------------- RECORD OBSERVATION --------------------------------------\\

    <T extends BaseModule> void recordObservation(Observation obs, String moduleID, int stackTLMod){
        if (status != Status.RUNNING) {
            throw new IllegalStateException("Procedures must be running to record observations");
        }
        if (currProc == null) {
            throw new IllegalStateException("A procedure must be "
                    + "started before recording observations");
        }
        currProc.addObservation(obs, stackTLMod + 1);
    }


    //-------------------------------------- GET REPORT ------------------------------------------\\

    Report getReport() {
        if (currProc != null) {
            Procedure proc = currProc.getAncesterProcedure();
            if (status == Status.RUNNING) {
                proc.clickHierarchy();
            }
            report.addCompletedProcedure(proc);
        }
        status = Status.STOPPED;
        return report;
    }
}
