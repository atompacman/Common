package com.atompacman.toolkat.collections;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public interface DoubleMap<A,B,C> {
    
    //======================================= METHODS ============================================\\

    //----------------------------------------- PUT ----------------------------------------------\\

    public C put(A a, B b, C c);
    
    
    //--------------------------------------- GETTERS --------------------------------------------\\

    public C get(A a, B b);
    
    public Map<B,C> getSubMap(A a);
    
    public List<Map<B,C>> getSubMaps();
    
    public Set<Entry<A, Map<B, C>>> entrySet();
    
    
    //---------------------------------------- CLEAR ---------------------------------------------\\

    public void clear();
}
