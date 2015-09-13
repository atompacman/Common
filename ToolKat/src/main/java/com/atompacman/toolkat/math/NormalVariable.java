package com.atompacman.toolkat.math;

import java.util.List;

import org.apache.commons.math3.stat.StatUtils;

public class NormalVariable {

    //===================================== INNER TYPES ==========================================\\

    @FunctionalInterface public interface DoubleGetter<T> { double getDouble(T obj); }
    
    
    
    //======================================= FIELDS =============================================\\

    private final double mean;
    private final double variance;



    //======================================= METHODS ============================================\\

    //--------------------------------- PUBLIC CONSTRUCTORS --------------------------------------\\

    public NormalVariable(double mean, double variance) {
        this.mean     = mean;
        this.variance = variance;
    }

    public static <T> NormalVariable computeFrom(double[] values) {
        double mean     = StatUtils.mean(values);
        double variance = StatUtils.variance(values, mean);
        return new NormalVariable(mean, variance);
    }

    public static <T> NormalVariable computeFrom(List<T> objects, DoubleGetter<T> getter) {
        double[] values = new double[objects.size()];
        for (int i = 0; i < objects.size(); ++i) {
            values[i] = getter.getDouble(objects.get(i));
        }
        return computeFrom(values);
    }
    

    //--------------------------------------- GETTERS --------------------------------------------\\

    public double mean() {
        return mean;
    }

    public double variation() {
        return variance;
    }
    
    
    //--------------------------------------- EQUALS ---------------------------------------------\\

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(mean);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(variance);
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
        NormalVariable other = (NormalVariable) obj;
        if (Double.doubleToLongBits(mean) != Double
                .doubleToLongBits(other.mean))
            return false;
        if (Double.doubleToLongBits(variance) != Double
                .doubleToLongBits(other.variance))
            return false;
        return true;
    }
}
