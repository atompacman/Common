package com.atompacman.klusterz;

import java.io.File;

import com.atompacman.configuana.param.LaxParam;
import com.atompacman.configuana.param.StrictParam;

public enum Parameters implements StrictParam {;

	public static class Paths {
		public static final String TEST_DIRECTORY 		= "test" 	+ File.separator;
		public static final String RESULTS_DIRECTORY 	= "results" + File.separator; 
		public static final String LOG_FILES_DIRECTORY	= "log" 	+ File.separator; 
		public static final String APP_PACKAGE			= "com.atompacman.klusterz";
	}
	
	public static class CPC {
		public static final String 	DEFAULT_ALGORITHM 			= "K_MEANS"; 
		public static final String 	DEFAULT_INITIAL_MEANS		= "RANDOM";
		public static final int 	DEFAULT_IMG_WIDTH			= 600;
		public static final int 	DEFAULT_IMG_HEIGHT			= 600;
		public static final int 	DEFAULT_PT_SIZE_ON_IMG		= 5;
		public static final int 	DEFAULT_MEAN_PT_SIZE_ON_IMG	= 8;
		public static final double 	MEAN_COLOR_DARK_COEFF		= 0.7;
		public static final int		PT_BORDER_COLOR				= 20;
	}

	public enum Misc implements LaxParam {
		RANDOM_SEED (5215153513L);

		private Object defVal;
		private Misc(Object defVal)  { this.defVal = defVal; }
		public Object defaultValue() { return defVal; }
	}
}