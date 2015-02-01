package com.atompacman.toolkat.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Map.Entry;

public abstract class TextInputForExpecMsgBasedTest {

	//======================================= METHODS ============================================\\

	public void launchTestList() {
		String expectedExceptionFilePath = TestFileDetector.detectSingleFileForCurrentTest(-1);
		Properties expectedExceptionFile = new Properties();
		
		try {
			expectedExceptionFile.load(new FileReader(expectedExceptionFilePath));
		} catch (IOException e) {
			fail("Could not load expected exception file at \"" + 
					expectedExceptionFilePath + "\": " + e.getMessage());
		}

		for (Entry<Object, Object> entry : expectedExceptionFile.entrySet()) {
			boolean anExceptionWasTriggered = false;
			try {
				launchTest((String) entry.getKey());
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

	public abstract void launchTest(String testInput);
}
