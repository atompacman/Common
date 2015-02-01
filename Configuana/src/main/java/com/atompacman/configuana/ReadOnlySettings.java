package com.atompacman.configuana;

import com.atompacman.configuana.param.Param;

public interface ReadOnlySettings {
	
	//----------------------------------------- GET ----------------------------------------------\\

	<P extends Param> Object get(P param);

	<P extends Param> String getString(P param);

	<P extends Param> int getInt(P param);

	<P extends Param> double getDouble(P param);

	<P extends Param> long getLong(P param);

	<P extends Param> boolean getBoolean(P param);
}
