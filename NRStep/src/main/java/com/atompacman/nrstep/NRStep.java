package com.atompacman.nrstep;

import java.util.ArrayList;
import java.util.List;

import com.atompacman.atomlog.Log;
import com.atompacman.configuana.Lib;
import com.atompacman.configuana.param.Param;

public class NRStep extends Lib {
	
	public List<Class<? extends Param>> getParamsClasses() {
		return new ArrayList<>();
	}

	public void init() {
		if (Log.infos() && Log.title("NRStep"));
	}

	public void shutdown() {
		
	}
}
