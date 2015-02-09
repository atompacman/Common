package com.atompacman.atomlog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import com.atompacman.atomlog.Log.Verbose;
import com.atompacman.toolkat.io.IO;

public class Appender {

	//====================================== CONSTANTS ===========================================\\

	private static final String FILES_TIMESTAMP_FORMAT = "YYYY-MM-dd__HH-mm-ss";


	
	//======================================= FIELDS =============================================\\

	private BufferedWriter 	writer;
	private Verbose 		minimalVerbose;
	private boolean 		closed;
	
	

	//======================================= METHODS ============================================\\

	//---------------------------------- PACKAGE CONSTRUCTOR -------------------------------------\\

	 Appender(String logFileDir, Verbose minimalVerbose, boolean appendDateToFileName) {
		try {
			File logDirectory = IO.buildFile(logFileDir);
			
			StringBuilder builder = new StringBuilder();
			builder.append(logDirectory);
			builder.append(File.separator);
			builder.append("log");
			
			if (appendDateToFileName) {
				builder.append('_');
				builder.append(Log.timestamp(FILES_TIMESTAMP_FORMAT));
			}
			builder.append(".txt");

			writer = new BufferedWriter(new FileWriter(builder.toString()));
		} catch (IOException e) {
			throw new RuntimeException("Could not create appender: ", e);
		}

		this.minimalVerbose = minimalVerbose;
		this.closed = false;
	}


	//---------------------------------------- APPEND --------------------------------------------\\

	void appendMessage(String message, Verbose msgVerbose) {
		if (closed) {
			if (Log.error() && Log.print("AN APPENDER CANNOT APPEND "
					+ "A MESSAGE AFTER BEING CLOSED."));
			return;
		}
		if (msgVerbose.ordinal() <= minimalVerbose.ordinal()) {
			try {
				writer.append(message);
				writer.newLine();
			} catch (IOException e) {
				throw new RuntimeException("Could not append message to log file: ", e);
			}
		}
	}


	//------------------------------------ WRITE FINAL LOG ---------------------------------------\\

	void writeFinalLog() {
		if (closed) {
			if (Log.error() && Log.print("AN APPENDER CANNOT WRITE "
					+ "ITS FINAL LOG AFTER BEING CLOSED."));
			return;
		}
		try {
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException("Could not write log file: ", e);
		}
		try {
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException("Could not close logger: ", e);
		}	
	}
}
