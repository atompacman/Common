package com.atompacman.toolkat.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextFileReader {

	//==================================== STATIC METHODS ========================================\\

	public static List<String> read(String filePath) throws IOException {
		return read(IO.buildFile(filePath));
	}
	
	public static List<String> read(File file) throws IOException {
		List<String> lines = new ArrayList<String>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(file));
			
			String line = in.readLine();
			while (line != null) {
				lines.add(line.trim());
				line = in.readLine();
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return lines;
	}
	
	public static String readAsSingleLine(String filePath) throws IOException {
		return readAsSingleLine(IO.buildFile(filePath));
	}
	
	public static String readAsSingleLine(File file) throws IOException {
		StringBuilder builder = new StringBuilder();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(file));
			
			String line = in.readLine();
			while (line != null) {
				builder.append(line.trim());
				line = in.readLine();
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return builder.toString();
	}
}
