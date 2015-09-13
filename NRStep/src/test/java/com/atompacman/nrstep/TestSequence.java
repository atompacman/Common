package com.atompacman.nrstep;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.atompacman.toolkat.misc.JSONUtils;

public class TestSequence {


    //================================== FUNCTIONNAL TESTS =======================================\\

    @Test
    @SuppressWarnings("unchecked")
    public void assertCorrectJSONSerialization() throws IOException {
        final String json = "[\"y\",\"o\"]";
        final Sequence<Character> seq = new Sequence<>(Arrays.asList('y', 'o'));

        assertEquals(json, JSONUtils.toJSONString(seq));
        assertEquals(seq,  JSONUtils.parseCollection(json,Sequence.class,Character.class));
    }
}
