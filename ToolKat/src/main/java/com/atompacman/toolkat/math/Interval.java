package com.atompacman.toolkat.math;

public class Interval<T> {

	//======================================= FIELDS =============================================\\

	private T beg;
	private T end;
	
	
	
	//======================================= METHODS ============================================\\

	//---------------------------------- PUBLIC CONSTRUCTOR --------------------------------------\\

	public Interval(T beg, T end) {
		this.beg = beg;
		this.end = end;
	}
	
	
	//--------------------------------------- GETTERS --------------------------------------------\\

	public T beg() {
		return beg;
	}
	
	public T end() {
		return end;
	}
}
