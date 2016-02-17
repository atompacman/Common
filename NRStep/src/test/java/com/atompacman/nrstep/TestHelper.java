package com.atompacman.nrstep;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

final class TestHelper {

    //
    //  ~  INIT  ~  //
    //
    
    private TestHelper() { 
        
    }
    
    
    //
    //  ~  HELPERS  ~  //
    //

    static ImmutableList<Character> createTestSequenceFrom(String seq) {
        return ImmutableList.copyOf(ArrayUtils.toObject(seq.toCharArray()));
    }
    
    static PatternTree<Character> createPatternTreeFor(String seq) {
        return new PatternTree<>(createTestSequenceFrom(seq));
    }
    
    static Pattern<Character> createPatternTreeLeafFrom(String seq, Integer...positions) {
        ImmutableList<Character> sequence = createTestSequenceFrom(seq);
        return Pattern.of(sequence, Sets.newHashSet(positions), new PatternTree<>(sequence));
    }
    
    static Pattern<Character> createPatternFrom(PatternTree<Character> subTree,Integer...positions){
        return Pattern.of(subTree.getSequence(), Sets.newHashSet(positions), subTree);
    }
}
