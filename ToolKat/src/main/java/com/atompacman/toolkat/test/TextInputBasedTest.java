package com.atompacman.toolkat.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import com.atompacman.toolkat.IO;

public final class TextInputBasedTest {

    //===================================== INNER TYPES ==========================================\\

    @FunctionalInterface
    public interface TestMethodWithOutput { 
        public String launchTest(String testInput); 
    }

    @FunctionalInterface
    public interface TestMethodWithException { 
        public void launchTest(String testInput) throws Exception; 
    }



    //======================================= METHODS ============================================\\

    public static void launchTestsWithExpectedOutput(String testInputListFilePath,
                                                     TestMethodWithOutput method) {

        for (Entry<Object, Object> entry : loadTestListFile(testInputListFilePath).entrySet()) {
            assertEquals(entry.getValue(), method.launchTest((String) entry.getKey()));
        }
    }

    public static void launchTestsWithExpectedException(String testInputListFilePath,
                                                        TestMethodWithException method) {

        for (Entry<Object, Object> entry : loadTestListFile(testInputListFilePath).entrySet()) {
            boolean anExceptionWasTriggered = false;
            try {
                method.launchTest((String) entry.getKey());
            } catch (Exception e) {
                assertEquals(entry.getValue(), e.getMessage());
                anExceptionWasTriggered = true;
            }
            if (!anExceptionWasTriggered) {
                fail("Expected exception message \"" + entry.getValue() +
                        "\" for test input \"" + entry.getKey() + "\".");
            }
        }
    }

    private static Properties loadTestListFile(String testInputListFilePath) {
        Properties testInputList = new Properties();
        try {
            testInputList.load(new FileReader(IO.getResource(testInputListFilePath)));
        } catch (IOException e) {
            fail("Could not load test input list file at \"" + 
                    testInputListFilePath +"\": " + e.getMessage());
        }
        return testInputList;
    }
}
