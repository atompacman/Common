package com.atompacman.toolkat.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class IO {

	public static File buildFile(String...pathElem) throws FileNotFoundException {
		if (pathElem == null) {
			throw new IllegalArgumentException("Null file path elements.");
		}
		if (pathElem.length == 0) {
			throw new IllegalArgumentException("File path elements cannot be null.");
		}
		File file = new File(pathElem[0]);

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
			throw new FileNotFoundException();
		}
		
		return file;
	}
}
