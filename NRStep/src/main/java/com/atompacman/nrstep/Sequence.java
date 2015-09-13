package com.atompacman.nrstep;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Sequence<T> extends ArrayList<T> {

    //======================================= METHODS ============================================\\

    //---------------------------------- PUBLIC CONSTRUCTOR --------------------------------------\\

    public Sequence() {
        
    }

    public Sequence(List<T> elements) {
        super(elements);
    }

    
    //------------------------------------- SUBSEQUENCE ------------------------------------------\\

    public Sequence<T> subSequence(int beg, int end) {
        return new Sequence<T>((List<T>) subList(beg, end));
    }
}
