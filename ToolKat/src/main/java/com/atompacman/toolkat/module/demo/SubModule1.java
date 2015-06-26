package com.atompacman.toolkat.module.demo;

import com.atompacman.toolkat.module.Module;
import com.atompacman.toolkat.module.ProcedureDescription;

public class SubModule1 extends Module {

    public enum SubProcedure {
        @ProcedureDescription(name = "First secondary procedure")
        SUB_PROC_A,
    }
    
    public void mainMethod() {
        procedure(SubProcedure.SUB_PROC_A);
        log("YOLO");
    }
}
