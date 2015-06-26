package com.atompacman.toolkat.time;

public class StopWatch {

    //====================================== CONSTANTS ===========================================\\

    private static final long UNKNOWN = -1;



    //======================================= FIELDS =============================================\\

    private long    startTime;
    private long    stopTime;
    private long    elapsedTime;
    private boolean stopped;



    //======================================= METHODS ============================================\\

    //---------------------------------- PUBLIC CONSTRUCTOR --------------------------------------\\

    public StopWatch() {
        this.startTime   = UNKNOWN;
        this.stopTime    = UNKNOWN;
        this.elapsedTime = UNKNOWN;
        this.stopped     = false;
    }


    //---------------------------------------- CLICK ---------------------------------------------\\

    public void click() {
        if (startTime == UNKNOWN) {
            startTime = System.nanoTime();
        } else if (stopped) {
            stopTime = System.nanoTime();
            stopped = false;
        } else {
            if (elapsedTime == UNKNOWN) {
                stopTime = System.nanoTime();
                elapsedTime = stopTime - startTime;
            } else {
                long now = System.nanoTime();
                elapsedTime += now - stopTime;
                stopTime = now;
            }
            stopped = true;
        }
    }


    //--------------------------------------- GETTERS --------------------------------------------\\

    public long getStartTime() {
        if (startTime == UNKNOWN) {
            throwException("start", "started");
        }
        return startTime;
    }

    public long getStopTime() {
        if (startTime == UNKNOWN) {
            throwException("stop", "started");
        }
        if (!stopped) {
            throwException("stop", "stopped");
        }
        return stopTime;
    }

    public long getElapsedTime() {
        if (startTime == UNKNOWN) {
            throwException("elapsed", "started");
        }
        if (!stopped) {
            throwException("elapsed", "stopped");
        }
        return elapsedTime;
    }


    //---------------------------------- EXCEPTION HELPER ----------------------------------------\\

    private static void throwException(String x, String y) {
        throw new IllegalStateException("Could not get " + x + " time: Watch was not " + y);
    }
}