package com.atompacman.toolkat.misc;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Log {

    //====================================== CONSTANTS ===========================================\\

    private static final Logger LOGGER = LogManager.getRootLogger();
    private static final int MSG_LENGTH_BEFORE_VERBOSE = 125;



    //======================================= METHODS ============================================\\

    public static void info(Object msg) {
        log(Level.INFO, 1, msg.toString());
    }

    public static void info(String msg, Object...params) {
        log(Level.INFO, 1, msg, params);
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
            sb.append(stack[stackLvl]);
        }

        // Add blank spaces if needed
        for (int i = sb.length() - 1; i < MSG_LENGTH_BEFORE_VERBOSE; ++i) {
            sb.append(' ');
        }

        // Append verbose
        sb.append(String.format("[%5s ] ", lvl));

        // Append message
        sb.append(msg);

        LOGGER.log(lvl, sb.toString(), params);
    }
}
