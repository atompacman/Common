package com.atompacman.toolkat.collections;

import java.util.Map;

public interface BiDoubleMap<A,B,C> extends DoubleMap<A,B,C> {

    //--------------------------------------- GETTERS --------------------------------------------\\
    
    public Map<A,C> getAlternativeSubMap(B b);
}
