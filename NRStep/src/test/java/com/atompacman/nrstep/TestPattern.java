package com.atompacman.nrstep;

import org.junit.Test;

import com.atompacman.toolkat.Log;

public final class TestPattern {

    //
    //  ~  TO STRING  ~  //
    //

    @Test
    public void toString_NormalState_NoThrow() {
        PatternTree<Character> tree = TestHelper.createPatternTreeFor("ayoyoyo");
        {
            tree.addPattern(TestHelper.createPatternTreeLeafFrom("yo", 1, 3, 5));
        }
        Pattern<Character> pattern = TestHelper.createPatternFrom(tree);

        Log.debug("\n" + pattern.toString());
    }
}
