package com.atompacman.toolkat.time;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.atompacman.toolkat.test.AbstractTest;

public class TestStopWatch extends AbstractTest {

    //====================================== CONSTANTS ===========================================\\

    private static final double TIME_DELTA_NS = 50000;



    //================================ INVALID USAGE DETECTION ===================================\\

    @Test
    public void getStartTimeBeforeWatchWasStarted() {
        expect("Could not get start time: Watch was not started");
        new StopWatch().getStartTime();
    }

    @Test
    public void getStopTimeBeforeWatchWasStarted() {
        expect("Could not get stop time: Watch was not started");
        new StopWatch().getStopTime();
    }

    @Test
    public void getStopTimeBeforeWatchWasStopped1() {
        expect("Could not get stop time: Watch was not stopped");
        StopWatch sw = new StopWatch();
        sw.click();
        sw.getStopTime();
    }

    @Test
    public void getStopTimeBeforeWatchWasStopped2() {
        expect("Could not get stop time: Watch was not stopped");
        StopWatch sw = new StopWatch();
        sw.click();
        sw.click();
        sw.click();
        sw.getStopTime();
    }

    @Test
    public void getElapsedTimeBeforeWatchWasStarted() {
        expect("Could not get elapsed time: Watch was not started");
        new StopWatch().getElapsedTime();
    }

    @Test
    public void getElapsedTimeBeforeWatchWasStopped1() {
        expect("Could not get elapsed time: Watch was not stopped");
        StopWatch sw = new StopWatch();
        sw.click();
        sw.getElapsedTime();
    }

    @Test
    public void getElapseTimeBeforeWatchWasStopped2() {
        expect("Could not get elapsed time: Watch was not stopped");
        StopWatch sw = new StopWatch();
        sw.click();
        sw.click();
        sw.click();
        sw.getElapsedTime();
    }

    //================================== FUNCTIONNAL TESTS =======================================\\

    @Test
    public void completeTest() {
        StopWatch sw = new StopWatch();
        long a, b, c, d, e, f;

        sw.click();
        a = System.nanoTime();
        assertEquals(a, sw.getStartTime(), TIME_DELTA_NS);

        sw.click();
        b = System.nanoTime();
        assertEquals(a,     sw.getStartTime(),   TIME_DELTA_NS);
        assertEquals(b,     sw.getStopTime(),    TIME_DELTA_NS);
        assertEquals(b - a, sw.getElapsedTime(), TIME_DELTA_NS);

        sw.click();
        c = System.nanoTime();
        assertEquals(a, sw.getStartTime(), TIME_DELTA_NS);

        sw.click();
        d = System.nanoTime();
        assertEquals(a,             sw.getStartTime(),   TIME_DELTA_NS);
        assertEquals(d,             sw.getStopTime(),    TIME_DELTA_NS);
        assertEquals(b - a + d - c, sw.getElapsedTime(), TIME_DELTA_NS);

        sw.click();
        e = System.nanoTime();
        assertEquals(a, sw.getStartTime(), TIME_DELTA_NS);

        sw.click();
        f = System.nanoTime();
        assertEquals(a,                     sw.getStartTime(),   TIME_DELTA_NS);
        assertEquals(f,                     sw.getStopTime(),    TIME_DELTA_NS);
        assertEquals(b - a + d - c + f - e, sw.getElapsedTime(), TIME_DELTA_NS);
    }
}
