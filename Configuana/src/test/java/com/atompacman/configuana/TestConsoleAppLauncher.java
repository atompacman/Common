package com.atompacman.configuana;

import org.junit.BeforeClass;
import org.junit.Test;

import com.atompacman.toolkat.test.AbstractTest;
import com.atompacman.toolkat.test.TextInputForExpecMsgBasedTest;

public class TestConsoleAppLauncher extends AbstractTest {
		
	//====================================== CONSTANTS ===========================================\\

	public static final String APP_PACKAGE 	= "com.atompacman.configuana";
	public static final String TEST_DIR 	= "test";

	

	//===================================== BEFORE CLASS =========================================\\

	@BeforeClass
	public static void prepareTestClass() {
		detectTestDirectory(TEST_DIR, APP_PACKAGE);
	}


	
	//====================================== UNIT TESTS ==========================================\\

	@Test
	public void detectProblemsInInvalidCmdLines() {
		new TextInputForExpecMsgBasedTest() {
			public void launchTest(String testInput) {
				if (testInput.isEmpty()) {
					AppLauncher.main(new String[0]);
				} else {
					AppLauncher.main(testInput.replace('_', ' ').split(" "));
				}
			}
		}.launchTestList();
	}
}
