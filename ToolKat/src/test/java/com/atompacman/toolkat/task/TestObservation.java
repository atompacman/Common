package com.atompacman.toolkat.task;

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.Level;
import org.junit.Test;

public final class TestObservation {

    //
    //  ~  UNIT TESTS  ~  //
    //
    
    @Test
    public void UnitTest_Getters_ValidInstance_ValidResults() {
        final String msg       = "Regardez-moi cette meule!";
        final Level  verbLvl   = Level.DEBUG;
        
        Observation obs = new Observation(msg, 2, verbLvl, false, 0);
        
        assertEquals(msg, obs.getRawMessage());
        assertEquals(verbLvl, obs.getVerboseLevel());
    }
}
