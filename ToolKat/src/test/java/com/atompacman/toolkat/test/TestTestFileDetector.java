package com.atompacman.toolkat.test;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.atompacman.toolkat.test.TestFileDetector;

public class TestTestFileDetector extends AbstractTest {

	//==================================== STATIC FIELDS =========================================\\

	private static final String TEST_DIR = "test\\test\\TestTestFileDetector";



	//===================================== BEFORE CLASS =========================================\\

	@BeforeClass
	public static void prepareTestClass() {
		TestFileDetector.setPackagePathToRemove("com.atompacman.toolkat");
		TestFileDetector.setTestDirectory("test");
	}



	//====================================== UNIT TESTS ==========================================\\

	@Test
	public void singleFile() {
		String path = TestFileDetector.detectSingleFileForCurrentTest();
		assertEquals(TEST_DIR + "\\singleFile.txt", path);
	}

	@Test
	public void fileDoesntExists() {
		expect("No test files with name beginning by \"fileDoesntExists\" " 
				+ "in directory \"" + TEST_DIR + "\".");
		TestFileDetector.detectSingleFileForCurrentTest();
	}

	@Test
	public void multipleFiles() {
		Map<String, String> testFilePath = TestFileDetector.detectAllFilesForCurrentTest();
		assertEquals(TEST_DIR + "\\multipleFiles-first.cpp", testFilePath.get("first"));
		assertEquals(TEST_DIR + "\\multipleFiles-second.txt", testFilePath.get("second"));
	}

	@Test
	public void detectIdenticalNames() {
		expect("Multiple test files with name \"detectIdenticalNames\" in \"" + TEST_DIR + "\".");
		TestFileDetector.detectAllFilesForCurrentTest();
	}

	@Test
	public void multipleDelimiters() {
		expect("Test file name must not have multiple \"-\" characters in its name.");
		TestFileDetector.detectAllFilesForCurrentTest();
	}

	@Test
	public void wrongDelimiter() {
		expect("No test files with name beginning by \"wrongDelimiter\""
				+ " in directory \"" + TEST_DIR + "\".");
		TestFileDetector.detectAllFilesForCurrentTest();
	}

	@Test
	public void moreThanOneFile() {
		expect("Multiple test files with name \"moreThanOneFile\" in \"" + TEST_DIR + "\".");
		TestFileDetector.detectSingleFileForCurrentTest();
	}
}
