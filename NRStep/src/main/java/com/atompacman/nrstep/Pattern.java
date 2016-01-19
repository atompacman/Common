package com.atompacman.nrstep;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.atompacman.toolkat.exception.Throw;
import com.atompacman.toolkat.misc.JSONUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Pattern<T> {

    //======================================= FIELDS =============================================\\

    private Sequence<T>    seq;
    private Set<Integer>   startPos;
    private PatternTree<T> subPat;



    //======================================= METHODS ============================================\\

    //---------------------------------- PACKAGE CONSTRUCTOR -------------------------------------\\

    @JsonCreator
    Pattern(@JsonProperty("sequence") Sequence<T> seq) {
        if (seq.isEmpty()) {
            Throw.aRuntime(NRStepException.class, "Cannot create a pattern from an empty sequence");
        }
        this.seq      = seq;
        this.startPos = new LinkedHashSet<Integer>();
        this.subPat   = null;
    }


    //------------------------------------ ADD OCCURRENCE ----------------------------------------\\

    void addOccurrences(List<Integer> startPos) {
        for (Integer start : startPos) {
            if (!this.startPos.add(start)) {
                Throw.aRuntime(NRStepException.class, "The occurrence of "
                        + "the pattern \"" + seq.toString() + "\" at position "
                        + "\"" + startPos + "\" has already been counted.");
            }	
        }
    }


    //--------------------------------- SET SUB OCCURRENCES --------------------------------------\\

    @JsonSetter
    void setSubPatterns(PatternTree<T> subPat) {
        this.subPat = subPat;
    }


    //--------------------------------------- GETTERS --------------------------------------------\\

    @JsonGetter("sequence")
    public Sequence<T> getSequence() {
        return seq;
    }

    @JsonGetter("positions")
    public Set<Integer> getStartingPositions() {
        return startPos;
    }

    @JsonIgnore
    public PatternTree<T> getSubPatterns() {
        return subPat;
    }

    @JsonGetter("sub_patterns")
    private List<Pattern<T>> getSubPatternsJSON() {
        // Directly return the sub patterns list
        return subPat.getAllPatterns();
    }
    
    
    //---------------------------------------- STATE ---------------------------------------------\\

    public int numAppearances() {
        return startPos.size();
    }


    //--------------------------------------- EQUALS ---------------------------------------------\\

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((seq == null) ? 0 : seq.hashCode());
        result = prime * result
                + ((startPos == null) ? 0 : startPos.hashCode());
        result = prime * result + ((subPat == null) ? 0 : subPat.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pattern<?> other = (Pattern<?>) obj;
        if (seq == null) {
            if (other.seq != null)
                return false;
        } else if (!seq.equals(other.seq))
            return false;
        if (startPos == null) {
            if (other.startPos != null)
                return false;
        } else if (!startPos.equals(other.startPos))
            return false;
        if (subPat == null) {
            if (other.subPat != null)
                return false;
        } else if (!subPat.equals(other.subPat))
            return false;
        return true;
    }

    
    //-------------------------------------- TO STRING -------------------------------------------\\

    public String toString() {
        return JSONUtils.toRobustJSONString(this);
    }
}
