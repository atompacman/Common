package com.atompacman.atomlog;

import com.atompacman.atomlog.Log.Verbose;
import com.atompacman.configuana.param.LaxParam;

public enum Parameters implements LaxParam {
	
	CONSOLE_VERBOSE 		(Verbose.EXTRA),
	WRITE_LOG_FILE  		(false),
	LOG_FILE_VERBOSE 		(Verbose.EXTRA),
	LOG_DIRECTORY			("log"),
	APPEND_DATE_TO_LOG_FILE (false);

	
	//======================================= FIELDS =============================================\\

	private Object defaultValue;
	
	
	
	//======================================= METHODS ============================================\\

	//---------------------------------- PRIVATE CONSTRUCTOR -------------------------------------\\

	private Parameters(Object defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	
	//--------------------------------------- GETTERS --------------------------------------------\\

	public Object defaultValue() {
		return defaultValue;
	}
}
