package com.atompacman.nrstep;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.atompacman.configuana.Lib;
import com.atompacman.configuana.param.Param;
import com.atompacman.toolkat.misc.StringHelper;

public class NRStep extends Lib {
	
	//====================================== CONSTANTS ===========================================\\

	private static final Logger logger = LogManager.getLogger(NRStep.class);
	

	
	//======================================= METHODS ============================================\\

	public List<Class<? extends Param>> getParamsClasses() {
		return new ArrayList<>();
	}

	public void init() {
		logger.info(StringHelper.title("NRStep"));
	}

	public void shutdown() {
		
	}
}
