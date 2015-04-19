package com.atompacman.nrstep;

import static org.junit.Assert.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class TestPatternTree {

    //====================================== CONSTANTS ===========================================\\

    private static final String JSON_SERIALIZATION_EXEMPLE = "{\"SUB\":[{\"SUB\":[{\"SUB\":[{\"POS"
            + "\":[0,2],\"SEQ\":[\"a\",\"s\"]}],\"POS\":[0,5],\"SEQ\":[\"a\",\"s\",\"a\",\"s\",\"d"
            + "\"]}],\"POS\":[0,11],\"SEQ\":[\"a\",\"s\",\"a\",\"s\",\"d\",\"a\",\"s\",\"a\",\"s\""
            + ",\"d\"]}],\"SEQ\":[\"a\",\"s\",\"a\",\"s\",\"d\",\"a\",\"s\",\"a\",\"s\",\"d\",\"k"
            + "\",\"a\",\"s\",\"a\",\"s\",\"d\",\"a\",\"s\",\"a\",\"s\",\"d\"]}";



    //================================== FUNCTIONNAL TESTS =======================================\\

    @Test
    public void assertCorrectJSONSerialization() {
        Sequence seq = new Sequence(Letter.valueOf("asasdasasdkasasdasasd"));
        PatternTree og = new PatternDetector().detect(seq);
        assertEquals(JSON_SERIALIZATION_EXEMPLE, og.toJSON().toString());
    }

    @Test
    public void assertCorrectJSONDeserialization() throws JSONException {
        JSONObject obj = new JSONObject(JSON_SERIALIZATION_EXEMPLE);
        PatternTree og = PatternTree.fromJSON(obj, Letter.class);
        assertEquals(JSON_SERIALIZATION_EXEMPLE, og.toJSON().toString());
    }
}
