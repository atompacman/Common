package com.atompacman.toolkat.test;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

public class AbstractTest {

	//==================================== STATIC FIELDS =========================================\\

	protected static String testDir;

	
	
	//======================================= FIELDS =============================================\\

    @Rule
    public ExpectedException expectation = ExpectedException.none();
    
    
    
	//==================================== STATIC METHODS ========================================\\

	//---------------------------------------- EXPECT --------------------------------------------\\

    protected void expect(Class<? extends Throwable> expectedException) {
    	expectation.expect(expectedException);
    }
   
    protected void expect(Class<? extends Throwable> expectedException, String exceptionMsg) {
    	expectation.expect(expectedException);
    	expectation.expectMessage(exceptionMsg);
    }
    
    protected void expect(String exceptionMsg) {
    	expectation.expectMessage(exceptionMsg);
    }
    
    
	//-------------------------------- DETECT TEST DIRECTORY -------------------------------------\\

	protected static void detectTestDirectory() {
		testDir = TestFileDetector.resolveTestDirectory(-1);
	}
	
	protected static void detectTestDirectory(String packagePathToRemove) {
		TestFileDetector.setPackagePathToRemove(packagePathToRemove);
		testDir = TestFileDetector.resolveTestDirectory(-1);
	}
	
	protected static void detectTestDirectory(String testDirRoot, String packagePathToRemove) {
		TestFileDetector.setPackagePathToRemove(packagePathToRemove);
		TestFileDetector.setTestDirectory(testDirRoot);
		testDir = TestFileDetector.resolveTestDirectory(-1);
	}
	
	protected static void detectTestDirectory(String testDirRoot, 
			String packagePathToRemove, int stackIndexModifier) {
		
		TestFileDetector.setPackagePathToRemove(packagePathToRemove);
		TestFileDetector.setTestDirectory(testDirRoot);
		testDir = TestFileDetector.resolveTestDirectory(stackIndexModifier - 1);
	}
}
