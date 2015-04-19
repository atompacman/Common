package com.atompacman.nrstep;

import java.util.ArrayList;
import java.util.List;

import com.atompacman.toolkat.json.JSONDeserializationException;
import com.atompacman.toolkat.json.JSONSerializable;

public class Letter implements PatternElement<Letter>, JSONSerializable {

    //======================================= FIELDS =============================================\\

    private char val;



    //======================================= METHODS ============================================\\

    //------------------------------- PUBLIC STATIC CONSTRUCTOR ----------------------------------\\

    public static Sequence valueOf(String seq) {
        List<Letter> letters = new ArrayList<Letter>();
        for (int i = 0; i < seq.length(); ++i) {
            letters.add(new Letter(seq.charAt(i)));
        }
        return new Sequence(letters);
    }


    //---------------------------------- PUBLIC CONSTRUCTOR --------------------------------------\\

    public Letter(char value) {
        this.val = Character.toLowerCase(value);
        if (val < 'a' || val > 'z') {
            throw new IllegalArgumentException("\"" + value + "\" is not a letter.");
        }
    }


    //-------------------------------- JSON STATIC CONSTRUCTOR -----------------------------------\\

    public static Letter fromJSON(Object jsonSource) {
        try {
            if (!(jsonSource instanceof String)) {
                throw new IllegalArgumentException("Source must be a String");
            }
            if (((String) jsonSource).length() != 1) {
                throw new IllegalArgumentException("Source string must be of length 1");
            }
        } catch (Exception e) {
            JSONDeserializationException.causedBy(jsonSource, Letter.class, e);
        }
        return new Letter(((String) jsonSource).charAt(0));
    }


    //--------------------------------------- EQUALS ---------------------------------------------\\

    public boolean isEqualTo(Letter other) {
        return val == other.val;
    }


    //---------------------------------------- JSON ----------------------------------------------\\

    public String toJSON() {
        return toString();
    }


    //------------------------------------- TO STRING --------------------------------------------\\

    public String toString() {
        return Character.toString(val);
    }
}