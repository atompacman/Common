package com.atompacman.toolkat.json;

import com.atompacman.toolkat.exception.AbstractRuntimeException;
import com.atompacman.toolkat.exception.Throw;

@SuppressWarnings("serial")
public class JSONDeserializationException extends AbstractRuntimeException {

	//==================================== STATIC METHODS ========================================\\

	public static void causedBy(Object jsonSource, Class<?> targetClass, Throwable e) {
		Throw.aRuntime(JSONDeserializationException.class, "Could not "
				+ "deserialize JSON source \"" + jsonSource.toString() + 
				"\" into a " + targetClass.getSimpleName() + " object", e);
	}
}
