package com.atompacman.toolkat.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class IO {

	public static String getPath(String...pathElem) {
		File file = buildInitFile(pathElem);
		
		for (int i = 1; i < pathElem.length; ++i) {
			file = new File(file.getAbsolutePath(), pathElem[i]);
		}
		return file.getAbsolutePath();
	}
	
	public static File getFile(String...pathElem) throws FileNotFoundException {
		File file = buildInitFile(pathElem);

		try {
			for (int i = 1; i < pathElem.length; ++i) {
				file = new File(file.getCanonicalPath(), pathElem[i]);
			}
			file = file.getCanonicalFile();
			
			if (!file.exists()) {
				throw new FileNotFoundException("Cannot find file "
						+ "\"" + file.getCanonicalPath() + "\".");
			}
		} catch (IOException e) {
			StringBuilder sb = new StringBuilder();
			for (String elem : pathElem) {
				sb.append(elem).append(File.separatorChar);
			}
			throw new FileNotFoundException("Cannot find file \"" + sb.toString() + "\".");
		}
		return file;
	}
	
	private static File buildInitFile(String...pathElem) {
		if (pathElem == null) {
			throw new IllegalArgumentException("Null file path elements.");
		}
		if (pathElem.length == 0) {
			throw new IllegalArgumentException("File path elements cannot be null.");
		}
		return new File(pathElem[0]);
	}
}
