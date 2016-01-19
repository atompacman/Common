package com.atompacman.toolkat.module;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

public final class Report {

    //===================================== INNER TYPES ==========================================\\

    public enum OutputFormat {
        CONSOLE, FILE
    }
    
    
    
    //======================================= FIELDS =============================================\\

    private final List<Procedure> completedProc;



    //======================================= METHODS ============================================\\

    //------------------------------------- CONSTRUCTORS -----------------------------------------\\

    Report() {
        this.completedProc = new LinkedList<>();
    }


    //----------------------------------------- ADD ----------------------------------------------\\

    void addCompletedProcedure(Procedure proc) {
        completedProc.add(proc);
    }

    
    //--------------------------------------- GETTERS --------------------------------------------\\

    public List<Procedure> getProcedures() {
        return completedProc;
    }
    
    
    //---------------------------------------- PRINT ---------------------------------------------\\

    public void print(PrintStream out) {
        for (Procedure proc : completedProc) {
            proc.print(out);
            out.println();
        }
    }
}
