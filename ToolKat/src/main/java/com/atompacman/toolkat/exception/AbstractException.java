package com.atompacman.toolkat.exception;

@SuppressWarnings("serial")
public class AbstractException extends Exception {

    public AbstractException() {
        super(Throw.msg, Throw.cause); 
    }
}
