package com.atompacman.toolkat.test;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

import com.atompacman.toolkat.IOUtils;

public class AbstractTest {

    //
    //  ~  FIELDS  ~  //
    //

    @Rule
    public ExpectedException expectation = ExpectedException.none();


    //
    //  ~  EXPECT  ~  //
    //

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


    //
    //  ~  LOAD RESOURCE  ~  //
    //

    protected File loadResource(String path) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        if (url == null) {
            fail("Test resource not found \"" + path + "\"");
        }
        File file = null;
        try {
            file = IOUtils.getFile(url.getPath());
        } catch (FileNotFoundException e) {
            fail("Test resource not found \"" + path + "\"");
        }
        return file;
    }
}
