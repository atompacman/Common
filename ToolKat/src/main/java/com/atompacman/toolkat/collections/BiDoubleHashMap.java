package com.atompacman.toolkat.collections;

import java.util.Map;

public class BiDoubleHashMap<A,B,C> extends DoubleHashMap<A,B,C> implements BiDoubleMap<A,B,C> {

    //======================================= FIELDS =============================================\\

    private DoubleMap<B,A,C> altMap;

    
    
    //======================================= METHODS ============================================\\

    public BiDoubleHashMap() {
        this.altMap = new DoubleHashMap<B,A,C>();
    }
    
    
    //----------------------------------------- PUT ----------------------------------------------\\

    public C put(A a, B b, C c) {
        altMap.put(b, a, c);
        return super.put(a, b, c);
    }

    
    //--------------------------------------- GETTERS --------------------------------------------\\

    public Map<A, C> getAlternativeSubMap(B b) {
        return altMap.getSubMap(b);
    }
}
