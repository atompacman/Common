package com.atompacman.toolkat.module;

import java.util.LinkedList;
import java.util.List;

public final class Report {

    //======================================= FIELDS =============================================\\

    private final List<Procedure> completedProc;



    //======================================= METHODS ============================================\\

    //---------------------------------- PACKAGE CONSTRUCTOR -------------------------------------\\

    Report() {
        this.completedProc = new LinkedList<>();
    }


    //----------------------------------------- ADD ----------------------------------------------\\

    void addCompletedProcedure(Procedure proc) {
        completedProc.add(proc);
    }
}
