package com.atompacman.toolkat.collections;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DoubleHashMap<A,B,C> implements DoubleMap<A,B,C> {

    //======================================= FIELDS =============================================\\

    private Map<A,Map<B,C>> map;
    
    
    
    //======================================= METHODS ============================================\\

    public DoubleHashMap() {
        this.map = new HashMap<A,Map<B,C>>();
    }
    
    
    //----------------------------------------- PUT ----------------------------------------------\\

    public C put(A a, B b, C c) {
        Map<B,C> submap = map.get(a);
        if (submap == null) {
            submap = new HashMap<B,C>();
            map.put(a, submap);
        }
        return submap.put(b, c);
    }

    
    //--------------------------------------- GETTERS --------------------------------------------\\

    public C get(A a, B b) {
        Map<B,C> submap = map.get(a);
        return submap == null ? null : submap.get(b);
    }

    public Map<B,C> getSubMap(A a) {
        return map.get(a);
    }

    public List<Map<B, C>> getSubMaps() {
        return new LinkedList<>(map.values());
    }

    public Set<Entry<A, Map<B, C>>> entrySet() {
        return map.entrySet();
    }

    
    //---------------------------------------- CLEAR ---------------------------------------------\\

    public void clear() {
        map.clear();
    }
}
