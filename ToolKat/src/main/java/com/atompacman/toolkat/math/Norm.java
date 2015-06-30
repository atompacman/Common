package com.atompacman.toolkat.math;

public class Norm {

    //======================================= FIELDS =============================================\\

    private double value;



    //======================================= METHODS ============================================\\

    //---------------------------------- PUBLIC CONSTRUCTOR --------------------------------------\\

    public Norm(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Norm cannot be negative.");
        }
        if (value > 1) {
            throw new IllegalArgumentException("Norm cannot be greater than one.");
        }
        this.value = value;
    }


    //--------------------------------------- GETTERS --------------------------------------------\\

    public boolean doUniformRandomTest() {
        return RandNumGen.nextDouble(0, 1) < value;
    }


    //--------------------------------------- GETTERS --------------------------------------------\\

    public double v() {
        return value;
    }
}
