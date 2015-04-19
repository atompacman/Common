package com.atompacman.nrstep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.atompacman.toolkat.test.AbstractTest;

public class TestPatternDetector extends AbstractTest {

    //================================== FUNCTIONNAL TESTS =======================================\\

    @Test
    public void emptySequence() {
        PatternDetector detector = new PatternDetector();
        PatternTree tree = detector.detect(Letter.valueOf(""));
        assertTrue(tree.getAllPatterns().isEmpty());
    }

    @Test
    public void test() {
        new PatternDetector().detect(Letter.valueOf("ababcabab"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void completeTest() throws IOException {
        List<String> lines = FileUtils.readLines(loadResources("completeTest.txt"));
        PatternDetector detector = new PatternDetector();
        for (String line : lines) {
            String[] parts = line.split("=");
            Sequence sequence = Letter.valueOf(parts[0].trim());
            assertEquals(parts[1].trim(), detector.detect(sequence).toJSON().toString());
        }
    }
}
