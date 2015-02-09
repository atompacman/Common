package com.atompacman.toolkat.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import com.atompacman.toolkat.exception.Throw;
import com.atompacman.toolkat.io.IO;

/**
 * <h1> Tool to find test file paths </h1>
 * <i> Used by test classes to automatically find the appropriate paths of files that are needed in 
 * a unit test method. </i><p>
 * 
 * Test files must be stored in a directory tree where the path to test files requested 
 * by a unit test method can be obtained as follow: <p> 
 * 
 * (1) Get the canonical class name containing the method that is calling this class. <br>
 * (2) Remove {@link #packagePathToRemove} from the beginning of class name. <br>
 * (3) Replace all "." by {@link File#separator}. <br>
 * (4) Insert {@link Parameters#testDirectory} at the beginning. <p>
 * 
 * We look inside the resolved directory for either: <p>
 * 
 * (a) The file whose name (without extension) is equal to the name of the calling method. <br>
 * (see: {@link #detectSingleFileForCurrentTest}) <br> <p>
 * (b) Files with names beginning by the name of the calling method directly followed by 
 * "{@value#SECTION_DELIM}"(which is used to distinguish between the multiple files than can be 
 * needed for a single test) and some tag. In this case, the path to every detected file is added to 
 * a map that links the substring after the "{@value#SECTION_DELIM}" in the file name to its 
 * complete file path. <br>
 * (see: {@link #detectAllFilesForCurrentTest})
 */
public class TestFileDetector {

	//====================================== CONSTANTS ===========================================\\

	private static final char 	SECTION_DELIM 					= '-';
	private static final int  	DFLT_CLIENT_CODE_STACK_INDEX;
	
	private static final String DEFAULT_TEST_DIRECTORY 			= "test" + File.separator;
	private static final String DEFAULT_PACKAGE_PATH_TO_REMOVE 	= "com.atompacman";


	
	//==================================== STATIC FIELDS =========================================\\

	private static String testDirectory;
	private static String packagePathToRemove;

	
	
	//==================================== STATIC METHODS ========================================\\

	//--------------------------------- STATIC INITIALIZATION ------------------------------------\\
	
	static {
		testDirectory = DEFAULT_TEST_DIRECTORY;
		packagePathToRemove = DEFAULT_PACKAGE_PATH_TO_REMOVE;
		
		int i = 0;
		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
			++i;
			if (ste.getClassName().equals(TestFileDetector.class.getName())) {
				break;
			}
		}
		DFLT_CLIENT_CODE_STACK_INDEX = i;
	}
	
	
	//----------------------------------- DETECT TEST FILES --------------------------------------\\

	public static String detectSingleFileForCurrentTest() {
		return detectSingleFileForCurrentTest(-1);
	}

	public static Map<String, String> detectAllFilesForCurrentTest() {
		return detectAllFilesForCurrentTest(-1);
	}
	
	public static String detectSingleFileForCurrentTest(int stackIndexModifier) {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement currSTE = stackTrace[DFLT_CLIENT_CODE_STACK_INDEX - stackIndexModifier];

		Map<String, String> testFilePath = detectFilesForCurrMethod(currSTE);

		if (testFilePath.size() > 1) {
			Throw.aRuntime(TestFileDetectorException.class, "More than one file with "
					+ "name beginning by " + currSTE.getMethodName() + "\" was found");
		}
		return testFilePath.values().iterator().next();
	}

	public static Map<String, String> detectAllFilesForCurrentTest(int stackIndexModifier) {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement currSTE = stackTrace[DFLT_CLIENT_CODE_STACK_INDEX - stackIndexModifier];
		return detectFilesForCurrMethod(currSTE);
	}

	public static String resolveTestDirectory() {
		return resolveTestDirectory(-1);
	}
	
	public static String resolveTestDirectory(int stackIndexModifier) {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement currSTE = stackTrace[DFLT_CLIENT_CODE_STACK_INDEX - stackIndexModifier];
		File dir = resolveDirectory(currSTE);
		return dir.getPath() + File.separator;
	}
	
	private static Map<String, String> detectFilesForCurrMethod(StackTraceElement ste) {
		String methodName = ste.getMethodName();
		File dir = resolveDirectory(ste);
		String pathPrefix = dir.getPath() + File.separatorChar;

		Map<String, String> testFilePaths = new HashMap<String, String>();

		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				continue;
			}
			String fileName = file.getName();
			int extensionIndex = fileName.indexOf('.');
			if (extensionIndex != -1) {
				fileName = fileName.substring(0, extensionIndex);
			}
			if (!fileName.startsWith(methodName)) {
				continue;
			}
			if (fileName.length() == methodName.length()) {
				if (testFilePaths.containsKey("")) {
					Throw.aRuntime(TestFileDetectorException.class, "Multiple test files "
							+ "with name \"" + fileName + "\" in \"" + dir	+ "\"");
				}
				testFilePaths.put("", pathPrefix + file.getName());
				continue;
			}
			if (fileName.charAt(methodName.length()) != SECTION_DELIM) {
				continue;
			}
			int delimIndex = fileName.indexOf(SECTION_DELIM);

			if (delimIndex != fileName.lastIndexOf(SECTION_DELIM)) {
				Throw.aRuntime(TestFileDetectorException.class, "Test file name must not have "
						+ "multiple \"" + SECTION_DELIM	+ "\" characters in its name");
			}
			String tagSection = fileName.substring(delimIndex + 1);

			if (testFilePaths.containsKey(tagSection)) {
				Throw.aRuntime(TestFileDetectorException.class, "Multiple test files "
						+ "with name \"" + fileName + "\" in \"" + dir	+ "\"");
			}
			testFilePaths.put(tagSection, pathPrefix + file.getName());
		}

		if (testFilePaths.size() == 0) {
			Throw.aRuntime(TestFileDetectorException.class, "No test files with name beginning by "
					+ "\"" + methodName + "\" in directory \"" + dir.getPath() + "\"");
		}

		return testFilePaths;
	}

	private static File resolveDirectory(StackTraceElement ste) {
		String packagePath = ste.getClassName();
		if (packagePath.indexOf(packagePathToRemove) != 0) {
			Throw.aRuntime(TestFileDetectorException.class, "Calling method is not from the "
					+ "\"" + packagePathToRemove + "\" package and therefore is not supported");
		}
		packagePath = packagePath.substring(packagePathToRemove.length() + 1);
		packagePath = packagePath.replace('.', File.separatorChar);

		String directory = testDirectory + File.separator + packagePath;

		File currDir = new File(directory);
		try {
			currDir = IO.getFile(directory);
		} catch (FileNotFoundException e) {
			Throw.aRuntime(TestFileDetectorException.class, "Test file "
					+ "directory \"" + currDir + "\" does not exist");
		}
		return currDir;
	}
	
	
	//---------------------------------------- SETTERS -------------------------------------------\\

	public static void setTestDirectory(String testDirectory) {
		TestFileDetector.testDirectory = testDirectory;
	}
	
	public static void setPackagePathToRemove(String packagePath) {
		TestFileDetector.packagePathToRemove = packagePath;
	}
}
