package com.atompacman.toolkat.exception;

@SuppressWarnings("serial")
public class AbstractException extends Exception {

    //======================================= METHODS ============================================\\

    //---------------------------------- PUBLIC CONSTRUCTOR --------------------------------------\\

    public AbstractException() {
        super(Throw.msg, Throw.cause); 
    }
}
