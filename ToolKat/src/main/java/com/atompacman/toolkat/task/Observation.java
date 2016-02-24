package com.atompacman.toolkat.task;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.Level;

import com.atompacman.toolkat.Log;

public class Observation {

    //
    //  ~  CONSTANTS  ~  //
    //

    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss:SSS";
    
    
    //
    //  ~  FIELDS  ~  //
    //

    // Metrics
    private final StackTraceElement[] stack;
    private final Date                creationTime;
    
    // Message
    private final String msg;
    private final int    indentLvl;
    private final Level  verbLvl;
    

    //
    //  ~  INIT  ~  //
    //

    Observation(String  msg, 
                int     indentLvl, 
                Level   verbLvl, 
                int     stackTraceMod) {
        
        this(msg, indentLvl, verbLvl, true, stackTraceMod);
    }
    
    Observation(String  msg, 
                int     indentLvl, 
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
        this.msg       = msg;
        this.indentLvl = indentLvl;
        this.verbLvl   = verbLvl;
        
        // Log if needed
        if (doLog) {
            Log.log(verbLvl, stackTraceMod + 1, getMessage());
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

    public String getRawMessage() {
        return msg;
    }

    public Level getVerboseLevel() {
        return verbLvl;
    }
    

    //
    //  ~  SERIALIZATION  ~  //
    //

    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indentLvl; ++i) {
            sb.append('\t');
        }
        sb.append('[').append(new SimpleDateFormat(DATE_FORMAT).format(creationTime)).append("] ");
        sb.append(msg);
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return getRawMessage();
    }
}