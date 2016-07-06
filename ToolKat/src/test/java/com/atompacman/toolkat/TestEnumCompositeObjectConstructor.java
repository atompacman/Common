package com.atompacman.toolkat;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.atompacman.toolkat.EnumCompositeObjectConstructor;
import com.atompacman.toolkat.test.AbstractTest;

public class TestEnumCompositeObjectConstructor extends AbstractTest {
    
    //
    //  ~  INNER TYPES  ~  //
    //

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

        private static EnumCompositeObjectConstructor<DummyToneA> a = 
                EnumCompositeObjectConstructor.of(DummyToneA.class);

        private DummyToneA(NoteLetter letter, Octave octave) {
            this.letter = letter;
            this.octave = octave;
        }

        public static DummyToneA valueOf(String repres) {
            return a.parse(repres);
        }

        public static DummyToneA valueOf(NoteLetter letter, Octave octave) {
            return new DummyToneA(letter, octave);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((letter == null) ? 0 : letter.hashCode());
            result = prime * result + ((octave == null) ? 0 : octave.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            DummyToneA other = (DummyToneA) obj;
            if (letter != other.letter)
                return false;
            if (octave != other.octave)
                return false;
            return true;
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

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((letter == null) ? 0 : letter.hashCode());
            result = prime * result + ((octave == null) ? 0 : octave.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            DummyToneB other = (DummyToneB) obj;
            if (letter != other.letter)
                return false;
            if (octave != other.octave)
                return false;
            return true;
        }
    }

    public static class DummyToneC {
        public String letter;

        private static EnumCompositeObjectConstructor<DummyToneC> a = 
                EnumCompositeObjectConstructor.of(DummyToneC.class);


        private DummyToneC(String t) {
            this.letter = t;
        }

        public static DummyToneC valueOf(String repres) {
            return a.parse(repres);
        }
    }


    //
    //  ~  TESTS  ~  //
    //

    @Test
    public void testIntegratedERC() {
        DummyToneA a = DummyToneA.valueOf("A5");
        DummyToneA b = new DummyToneA(NoteLetter.A, Octave.FIVE);
        assertEquals(a, b);
    }

    @Test
    public void missingInformationDetection() {
        expect("\"A\" is not a valid representation of a \"DummyToneA\" object");
        DummyToneA.valueOf("A");
    }

    @Test
    public void testExternalERC() {
        EnumCompositeObjectConstructor<DummyToneB> constructor = 
                EnumCompositeObjectConstructor.of(DummyToneB.class);
        DummyToneB a = constructor.parse("A5");
        DummyToneB b = new DummyToneB(NoteLetter.A, Octave.FIVE);
        assertEquals(a, b);
    }

    @Test
    public void detectLackOfEnumBasedConstruc() {
        expect(ExceptionInInitializerError.class);
        DummyToneC.valueOf("%");
    }
}
