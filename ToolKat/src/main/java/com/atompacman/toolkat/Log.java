package com.atompacman.toolkat;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Log {

    //
    //  ~  CONSTANTS  ~  //
    //

    private static final Logger LOGGER = LogManager.getRootLogger();
    private static final int    MSG_LENGTH_BEFORE_VERBOSE = 130;
    
    
    //
    //  ~  CONSTANTS  ~  //
    //
    
    private static String ignoredPackagePart;
    

    //
    //  ~  INIT  ~  //
    //

    static {
        ignoredPackagePart = "";
    }
    
    private Log() {
        
    }
    
    
    //
    //  ~  SETTERS  ~  //
    //
    
    public static void setIgnoredPackagePart(String ignored) {
        ignoredPackagePart = ignored.charAt(ignored.length() - 1) == '.' ? ignored : ignored + ".";
        info("Logger is set to ignore package part \"%s\" when printing calling methods", ignored);
    }
    
    
    //
    //  ~  LOG  ~  //
    //

    public static void debug(Object msg) {
        log(Level.DEBUG, 1, msg.toString());
    }

    public static void debug(String msg, Object...params) {
        log(Level.DEBUG, 1, msg, params);
    }

    public static void info(Object msg) {
        log(Level.INFO, 1, msg.toString());
    }

    public static void info(String msg, Object...params) {
        log(Level.INFO, 1, msg, params);
    }

    public static void warn(Object msg) {
        log(Level.WARN, 1, msg.toString());
    }

    public static void warn(String msg, Object...params) {
        log(Level.WARN, 1, msg, params);
    }

    public static void error(Object msg) {
        log(Level.ERROR, 1, msg.toString());
    }

    public static void error(String msg, Object...params) {
        log(Level.ERROR, 1, msg, params);
    }

    public static void trace(Object msg) {
        log(Level.TRACE, 1, msg.toString());
    }

    public static void trace(String msg, Object...params) {
        log(Level.TRACE, 1, msg, params);
    }

    public static void log(Level lvl, int stackDepthModifier, String msg, Object...params) {
        StringBuilder sb = new StringBuilder();

        // Append date/time
        sb.append(new SimpleDateFormat("YYYY-MM-dd|HH:mm:ss.SSS| ").format(new Date()));

        // Append calling method
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        if (stackDepthModifier < 0) {
            throw new IllegalArgumentException("Stack depth modifier cannot be negative");
        }
        int stackLvl = stackDepthModifier + 2;
        if (stackLvl >= stack.length) {
            sb.append("STACK TRACE DEPTH MODIFIER ERROR");
        } else {
            sb.append(stack[stackLvl].toString().replaceFirst(ignoredPackagePart, ""));
        }

        // Add blank spaces if needed
        for (int i = sb.length() - 1; i < MSG_LENGTH_BEFORE_VERBOSE; ++i) {
            sb.append(' ');
        }

        // Append verbose
        sb.append('{').append(lvl.name().charAt(0)).append('}').append(' ');

        // Append message
        sb.append(String.format(msg.toString(), params));

        LOGGER.log(lvl, sb);
    }
}
