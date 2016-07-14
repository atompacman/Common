package com.fxguild.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.fxguild.common.test.AbstractTest;
import com.google.auto.value.AutoValue;

public class TestEnumCompositeObjectConstructor extends AbstractTest {
    
    //
    //  ~  INNER TYPES  ~  //
    //

    public enum NoteLetter { 
        C, D, E , F, G, A, B 
    }

    public enum Octave {
        
        ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE; 

        
        //
        //  ~  SERIALIZATION  ~  //
        //

        @Override
        public String toString() {
            return Integer.toString(ordinal());
        }
    }

    @AutoValue
    public static abstract class DummyToneGood {

        //
        //  ~  CONSTANTS  ~  //
        //
        
        private static final EnumCompositeObjectConstructor<DummyToneGood> ECOC = 
                                             EnumCompositeObjectConstructor.of(DummyToneGood.class);

        //
        //  ~  FIELDS  ~  //
        //
        
        public abstract NoteLetter getLetter();
        public abstract Octave     getOctave();

        
        //
        //  ~  INIT  ~  //
        //
        
        public static DummyToneGood of(String repres) {
            return ECOC.parse(repres);
        }

        public static DummyToneGood of(NoteLetter letter, Octave octave) {
            return new AutoValue_TestEnumCompositeObjectConstructor_DummyToneGood(letter, octave);
        }
    }

    @AutoValue
    public static abstract class DummyToneBad {

        //
        //  ~  CONSTANTS  ~  //
        //
        
        private static final EnumCompositeObjectConstructor<DummyToneBad> ECOC = 
                                              EnumCompositeObjectConstructor.of(DummyToneBad.class);

        //
        //  ~  FIELDS  ~  //
        //
        
        public abstract NoteLetter getLetter();
        public abstract Octave     getOctave();

        
        //
        //  ~  INIT  ~  //
        //
        
        public static DummyToneBad of(String repres) {
            return ECOC.parse(repres);
        }
    }


    //
    //  ~  TESTS  ~  //
    //

    @Test
    public void testIntegratedERC() {
        DummyToneGood a = DummyToneGood.of("A5");
        DummyToneGood b = DummyToneGood.of(NoteLetter.A, Octave.FIVE);
        assertEquals(a, b);
    }

    @Test
    public void missingInformationDetection() {
        expect("\"A\" is not a valid representation of a \"DummyToneGood\" object");
        DummyToneGood.of("A");
    }

    @Test
    public void detectLackOfEnumBasedConstruc() {
        expect(ExceptionInInitializerError.class);
        DummyToneBad.of("%");
    }
}
