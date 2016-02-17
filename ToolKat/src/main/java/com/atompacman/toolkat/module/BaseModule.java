package com.atompacman.toolkat.module;

import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.Level;

import com.atompacman.toolkat.misc.EnumUtils;
import com.atompacman.toolkat.misc.StringHelper;

public abstract class BaseModule {

    //====================================== CONSTANTS ===========================================\\

    public static final String DEFAULT_PROC_NAME = "Default procedure";



    //===================================== INNER TYPES ==========================================\\

    public enum Default {
        @ProcedureDescription(nameFormat = DEFAULT_PROC_NAME)
        DEFAULT_PROCEDURE;
    }



    //==================================== STATIC FIELDS =========================================\\

    private static int lastModuleID = 0; 
    
    
    
    //======================================= FIELDS =============================================\\

    protected       Profiler profiler;
    private         Level    verbose;
    protected final String   moduleID;
    


    //======================================= METHODS ============================================\\

    //------------------------------------- CONSTRUCTORS -----------------------------------------\\

    public BaseModule() {
        this(Level.DEBUG);
    }

    public BaseModule(Level verbose) {
        this(verbose, null);
        profiler.startProcedure(extractProcDesc(Default.DEFAULT_PROCEDURE), moduleID, 1);
    }

    public BaseModule(Level verbose, BaseModule parentModule) {
        this.profiler = parentModule == null ? new Profiler() : parentModule.profiler;
        this.verbose  = verbose;
        this.moduleID = StringHelper.splitClassName(this) + "[" + lastModuleID++ + "]";
    }
    

    //----------------------------------------- LOG ----------------------------------------------\\

    protected void log(String format, Object...args) {
        LogEntry entry = new LogEntry(String.format(format, args), moduleID, verbose, 1);
        profiler.recordObservation(entry, moduleID, 1);
    }

    protected void log(int stackTrackModifier, String format, Object...args) {
        LogEntry entry = new LogEntry(String.format(format, args), moduleID, verbose, 1);
        profiler.recordObservation(entry, moduleID, stackTrackModifier + 1);
    }

    protected void log(Level verbose, String format, Object...args) {
        LogEntry entry = new LogEntry(String.format(format, args), moduleID, verbose, 1);
        profiler.recordObservation(entry, moduleID, 1);
    }

    protected void log(Level verbose, int titleSpacing, String format, Object...args) {
        LogEntry entry = new LogEntry(String.format(format,args),moduleID, verbose, titleSpacing,1);
        profiler.recordObservation(entry, moduleID, 1);
    }

    protected void log(Level verbose, int titleSpacing, int stModif, String format, Object...args) {
        LogEntry entry = new LogEntry(String.format(format,args),moduleID, verbose, titleSpacing,1);
        profiler.recordObservation(entry, moduleID, stModif + 1);
    }


    //--------------------------------------- ANOMALY --------------------------------------------\\

    protected void signal(Enum<?> anomaly) {
        Anomaly ano = new Anomaly(extractAnomalyDesc(anomaly), moduleID, 1);
        profiler.recordObservation(ano, moduleID, 1);
    }

    protected void signal(Enum<?> anomaly, Object...args) {
        Anomaly ano = new Anomaly(extractAnomalyDesc(anomaly), moduleID, 1, args);
        profiler.recordObservation(ano, moduleID, 1);
    }

    protected <T extends Exception> void signalException(Enum<?>   anomaly, 
                                                         Class<T>  exceptionClass, 
                                                         Object... args) throws T {

        Anomaly ano = new Anomaly(extractAnomalyDesc(anomaly),moduleID, 1, args);
        profiler.recordObservation(ano, moduleID, 1);
        try {
            throw exceptionClass.getConstructor(String.class).newInstance(ano.getDetails());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T extends Exception> void signalException(Enum<?>   anomaly, 
                                                         Class<T>  exceptionClass, 
                                                         Throwable cause, 
                                                         Object... args) throws T {

        Anomaly ano = new Anomaly(extractAnomalyDesc(anomaly), moduleID, 1, args);
        profiler.recordObservation(ano, moduleID, 1);
        try {
            throw exceptionClass.getConstructor(String.class, Throwable.class)
                .newInstance(ano.getDetails(), cause);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }


    //--------------------------------------- SETTERS --------------------------------------------\\

    protected void setVerbose(Level verbose) {
        this.verbose = verbose;
    }


    //-------------------------------------- GET REPORT ------------------------------------------\\

    public Report getReport() {
        return profiler.getReport();
    }


    //---------------------------------------- RESET ---------------------------------------------\\

    protected void reset() {
        this.profiler = new Profiler();
    }


    //--------------------------------------- HELPERS --------------------------------------------\\

    protected static ProcedureDescription extractProcDesc(Enum<?> procDesc) {
        return EnumUtils.extractAnnotation(procDesc, ProcedureDescription.class);
    }
    
    protected static AnomalyDescription extractAnomalyDesc(Enum<?> anomalyDesc) {
        return EnumUtils.extractAnnotation(anomalyDesc, AnomalyDescription.class);
    }
}
