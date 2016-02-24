package com.atompacman.nrstep;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.Set;

import com.atompacman.toolkat.JSONUtils;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

@AutoValue
public abstract class Pattern<T> {
    
    //
    //  ~  FIELDS  ~  //
    //
    
    public abstract ImmutableList<T>      getSequence();
    
    public abstract ImmutableSet<Integer> getStartingPositions();
    
    @JsonIgnore
    public abstract PatternTree<T>        getSubPatterns();
    
    
    //
    //  ~  INIT  ~  //
    //
    
    static <T> Pattern<T> of(List<T>        sequence, 
                             Set<Integer>   startingPosition,
                             PatternTree<T> subPatterns) {
        
        checkNotNull(subPatterns, "subPatterns");
        checkArgument(!checkNotNull(sequence).isEmpty(), "Sequence must not be empty");
        
        for (int start : checkNotNull(startingPosition)) {
            if (start < 0) {
                throw new IllegalArgumentException("Starting positions cannot be negative");
            }
        }
        
        return new AutoValue_Pattern<T>(ImmutableList.copyOf(sequence), 
                                        ImmutableSet.copyOf(startingPosition), 
                                        subPatterns);
    }

    
    //
    //  ~  STATE  ~  //
    //

    public int numAppearances() {
        return getStartingPositions().size();
    }

    
    //
    //  ~  TO STRING  ~  //
    //

    /**
     * Use JSON serialization
     * Note: The class is not JSON serializable/deserializable.
     */
    public String toString() {
        return JSONUtils.toQuietPrettyJSONString(this);
    }
    
    /** 
     * Use by JSON serializer for toString() purpose only
     */
    @JsonGetter("subPatterns")
    private List<Pattern<T>> getSubPatternsJSON() {
        // Directly return the sub patterns list
        return getSubPatterns().getAllPatterns();
    }
}
