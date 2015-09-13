package com.atompacman.toolkat.module;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.logging.log4j.Level;

import com.atompacman.toolkat.exception.AbstractException;
import com.atompacman.toolkat.exception.Throw;

public abstract class Module {

    //====================================== CONSTANTS ===========================================\\

    public static final String DEFAULT_PROC_NAME = "Default procedure";



    //===================================== INNER TYPES ==========================================\\

    public enum Default {
        @ProcedureDescription(name = DEFAULT_PROC_NAME)
        DEFAULT_PROCEDURE;
    }



    //======================================= FIELDS =============================================\\

    private Profiler profiler;
    private Level    verbose;



    //======================================= METHODS ============================================\\

    //--------------------------------- PUBLIC CONSTRUCTORS --------------------------------------\\

    public Module() {
        this(Level.INFO);
    }

    public Module(Level verbose) {
        this.profiler = new Profiler();
        this.verbose  = verbose;
        procedure(Default.DEFAULT_PROCEDURE);
    }


    //---------------------------------- REGISTER SUBMODULE --------------------------------------\\

    protected void registerSubmodule(Module subModule) {
        subModule.setProfiler(profiler);
    }


    //-------------------------------------- PROCEDURE -------------------------------------------\\

    protected void procedure(Enum<?> procDesc) {
        profiler.startProcedure(extractProcDesc(procDesc), this, 1);
    }

    protected void subprocedure(Enum<?> parentProcDesc, Enum<?> procDesc) {
        profiler.startProcedure(extractProcDesc(parentProcDesc),extractProcDesc(procDesc), this, 1);
    }


    //----------------------------------------- LOG ----------------------------------------------\\

    protected void log(String format, Object...args) {
        LogEntry entry = new LogEntry(String.format(format, args), verbose, 1);
        profiler.recordObservation(entry, 1);
    }

    protected void log(int stackTrackModifier, String format, Object...args) {
        LogEntry entry = new LogEntry(String.format(format, args), verbose, 1);
        profiler.recordObservation(entry, stackTrackModifier + 1);
    }

    protected void log(Level verbose, String format, Object...args) {
        LogEntry entry = new LogEntry(String.format(format, args), verbose, 1);
        profiler.recordObservation(entry, 1);
    }

    protected void log(Level verbose, int titleSpacing, String format, Object...args) {
        LogEntry entry = new LogEntry(String.format(format, args), verbose, titleSpacing, 1);
        profiler.recordObservation(entry, 1);
    }

    protected void log(Level verbose, int titleSpacing, int stModif, String format, Object...args) {
        LogEntry entry = new LogEntry(String.format(format, args), verbose, titleSpacing, 1);
        profiler.recordObservation(entry, stModif + 1);
    }


    //--------------------------------------- ANOMALY --------------------------------------------\\

    protected void signal(Enum<?> anomaly) {
        Anomaly ano = new Anomaly(extractAnomalyDesc(anomaly), 1);
        profiler.recordObservation(ano, 1);
    }

    protected void signal(Enum<?> anomaly, Object...args) {
        Anomaly ano = new Anomaly(extractAnomalyDesc(anomaly), 1, args);
        profiler.recordObservation(ano, 1);
    }

    protected <T extends AbstractException> void signalException(Enum<?>   anomaly, 
            Class<T>  exceptionClass, 
            Object... args) throws T {

        Anomaly ano = new Anomaly(extractAnomalyDesc(anomaly), 1, args);
        profiler.recordObservation(ano, 1);
        Throw.a(exceptionClass, ano.getDetails());
    }

    protected <T extends AbstractException> void signalException(Enum<?>   anomaly, 
            Class<T>  exceptionClass, 
            Throwable cause, 
            Object... args) throws T {

        Anomaly ano = new Anomaly(extractAnomalyDesc(anomaly), 1, args);
        profiler.recordObservation(ano, 1);
        Throw.a(exceptionClass, ano.getDetails(), cause);
    }


    //--------------------------------------- SETTERS --------------------------------------------\\

    private void setProfiler(Profiler profiler) {
        this.profiler = profiler;
    }

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
        this.verbose  = Level.INFO;
    }


    //--------------------------------------- HELPERS --------------------------------------------\\

    private static ProcedureDescription extractProcDesc(Enum<?> procDesc) {
        return extractAnnotation(procDesc, ProcedureDescription.class);
    }

    private static AnomalyDescription extractAnomalyDesc(Enum<?> anomalyDesc) {
        return extractAnnotation(anomalyDesc, AnomalyDescription.class);
    }

    private static <T extends Annotation> T extractAnnotation(Enum<?> annotEnum, Class<T> clazz) {
        try {
            Field field = annotEnum.getClass().getField(annotEnum.toString());
            return field.getAnnotation(clazz);
        } catch (Exception e) {
            throw new RuntimeException(annotEnum + " is not an enum field "
                    + "with the " + clazz.getSimpleName() + " annotation", e);
        }
    }
}
