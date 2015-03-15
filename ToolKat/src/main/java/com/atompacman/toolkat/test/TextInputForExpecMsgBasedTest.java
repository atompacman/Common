package com.atompacman.toolkat.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Map.Entry;

public abstract class TextInputForExpecMsgBasedTest extends AbstractTest {

	//=================================== ABSTRACT METHODS =======================================\\

	public abstract void launchTest(String testInput);

	
	
	//======================================= METHODS ============================================\\

	public void launchTestList(String listPath) {
		Properties expectedExceptionFile = new Properties();
		
		try {
			expectedExceptionFile.load(new FileReader(loadResources(listPath)));
		} catch (IOException e) {
			fail("Could not load expected exception file at \""+ listPath +"\": " + e.getMessage());
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
}
