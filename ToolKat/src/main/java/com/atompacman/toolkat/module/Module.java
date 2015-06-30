package com.atompacman.toolkat.module;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.logging.log4j.Level;

public abstract class Module {

    //======================================= FIELDS =============================================\\

    private Profiler profiler;
    private Level    verbose;



    //======================================= METHODS ============================================\\

    //--------------------------------- PROTECTED CONSTRUCTOR ------------------------------------\\

    protected Module() {
        this.profiler = new Profiler();
        this.verbose  = Level.INFO;
    }


    //----------------------------------- CREATE SUBMODULE ---------------------------------------\\

    protected <T extends Module> T createSubmodule(Class<T> clazz) {
        try {
            T module = clazz.newInstance();
            module.setProfiler(profiler);
            return module;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Implemented modules must have a default constructor.", e);
        }
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

    protected void log(Level verbose, String format, Object...args) {
        LogEntry entry = new LogEntry(String.format(format, args), verbose, 1);
        profiler.recordObservation(entry, 1);
    }

    protected void log(Level verbose, int titleSpacing, String format, Object...args) {
        LogEntry entry = new LogEntry(String.format(format, args), verbose, titleSpacing, 1);
        profiler.recordObservation(entry, 1);
    }


    //--------------------------------------- ANOMALY --------------------------------------------\\

    protected void signal(Enum<?> anomaly) {
        Anomaly ano = new Anomaly(extractAnomalyDesc(anomaly), 1);
        profiler.recordObservation(ano, 1);
    }

    protected void signal(Enum<?> anomaly, String details) {
        Anomaly ano = new Anomaly(extractAnomalyDesc(anomaly), details, 1);
        profiler.recordObservation(ano, 1);
    }

    protected void signal(Enum<?> anomaly, String format, Object...args) {
        Anomaly ano = new Anomaly(extractAnomalyDesc(anomaly), String.format(format, args), 1);
        profiler.recordObservation(ano, 1);
    }


    //--------------------------------------- SETTERS --------------------------------------------\\

    protected void setProfiler(Profiler profiler) {
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
