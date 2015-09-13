package com.atompacman.toolkat.math;

public class Probability extends Norm implements Comparable<Probability> {

    //====================================== CONSTANTS ===========================================\\

    public static final Probability CERTAIN     = new Probability(1.0);
    public static final Probability FIFTY_FIFTY = new Probability(0.5);
    public static final Probability IMPOSSIBLE  = new Probability(0.0);

    
    
    //======================================= METHODS ============================================\\

    //--------------------------------- PUBLIC CONSTRUCTORS --------------------------------------\\

    public Probability(double value) {
        super(value);
    }
    
    
    
    //---------------------------------------- STATE ---------------------------------------------\\

    public boolean isImpossible(Epsilon e) {
        return MathUtils.almostEquals(value, 0.0, e);
    }
    
    public boolean isCertain(Epsilon e) {
        return MathUtils.almostEquals(value, 1.0, e);
    }


    //--------------------------------------- EQUALS ---------------------------------------------\\
    
    public int compareTo(Probability o) {
        return Double.compare(value, o.value);
    }
}
