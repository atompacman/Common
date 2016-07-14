package com.fxguild.common.test;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

import com.fxguild.common.IOUtils;

/**
 * Abstract class for unit test classes that provides shortcuts for exception expectation 
 * specification and test resource loading.  
 */
public abstract class AbstractTest {

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
        try {
            return IOUtils.getMavenResource(getClass(), resName);
        } catch (FileNotFoundException e) {
            fail("Test resource not found \"" + resName + "\"");
        }
        return null;
    }
}
