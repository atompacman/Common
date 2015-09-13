package com.atompacman.toolkat.math;

public enum Epsilon {

    MILLI, MICRO, NANO, PICO, FEMTO, ATTO;


    //======================================= FIELDS =============================================\\

    private double value;
    
    

    //======================================= METHODS ============================================\\

    //--------------------------------- PRIVATE CONSTRUCTORS -------------------------------------\\

    private Epsilon() {
        this.value = Math.pow(10, -3 * (ordinal() + 1));
    }
    
    
    //--------------------------------------- GETTERS --------------------------------------------\\

    public double value() {
        return value;
    }
}