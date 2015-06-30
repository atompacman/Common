package com.atompacman.toolkat.module.demo;

import com.atompacman.toolkat.misc.Log;
import com.atompacman.toolkat.module.AnomalyDescription;
import com.atompacman.toolkat.module.AnomalyDescription.Impact;
import com.atompacman.toolkat.module.AnomalyDescription.Recoverability;
import com.atompacman.toolkat.module.Module;
import com.atompacman.toolkat.module.ProcedureDescription;

public class MainModule1 extends Module {

    public enum MainProcedure {
        @ProcedureDescription(name = "First main procedure")
        PROC_A,
        
        @ProcedureDescription(name = "Second main procedure")
        PROC_B;
    }
    
    public enum MainAnomaly {
        @AnomalyDescription(consequences   = "bricks will be", 
                            description    = "ayoyoyo", 
                            impact         = Impact.FATAL, 
                            name           = "AYOOO", 
                            recoverability = Recoverability.TRIVIAL)
        ANOM_A;
    }
    
    public static void main(String[] args) {
        new MainModule1().mainMethod();
    }
    
    public void mainMethod() {
        procedure(MainProcedure.PROC_A);

        log("Yolo");
        signal(MainAnomaly.ANOM_A);
        
        secondaryMethod();
    }
    
    private void secondaryMethod() {
        procedure(MainProcedure.PROC_B);
        Log.info("Ayoyoyo");
        createSubmodule(SubModule1.class).mainMethod();
    }
}
