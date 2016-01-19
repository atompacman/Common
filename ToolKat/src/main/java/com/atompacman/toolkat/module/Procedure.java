package com.atompacman.toolkat.module;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import com.atompacman.toolkat.module.Report.OutputFormat;
import com.atompacman.toolkat.time.StopWatch;

public final class Procedure extends StopWatch {

    //======================================= FIELDS =============================================\\

    private final String                name;
    private final ProcedureDescription  desc;
    private final String                assocModuleID;
    private final Procedure             parent;
    private final List<Observation>     obs;
    private final LinkedList<Procedure> children;



    //======================================= METHODS ============================================\\

    //------------------------------------- CONSTRUCTORS -----------------------------------------\\

    Procedure(ProcedureDescription desc, String moduleID, Object... procNameArgs) {
        this(desc, moduleID, null, procNameArgs);
    }

    Procedure(ProcedureDescription desc, String moduleID, Procedure parent, Object...procNameArgs) {
        this.name          = String.format(desc.nameFormat(), procNameArgs);
        this.desc          = desc;
        this.assocModuleID = moduleID;
        this.parent        = parent;
        this.obs           = new LinkedList<>();
        this.children      = new LinkedList<>();
        
        // Attach current procedure to its parent
        if (parent != null) {
            Procedure child = parent.getLastChildProcedure();
            if (child != null) {
                child.clickHierarchy();
            }
            parent.children.add(this);
        }
        
        // Start current procedure
        click();
    }


    //--------------------------------------- SETTERS --------------------------------------------\\

    void addObservation(Observation ob, int stackTrackLvlModifier) {
        obs.add(ob);
        ob.log(stackTrackLvlModifier + 1);
    }

    void clickHierarchy() {
        click();
        if (!children.isEmpty()) {
            children.peekLast().clickHierarchy();
        }
    }


    //--------------------------------------- GETTERS --------------------------------------------\\

    String getName() {
        return name;
    }

    ProcedureDescription getDescription() {
        return desc;
    }

    String getAssociatedModuleID() {
        return assocModuleID;
    }

    Procedure getParentProcedure() {
        return parent;
    }

    List<Observation> getObservations() {
        return obs;
    }

    List<Procedure> getChildProcedures() {
        return children;
    }

    int getGeneration() {
        return parent == null ? 0 : parent.getGeneration() + 1;
    }

    Procedure getLastChildProcedure() {
        return children.isEmpty() ? null : children.peekLast();
    }

    Procedure getAncesterProcedure() {
        return parent == null ? this : parent.getAncesterProcedure();
    }

    Procedure getYoungestChildProcedure() {
        return children.isEmpty() ? this : children.peekLast().getYoungestChildProcedure();
    }


    //---------------------------------------- PRINT ---------------------------------------------\\

    public void print(PrintStream out) {
        out.print('[');
        out.print(assocModuleID);
        out.print("] ");
        out.println(name);
        for (Observation ob : obs) {
            out.println(ob.format(OutputFormat.FILE));
        }
        for (Procedure child : children) {
            child.print(out);
            out.println();
        }
    }
    
    
    //-------------------------------------- TO STRING -------------------------------------------\\

    public String toString() {
        return assocModuleID + " : " + name;
    }
}
