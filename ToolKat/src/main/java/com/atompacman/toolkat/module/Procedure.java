package com.atompacman.toolkat.module;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.atompacman.toolkat.time.StopWatch;

public final class Procedure extends StopWatch {

    //======================================= FIELDS =============================================\\

    private final ProcedureDescription    desc;
    private final Class<? extends Module> assocModule;
    private final List<Observation>       obs;

    private final Procedure               parent;
    private final LinkedList<Procedure>   children;



    //======================================= METHODS ============================================\\

    //---------------------------------- PACKAGE CONSTRUCTOR -------------------------------------\\

    Procedure(ProcedureDescription cp, Class<? extends Module> assocModule) {
        this(cp, assocModule, null);
    }
    
    Procedure(ProcedureDescription cp, Class<? extends Module> assocModule, Procedure parent) {
        this.desc        = cp;
        this.assocModule = assocModule;
        this.obs         = new ArrayList<>();

        this.parent      = parent;
        this.children    = new LinkedList<>();
    }


    //--------------------------------------- SETTERS --------------------------------------------\\

    void addObservation(Observation ob) {
        obs.add(ob);
    }

    void addChild(Procedure procedure) {
        children.add(procedure);
    }

    void clickHierarchy() {
        click();
        if (!children.isEmpty()) {
            children.peekLast().clickHierarchy();
        }
    }


    //--------------------------------------- GETTERS --------------------------------------------\\

    ProcedureDescription getDescription() {
        return desc;
    }

    Class<? extends Module> getAssociatedModule() {
        return assocModule;
    }

    List<Observation> getObservations() {
        return obs;
    }

    Procedure getParentProcedure() {
        return parent;
    }

    List<Procedure> getChildProcedures() {
        return children;
    }

    int getGeneration() {
        return parent == null ? 0 : parent.getGeneration() + 1;
    }
    
    Procedure getLastChildProcedure() {
        if (children.isEmpty()) {
            throw new IllegalStateException("Procedure \"" + desc.name()
                    + "\" does not have a child procedure");
        }
        return children.peekLast();
    }

    Procedure getAncesterProcedure() {
        return parent == null ? this : parent.getAncesterProcedure();
    }
    
    Procedure getYoungestChildProcedure() {
        return children.isEmpty() ? this : children.peekLast().getYoungestChildProcedure();
    }
}
