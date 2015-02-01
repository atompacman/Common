package com.atompacman.toolkat.exception;

@SuppressWarnings("serial")
public class AbstractRuntimeException extends RuntimeException {
	
	//======================================= METHODS ============================================\\

	//---------------------------------- PUBLIC CONSTRUCTOR --------------------------------------\\

	public AbstractRuntimeException() {
		super(Throw.msg, Throw.cause); 
	}
}
