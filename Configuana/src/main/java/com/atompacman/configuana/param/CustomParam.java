package com.atompacman.configuana.param;

public interface CustomParam extends ParamWithDefault {
			
	boolean isMandatory();
	
	boolean isModifiable();
}
