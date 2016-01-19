package com.atompacman.nrstep;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.atompacman.toolkat.misc.JSONUtils;

public class TestPatternDetector {

    //================================== FUNCTIONNAL TESTS =======================================\\

    @Test
    public void test() throws IOException {
        Sequence<Character> sequence = new Sequence<Character>(Arrays.asList(
                'A', 'B', 'A', 'B', 'C', 'A', 'C', 'A', 'A', 'B', 'A', 'B', 'C'));        
        PatternDetector<Character> detector = new PatternDetector<>();
        PatternTree<Character> patterns = detector.detect(sequence);
        System.out.println(JSONUtils.toPrettyJSONString(patterns));
    }
}
