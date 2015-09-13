package com.atompacman.toolkat.math;

public class MathUtils {

    //======================================= METHODS ============================================\\

    //--------------------------------------- EQUALS ---------------------------------------------\\

    public static boolean almostEquals(double x, double y, Epsilon e) {
        return Math.abs(x - y) < e.value();
    }
}
