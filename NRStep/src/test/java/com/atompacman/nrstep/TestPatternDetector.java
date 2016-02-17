package com.atompacman.nrstep;

import java.io.IOException;

import org.junit.Test;

import com.atompacman.toolkat.misc.Log;
import com.google.common.collect.ImmutableList;

public final class TestPatternDetector {

    //
    //  ~  DETECT  ~  //
    //

    @Test
    public void detect_MediumComplexity_CorrectReturn() throws IOException {
        ImmutableList<Character> seq = TestHelper.createTestSequenceFrom("ABCXYZ__ABC_XYZ_CXY");
        PatternTree<Character> patterns = new PatternDetector<Character>().detect(seq);
        Log.debug("\n" + patterns.toString());
    }
}
