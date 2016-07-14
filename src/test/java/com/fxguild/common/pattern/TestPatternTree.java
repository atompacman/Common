package com.fxguild.common.pattern;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class TestPatternTree {

    //
    //  ~  ADD PATTERN / CONTAINS  ~  //
    //

    @Test
    public void addPatternAndcontains_NormalState_IsContained() {
        PatternTree<Character> tree = TestHelper.createPatternTreeFor("trololo");
        Pattern<Character> subPattern = TestHelper.createPatternTreeLeafFrom("ol", 2, 4);
        tree.addPattern(subPattern);
        assertTrue(tree.contains(subPattern.getSequence()));
    }
    
    
    //
    //  ~  TO STRING  ~  //
    //
    
    @Test
    public void toString_NormalState_NoThrow() {
        PatternTree<Character> tree = TestHelper.createPatternTreeFor("ababacabbacababa");
        {
            PatternTree<Character> ababa = TestHelper.createPatternTreeFor("ababa");
            {
                ababa.addPattern(TestHelper.createPatternTreeLeafFrom("ab", 0, 2));
                ababa.addPattern(TestHelper.createPatternTreeLeafFrom("ba", 1, 3));
            }
            tree.addPattern(TestHelper.createPatternFrom(ababa, 0, 11));
        }{
            PatternTree<Character> bacab = TestHelper.createPatternTreeFor("bacab");
            tree.addPattern(TestHelper.createPatternFrom(bacab, 3, 8));
        }
    }
}
