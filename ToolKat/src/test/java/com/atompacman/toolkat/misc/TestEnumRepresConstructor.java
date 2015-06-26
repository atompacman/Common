package com.atompacman.toolkat.misc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.atompacman.toolkat.misc.EnumRepresConstruc;
import com.atompacman.toolkat.test.AbstractTest;

public class TestEnumRepresConstructor extends AbstractTest {

    //===================================== INNER TYPES ==========================================\\

    public enum NoteLetter { 
        C, D, E , F, G, A, B 
    }

    public enum Octave { 
        ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE; 

        public String toString() {
            return Integer.toString(ordinal());
        }
    }

    public static class DummyToneA {
        public NoteLetter letter;
        public Octave octave;

        private static EnumRepresConstruc<DummyToneA> a = 
                new EnumRepresConstruc<DummyToneA>(DummyToneA.class);

        private DummyToneA(NoteLetter letter, Octave octave) {
            this.letter = letter;
            this.octave = octave;
        }

        public static DummyToneA valueOf(String repres) {
            return a.newInstance(repres);
        }

        public static DummyToneA valueOf(NoteLetter letter, Octave octave) {
            return new DummyToneA(letter, octave);
        }

        public boolean equals(Object o) {
            return letter == ((DummyToneA)o).letter && octave == ((DummyToneA)o).octave;
        }
    }

    public static class DummyToneB {
        public NoteLetter letter;
        public Octave octave;


        private DummyToneB(NoteLetter letter, Octave octave) {
            this.letter = letter;
            this.octave = octave;
        }

        public static DummyToneB valueOf(NoteLetter letter, Octave octave) {
            return new DummyToneB(letter, octave);
        }

        public boolean equals(Object o) {
            return letter == ((DummyToneB)o).letter && octave == ((DummyToneB)o).octave;
        }
    }

    public static class DummyToneC {
        public String letter;

        private static EnumRepresConstruc<DummyToneC> a = 
                new EnumRepresConstruc<DummyToneC>(DummyToneC.class);


        private DummyToneC(String t) {
            this.letter = t;
        }

        public static DummyToneC valueOf(String repres) {
            return a.newInstance(repres);
        }
    }



    //====================================== UNIT TESTS ==========================================\\

    //------------------------- INTERNAL ENUM REPRESENTATION CONSTRUCTOR -------------------------\\

    @Test
    public void testIntegratedERC() {
        DummyToneA a = DummyToneA.valueOf("A5");
        DummyToneA b = new DummyToneA(NoteLetter.A, Octave.FIVE);
        assertEquals(a, b);
    }

    @Test
    public void missingInformationDetection() {
        expect("\"A\" is not a valid representation of a \"DummyToneA\" object.");
        DummyToneA.valueOf("A");
    }


    //------------------------- EXTERNAL ENUM REPRESENTATION CONSTRUCTOR -------------------------\\

    @Test
    public void testExternalERC() {
        EnumRepresConstruc<DummyToneB> constructor = 
                new EnumRepresConstruc<DummyToneB>(DummyToneB.class);
        DummyToneB a = constructor.newInstance("A5");
        DummyToneB b = new DummyToneB(NoteLetter.A, Octave.FIVE);
        assertEquals(a, b);
    }


    //--------------------------- LACK OF ENUM-BASED STATIC CONSTRUCTOR --------------------------\\

    @Test
    public void detectLackOfEnumBasedConstruc() {
        expect(ExceptionInInitializerError.class);
        DummyToneC.valueOf("%");
    }
}
