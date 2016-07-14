package com.fxguild.common.pattern;

import java.io.IOException;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public final class TestPatternDetector {

    //
    //  ~  DETECT  ~  //
    //

    @Test
    public void detect_MediumComplexity_CorrectReturn() throws IOException {
        ImmutableList<Character> seq = TestHelper.createTestSequenceFrom("ABCXYZ__ABC_XYZ_CXY");
        new PatternDetector<Character>().detect(seq);
    }
}
