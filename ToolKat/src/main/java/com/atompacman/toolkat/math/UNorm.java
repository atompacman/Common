package com.atompacman.toolkat.math;

public class UNorm {

    //======================================= FIELDS =============================================\\

    protected double value;



    //======================================= METHODS ============================================\\

    //---------------------------------- PUBLIC CONSTRUCTOR --------------------------------------\\

    public UNorm(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Norm cannot be negative.");
        }
        if (value > 1) {
            throw new IllegalArgumentException("Norm cannot be greater than one.");
        }
        this.value = value;
    }


    //--------------------------------------- GETTERS --------------------------------------------\\

    public double v() {
        return value;
    }


    //--------------------------------------- EQUALS ---------------------------------------------\\

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UNorm other = (UNorm) obj;
        if (Double.doubleToLongBits(value) != Double
                .doubleToLongBits(other.value))
            return false;
        return true;
    }    
}
