package com.atompacman.nrstep;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.atompacman.toolkat.misc.JSONUtils;

public class TestPattern {

    //================================== FUNCTIONNAL TESTS =======================================\\

    @Test
    public void assertCorrectJSONSerialization() throws IOException {
        final String testJSON =
                "{\"sequence\":[\"y\",\"o\"],\"subPatterns\":null,\"startingPositions\":[1,3]}";
        final Pattern<String> testPat = new Pattern<String>(
                new Sequence<String>(Arrays.asList("y","o")));
        testPat.addOccurrences(Arrays.asList(1,3));
                
        assertEquals(testPat, JSONUtils.parse(testJSON, Pattern.class));
    }
}
