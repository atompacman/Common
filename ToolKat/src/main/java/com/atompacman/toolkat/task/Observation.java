package com.atompacman.toolkat.task;

import java.util.Date;

import org.apache.logging.log4j.Level;

import com.atompacman.toolkat.Log;

public class Observation {
    
    //
    //  ~  FIELDS  ~  //
    //

    // Metrics
    private final StackTraceElement[] stack;
    private final Date                creationTime;
    
    // Message
    private final String msg;
    private final Level  verbLvl;
    

    //
    //  ~  INIT  ~  //
    //

    Observation(String taskName,
                String msg,
                Level  verbLvl, 
                int    stackTraceMod) {
        
        this(taskName, msg, verbLvl, true, stackTraceMod + 1);
    }
    
    Observation(String  taskName,
                String  msg,
                Level   verbLvl, 
                boolean doLog, 
                int     stackTraceMod) {
        
        // Save stack trace
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int stackLen = stackTrace.length - stackTraceMod - 2;
        this.stack = new StackTraceElement[stackLen];
        System.arraycopy(stackTrace, stackTraceMod + 2, stack, 0, stackLen);
        
        // Save current time
        this.creationTime = new Date();
        
        // Save message and formatting parameters
        this.msg     = msg;
        this.verbLvl = verbLvl;
        
        // Log if needed
        if (doLog) {
            Log.log(verbLvl, stackTraceMod + 1, "%s %s", taskName, msg);
        }
    }

    
    //
    //  ~  GETTERS  ~  //
    //

    public StackTraceElement[] getStack() {
        return stack;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public String getMessage() {
        return msg;
    }

    public Level getVerboseLevel() {
        return verbLvl;
    }
    

    //
    //  ~  SERIALIZATION  ~  //
    //

    @Override
    public String toString() {
        return msg;
    }
}