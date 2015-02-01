package com.atompacman.configuana;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class CmdArgs<F extends Flag> {

	//======================================= FIELDS =============================================\\

	private List<String> 		 mainArgs;
	private Map<F, List<String>> flagValues;
	
	
	
	//======================================= METHODS ============================================\\

	//---------------------------------- PACKAGE CONSTRUCTOR -------------------------------------\\

	@SuppressWarnings("unchecked")
	<E extends Enum<E>> CmdArgs(Class<F> flagClass) {
		flagValues = (Map<F, List<String>>) new EnumMap<E, List<String>>((Class<E>) flagClass);
		for (F flag : flagClass.getEnumConstants()) {
			flagValues.put(flag, flag.info().defaultValues());
		}
	}
	
	
	//--------------------------------------- SETTERS --------------------------------------------\\

	void setMainArgs(List<String> mainArgs) {
		this.mainArgs = mainArgs;
	}
	
	void setValues(F flag, List<String> values) {
		flagValues.put(flag, values);
	}
	
	
	//--------------------------------------- GETTERS --------------------------------------------\\

	public List<String> getMainArgs() {
		return mainArgs;
	}
	
	public String getValue(F flag) {
		return getValues(flag).get(0);
	}
	
	public List<String> getValues(F flag) {
		List<String> values = flagValues.get(flag);
		if (values == null) {
			values = new ArrayList<String>();
			flagValues.put(flag, values);
		}
		if (values.isEmpty()) {
			values.add("");
		}
		return values;
	}

	public boolean hasFlag(F flag) {
		return flagValues.containsKey(flag);
	}
}
