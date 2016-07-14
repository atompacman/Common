package com.fxguild.common.pattern;

import org.junit.Test;

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
        TestHelper.createPatternFrom(tree);
    }
}
