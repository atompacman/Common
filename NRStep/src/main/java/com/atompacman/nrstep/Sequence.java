package com.atompacman.nrstep;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.atompacman.toolkat.json.JSONDeserializationException;
import com.atompacman.toolkat.json.JSONSerializable;

@SuppressWarnings("serial")
public class Sequence extends ArrayList<PatternElement<?>> implements JSONSerializable {

    //======================================= METHODS ============================================\\

    //---------------------------------- PUBLIC CONSTRUCTOR --------------------------------------\\

    public Sequence() {
        super();
    }

    public Sequence(List<? extends PatternElement<?>> elements) {
        super(elements);
    }


    //-------------------------------- JSON STATIC CONSTRUCTOR -----------------------------------\\

    static Sequence fromJSON(JSONArray jsonArray, Class<? extends PatternElement<?>> elementClass) {
        Sequence seq = new Sequence();

        try {
            Method elemFactory = null;
            try {
                elemFactory = elementClass.getMethod("fromJSON", Object.class);
            } catch (Exception e) {
                throw new IllegalArgumentException("Could not find a method \"public static " + 
                        elementClass.getSimpleName() + "\" fromJSON(Object)\"");
            }
            try {
                for (int i = 0; i < jsonArray.length(); ++i) {
                    seq.add((PatternElement<?>) elemFactory.invoke(null, jsonArray.get(i)));
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Could not successfully invoke method \"public "
                        + "static " + elementClass.getSimpleName() + "\" fromJSON(Object)\"");
            }
        } catch (Exception e) {
            JSONDeserializationException.causedBy(jsonArray, Sequence.class, e);
        }

        return seq;
    }


    //------------------------------------- SUBSEQUENCE ------------------------------------------\\

    public Sequence subSequence(int beg, int end) {
        return new Sequence(subList(beg, end));
    }


    //---------------------------------------- JSON ----------------------------------------------\\

    public Object toJSON() {
        List<Object> elements = new ArrayList<>();
        for (PatternElement<?> elem: this) {
            elements.add(elem.toJSON());
        }
        return elements;
    }
}
