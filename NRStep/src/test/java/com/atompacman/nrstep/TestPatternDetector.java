package com.atompacman.nrstep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.atompacman.toolkat.io.TextFileReader;
import com.atompacman.toolkat.test.AbstractTest;
import com.atompacman.toolkat.test.TestFileDetector;

public class TestPatternDetector extends AbstractTest {
	
	//===================================== BEFORE CLASS =========================================\\

	@BeforeClass
	public static void beforeClass() {
		detectTestDirectory("test", "com.atompacman.nrstep");
	}
	
	
	
	//================================== FUNCTIONNAL TESTS =======================================\\

	@Test
	public void emptySequence() {
		PatternDetector detector = new PatternDetector();
		PatternTree tree = detector.detect(Letter.valueOf(""));
		assertTrue(tree.getAllPatterns().isEmpty());
	}
	
	@Test
	public void test() {
		Sequence seq = Letter.valueOf("ababcabab");
		PatternDetector pd = new PatternDetector();
		PatternTree pt = pd.detect(seq);
		System.out.println(pt.toJSON());
	}
	
	@Test
	public void completeTest() throws IOException {
		List<String> lines = TextFileReader.read(TestFileDetector.detectSingleFileForCurrentTest());
		PatternDetector detector = new PatternDetector();
		for (String line : lines) {
			String[] parts = line.split("=");
			Sequence sequence = Letter.valueOf(parts[0].trim());
			assertEquals(parts[1].trim(), detector.detect(sequence).toJSON().toString());
		}
	}
}
