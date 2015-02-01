package com.atompacman.toolkat.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextFileReader {

	//==================================== STATIC METHODS ========================================\\

	public static String readAsSingleLine(String filePath) throws IOException {
		return readAsSingleLine(new File(filePath));
	}

	public static List<String> read(String filePath) throws IOException {
		return read(new File(filePath));
	}
	
	public static String readAsSingleLine(File file) throws IOException {
		List<String> lines = read(file);
		StringBuilder builder = new StringBuilder();

		for (String line : lines) {
			builder.append(line);
			builder.append(' ');
		}
		
		return builder.toString();
	}

	public static List<String> read(File file) throws IOException {
		List<String> lines = new ArrayList<String>();
		BufferedReader in = new BufferedReader(new FileReader(file));	
		String line = in.readLine();

		while (line != null) {
			lines.add(line.trim());
			line = in.readLine();
		}

		in.close();

		return lines;
	}
}
