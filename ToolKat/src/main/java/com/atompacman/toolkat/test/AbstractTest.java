package com.atompacman.toolkat.test;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

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

    protected File loadResource() {
        return loadResource(Thread.currentThread().getStackTrace()[2].getMethodName());
    }
    
    protected File loadResource(String resName) {
        String path = getClass().getCanonicalName().replace(".", "/") + "/" + resName;
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        if (url == null) {
            fail("Test resource not found \"" + path + "\"");
        }
        File file = new File(url.getFile());
        if (!file.exists()) {
            fail("Test resource not found \"" + path + "\"");
        }
        return file;
    }
}
