package com.atompacman.toolkat.math;

import java.util.Random;

public class RandGen {

    //==================================== STATIC FIELDS =========================================\\

    private static Random randGen;



    //======================================= METHODS ============================================\\

    //--------------------------------- STATIC INITIALIZATION ------------------------------------\\

    static {
        randGen = new Random();
    }



    //------------------------------------ SET NEW SEED ------------------------------------------\\

    public static void setNewSeed(long seed) {
        randGen.setSeed(seed);
    }


    //---------------------------------------- NEXT ----------------------------------------------\\

    public static double nextDouble(double min, double max) {
        return min + randGen.nextDouble() * max;
    }

    public static int nextInt(int min, int max) {
        return min + randGen.nextInt(max + 1);
    }
    
    public static byte nextByte(byte min, byte max) {
        return (byte) (min + randGen.nextInt(max + 1));
    }
}
