package com.atompacman.toolkat.test;

import org.junit.BeforeClass;
import org.junit.Test;

import com.atompacman.toolkat.test.AbstractTest;

public class TestAbstractTest extends AbstractTest {

	//===================================== BEFORE CLASS =========================================\\

	@BeforeClass
	public static void prepareTestClass() {
		detectTestDirectory("test", "com.atompacman.toolkat");
	}
	
	
	
	//====================================== UNIT TESTS ==========================================\\

	@Test
	public void exceptionMessageComparisonStaticMethod() {
		expect(ArrayIndexOutOfBoundsException.class, "-22");
		TestFileDetector.resolveTestDirectory(24);
	}
}
