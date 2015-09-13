package com.atompacman.toolkat.collections;

import java.util.Map;

public interface DoubleMap<A,B,C> {
    
    //======================================= METHODS ============================================\\

    //----------------------------------------- PUT ----------------------------------------------\\

    public C put(A a, B b, C c);
    
    
    //--------------------------------------- GETTERS --------------------------------------------\\

    public C get(A a, B b);
    
    public Map<B,C> getSubMap(A a);
}
