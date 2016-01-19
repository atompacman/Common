package com.atompacman.toolkat.module;

import org.apache.logging.log4j.Level;

import com.atompacman.toolkat.misc.StringHelper;
import com.atompacman.toolkat.module.Report.OutputFormat;

public class LogEntry extends Observation {

    //====================================== CONSTANTS ===========================================\\

    public static final int NO_TITLE = -1;



    //======================================= FIELDS =============================================\\

    private final String msg;
    private final Level  verbose;
    private final int    indentationLvl;



    //======================================= METHODS ============================================\\

    //------------------------------------- CONSTRUCTORS -----------------------------------------\\

    public LogEntry(String msg, String moduleID, Level verbose, int stackTraceMod) {
        this(msg, moduleID, verbose, NO_TITLE, stackTraceMod + 1);
    }

    public LogEntry(String msg, String moduleID, Level verbose, int indentLvl, int stackTraceMod) {
        super(moduleID, stackTraceMod + 1);
        this.msg            = msg;
        this.verbose        = verbose;
        this.indentationLvl = indentLvl;
    }


    //--------------------------------------- GETTERS --------------------------------------------\\

    public String getMsg() {
        return msg;
    }


    //--------------------------------------- FORMAT ---------------------------------------------\\

    public Level verbose() {
        return verbose;
    }

    public String format(OutputFormat format) {
        StringBuilder sb = new StringBuilder();
        
        switch (format) {
        case CONSOLE:
            sb.append(indentationLvl == NO_TITLE ? msg : StringHelper.title(msg, indentationLvl));
            break;
            
        case FILE:
            for (int i = 0; i < indentationLvl; ++i) {
                sb.append('\t');
            }
            sb.append(msg);
            break;
            
        default:
            break;
        }
        return sb.toString();
    }
}
