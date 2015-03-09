package com.atompacman.toolkat.test;

public class Assert {

	public static <T> T paramIsNotNull(T varValue, String varName) {
		if (varValue == null) {
			throw new IllegalArgumentException("Parameter \"" + varName + "\" cannot be null.");
		}
		return varValue;
	}
}
