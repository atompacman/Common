package com.atompacman.atomlog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.atompacman.configuana.Lib;
import com.atompacman.configuana.Settings;
import com.atompacman.configuana.param.Param;

public class Log extends Lib {

	//====================================== CONSTANTS ===========================================\\
		
	private static final int  	CLASSNAME_LENGTH 		= 38;
	private static final char 	CLASSNAME_FILLER_CHAR	= '=';

	private static final int  	TITLE_LINE_LENGTH 		= 97;
	private static final char 	TITLE_LINE_CHAR 		= '=';

	private static final String TIMESTAMP_FORMAT 		= "[HH:mm:ss:SSS]";

	private static final int 	INVOKER_STACK_DEPTH		= 3;


	
	//===================================== INNER TYPES ==========================================\\

	public enum Verbose { OFF, ERROR, VITAL, WARNG, INFOS, EXTRA }


	
	//==================================== STATIC FIELDS =========================================\\
	
	private static Verbose 			minimalVerbose;
	private static Verbose 			calledVerbose;

	private static List<Appender> 	appenders;
	
	private static boolean			initialized;

	
	
	//======================================= METHODS ============================================\\

	//-------------------------------------- INITIALIZE ------------------------------------------\\
	
	public void init() {
		try {
			if (initialized) {
				throw new RuntimeException("Already initialized.");
			}
			initialized = true;
			
			if (getLoadedSettingsProfileNames().size() == 0) {
				throw new RuntimeException("No settings profile loaded.");
			}

			Settings settings = getDefaultProfile();
					
			minimalVerbose = Verbose.valueOf(settings.getString(Parameters.CONSOLE_VERBOSE));
			calledVerbose = Verbose.OFF;
			appenders = new ArrayList<>();
			
			if (settings.getBoolean(Parameters.WRITE_LOG_FILE)) {
				String dir = settings.getString(Parameters.LOG_DIRECTORY);
				Verbose verbose = Verbose.valueOf(settings.getString(Parameters.LOG_FILE_VERBOSE));
				boolean appendDate = settings.getBoolean(Parameters.APPEND_DATE_TO_LOG_FILE);
				addAppender(dir, verbose, appendDate);
			}
			
			if (getLoadedSettingsProfileNames().size() > 1) {
				if (Log.warng() && Log.print("More than one settings "
						+ "profile loaded: Arbitrary selecting one."));
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot initialize Log: " + e.getMessage() + ".", e);
		}
	}
	
	
	//--------------------------------------- GETTERS --------------------------------------------\\

	public List<Class<? extends Param>> getParamsClasses() {
		List<Class<? extends Param>> paramClass = new ArrayList<>();
		paramClass.add(Parameters.class);
		return paramClass;
	}
	
	
	
	//==================================== STATIC METHODS ========================================\\
	
	//---------------------------------------- PRINT ---------------------------------------------\\

	public static boolean print(String message) {
		processMessage(message);
		return true;
	}
	
	public static boolean print(String format, Object... args) {
		processMessage(String.format(format, args));
		return true;
	}

	public static boolean title(String title) {
		processMessage(generateTitle(title, 0));
		return true;
	}

	public static boolean title(String title, int spacesBetweenDots) {
		processMessage(generateTitle(title, spacesBetweenDots));
		return true;
	}

	public static boolean line(int spacesBetweenDots) {
		processMessage(generateTitle("", spacesBetweenDots));
		return true;
	}

	private static void processMessage(String message) {
		checkIfInit();
		
		StringBuilder builder = new StringBuilder();
		builder.append(timestamp());
		builder.append(' ');
		builder.append(invokingClassName());
		builder.append(" {");
		builder.append(calledVerbose.name());
		builder.append("} : ");
		builder.append(message);
		
		message = builder.toString();
		
		System.out.println(message);
		appendMessage(message);
	}


	//-------------------------------------- APPENDERS -------------------------------------------\\

	private static void appendMessage(String message) {
		for (Appender appender : appenders) {
			appender.appendMessage(message, calledVerbose);
		}
	}

	public static void writeFinalLogs() {
		checkIfInit();
		
		for (Appender appender : appenders) {
			appender.writeFinalLog();
		}
	}

	public static void addAppender(String logFileDir, boolean appendDate) {
		addAppender(logFileDir, Verbose.EXTRA, appendDate);
	}

	public static void addAppender(String logFileDir, Verbose verbose, boolean appendDate) {
		checkIfInit();
		appenders.add(new Appender(logFileDir, verbose, appendDate));
	}

	public static void removeAppenders() {
		checkIfInit();
		appenders.clear();
	}
	

	//--------------------------------------- SETTERS --------------------------------------------\\

	public static void setVerbose(Verbose newVerbose) {
		checkIfInit();
		minimalVerbose = newVerbose;
	}

	
	//------------------------------------ CHECK VERBOSE -----------------------------------------\\

	public static boolean error() {
		calledVerbose = Verbose.ERROR;
		return minimalVerbose.ordinal() >= calledVerbose.ordinal();
	}

	public static boolean vital() {
		calledVerbose = Verbose.VITAL;
		return minimalVerbose.ordinal() >= calledVerbose.ordinal();
	}

	public static boolean warng() {
		calledVerbose = Verbose.WARNG;
		return minimalVerbose.ordinal() >= calledVerbose.ordinal();
	}

	public static boolean infos() {
		calledVerbose = Verbose.INFOS;
		return minimalVerbose.ordinal() >= calledVerbose.ordinal();
	}

	public static boolean extra() {
		calledVerbose = Verbose.EXTRA;
		return minimalVerbose.ordinal() >= calledVerbose.ordinal();
	}


	//------------------------------------ PACKAGE UTILS -----------------------------------------\\

	static String timestamp() {
		return timestamp(TIMESTAMP_FORMAT);
	}

	static String timestamp(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}

	
	//------------------------------------ PRIVATE UTILS -----------------------------------------\\

	private static String invokingClassName() {
		StackTraceElement invokerStrackTrace = new Throwable().getStackTrace()[INVOKER_STACK_DEPTH];
		String fullStackTrace = invokerStrackTrace.toString(); 
		String className = fullStackTrace.substring(
				fullStackTrace.indexOf('('), fullStackTrace.indexOf(')') + 1);
		
		StringBuilder builder = new StringBuilder();
		builder.append(className);
		builder.append(' ');

		for (int i = builder.length(); i < CLASSNAME_LENGTH; ++i) {
			builder.append(CLASSNAME_FILLER_CHAR);
		}

		return builder.toString();
	}

	private static String generateTitle(String title, int spacesBetweenDots) {
		StringBuilder line = new StringBuilder();

		line.append(TITLE_LINE_CHAR);

		while ((line.length() + spacesBetweenDots) < TITLE_LINE_LENGTH) {
			for (int i = 0; i < spacesBetweenDots; ++i) {
				line.append(' ');
			}
			line.append(TITLE_LINE_CHAR);
		}
		while (line.length() < TITLE_LINE_LENGTH - 1) {
			line.append(' ');
		}
		if (line.length() == TITLE_LINE_LENGTH - 1) {
			line.append(TITLE_LINE_CHAR);
		}

		if (title != null && title.length() != 0) {
			title = " " + title + " ";
		} else {
			title = "";
		}
		int titleLength = title.length();
		int titleStartPos = (TITLE_LINE_LENGTH - titleLength + 1) / 2;

		if (titleStartPos < 0) {
			line.replace(0, title.length() - 1, title.substring(1));
		} else {
			line.replace(titleStartPos, titleStartPos + titleLength, title);
		}

		return line.toString();
	}

	private static void checkIfInit() {
		if (!initialized) {
			throw new RuntimeException("Log cannot be used before its initialization.");
		}
	}




	//--------------------------------------- SHUTDOWN -------------------------------------------\\

	public void shutdown() {
		writeFinalLogs();
	}
}
