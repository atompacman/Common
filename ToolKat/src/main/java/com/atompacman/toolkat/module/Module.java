package com.atompacman.toolkat.module;

import org.apache.logging.log4j.Level;

public abstract class Module extends BaseModule {

    //======================================= METHODS ============================================\\

    //------------------------------------- CONSTRUCTORS -----------------------------------------\\

    public Module() {
        super(Level.INFO, null);
    }

    public Module(Level verbose) {
        super(verbose, null);
    }
    
    public Module(Level verbose, BaseModule parentModule) {
        super(verbose, parentModule);
    }
    
    
    //-------------------------------------- PROCEDURE -------------------------------------------\\

    protected void procedure(Enum<?> procDesc, Object... procNameArgs) {
        profiler.startProcedure(extractProcDesc(procDesc), 
                                moduleID, 
                                1, 
                                procNameArgs);
    }

    protected void subprocedure(Enum<?> parentProcDesc, Enum<?> procDesc, Object... procNameArgs) {
        profiler.startSubProcedure(extractProcDesc(parentProcDesc),
                                   extractProcDesc(procDesc), 
                                   moduleID, 
                                   1, 
                                   procNameArgs);
    }
    
    protected void subprocedure(Enum<?> procDesc, Object... procNameArgs) {
        profiler.startSubProcedure(extractProcDesc(procDesc), 
                                   moduleID, 
                                   1, 
                                   procNameArgs);
    }
}
