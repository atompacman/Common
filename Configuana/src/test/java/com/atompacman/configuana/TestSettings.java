package com.atompacman.configuana;

import com.atompacman.toolkat.test.AbstractTest;

public class TestSettings extends AbstractTest {
//
//	//===================================== BEFORE CLASS =========================================\\
//
//	@BeforeClass
//	public static void prepareTestClass() {
//		detectTestDirectory(TestConsoleAppLauncher.TEST_DIR, TestConsoleAppLauncher.APP_PACKAGE);
//	}
//
//
//
//	//==================================== COMPLETE TEST =========================================\\
//
//	/**
//	 * Asserts that no exception is thrown when performing a variety of valid operations on a 
//	 * settings profile.
//	 */
//	@Test
//	public void completeTest() {
//		Settings csp = new Settings("completeTest", testDir);
//		csp.addParameterSetClass(A_S_L$L_L_A$$A$.class);
//		csp.updateWithFile();
//		
//		String expecProfilePath = testDir + "completeTest" + Settings.SETTINGS_PROFILE_EXT;
//
//		assertEquals(true, 			 	csp.getBoolean(A_S_L$L_L_A$$A$.S_L$.L.ITEM_2));
//		assertEquals(6.4, 			 	csp.getDouble(A_S_L$L_L_A$$A$.L_L_A$$.L_A$.ITEM_1), 1e-8);
//		assertEquals(335, 			 	csp.getInt(A_S_L$L_L_A$$A$.L_L_A$$.ITEM_1));
//		assertEquals(345735687356l,  	csp.getLong(A_S_L$L_L_A$$A$.L_L_A$$.L_A$.A.ITEM_1));
//		assertEquals("kraft dinner", 	csp.getString(A_S_L$L_L_A$$A$.L_L_A$$.L_A$.A.ITEM_2));
//		assertEquals(expecProfilePath,	csp.getProfileFilePath());
//		assertEquals("completeTest", 	csp.getProfileName());
//	}
//	
//	
//	
//	//================================== FUNCTIONNAL TESTS =======================================\\
//
//	//----------------------------------- GET PROFILE PATH ---------------------------------------\\
//
//	/**
//	 * Asserts that the settings profile file path is correctly constructed.
//	 */
//	@Test
//	public void correctProfileFilePath() {
//		Settings settings = new Settings("salut", "goglu" + File.separator + testDir);
//		String expected = "goglu" + File.separator + testDir + "salut.csp";
//		assertEquals(expected, settings.getProfileFilePath());
//	}
//
//
//	//------------------------------------- SAVE TO FILE -----------------------------------------\\
//
//	/**
//	 * Asserts that no exception is thrown when calling the {@link Settings#saveToFile()}
//	 * on a valid settings profile.
//	 */
//	@Test
//	public void noErrorsWhenSaving() {
//		Settings settings = new Settings("noErrorsWhenSaving", testDir);
//		settings.addParameterSetClass(A_S_L$L_L_A$$A$.class);
//		settings.saveToFile();
//	}
//	
//	/**
//	 * Asserts the consistency between the {@link Settings#saveToFile()} and
//	 * {@link Settings#updateWithFile()} methods.
//	 */
//	@Test
//	public void loadSaveConsistency() {
//		Settings settings = new Settings("loadSaveConsistency", testDir);
//		settings.addParameterSetClass(L.class);
//		settings.set(L.ITEM_1, "wololo");
//		settings.set(L.ITEM_2, "ayoyoyo");
//		
//		settings.saveToFile();
//		
//		Settings settings2 = new Settings("loadSaveConsistency", testDir);
//		settings2.addParameterSetClass(L.class);
//		settings2.updateWithFile();
//		assertEquals("wololo", settings2.getString(L.ITEM_1));
//		assertEquals("ayoyoyo", settings2.getString(L.ITEM_2));
//	}
//	
//	
//	
//	//================================ INVALID DATA DETECTION ====================================\\
//
//	//----------------------------------- ADD PARAMETER SET --------------------------------------\\
//
//	/**
//	 * Asserts that exception messages triggered by the adding of erroneous parameter set classes 
//	 * to settings profiles are what they are expected to be. Those expectations are described in 
//	 * a standard property file that associate a parameter set class to an exception message that
//	 * should be triggered when trying to add it to an empty settings profile.
//	 * 
//	 * @throws IOException				Problem finding / reading the expected exception file
//	 * @throws ClassNotFoundException	One of the class mentioned in the exception exception file
//	 * 									is not an inner class of {@link TestParameterSets}.
//	 */
//	@Test
//	public void detectProblemsInParameterSetClasses() throws IOException, ClassNotFoundException {
//		String expectedExceptionFilePath = TestFileDetector.detectSingleFileForCurrentTest();
//		Properties expectedExceptionFile = new Properties();
//		expectedExceptionFile.load(new FileReader(expectedExceptionFilePath));
//
//		for (Entry<Object, Object> entry : expectedExceptionFile.entrySet()) {
//			boolean anExceptionWasTriggered = false;
//			try {
//				Settings settings = new Settings("", testDir);
//				String paramSetClass = TestParameterSets.class.getName() + "$" + entry.getKey();
//				settings.addParameterSetClass(Class.forName(paramSetClass));
//			} catch (ConfiguanaException e) {
//				assertEquals(entry.getValue(), e.getMessage());
//				anExceptionWasTriggered = true;
//			}
//			if (!anExceptionWasTriggered) {
//				fail("Expected exception message \"" + entry.getValue() + "\" "
//						+ "for parameter set class \"" + entry.getKey() + "\".");
//			}
//		}
//	}
//
//	/**
//	 * Asserts that an exception is thrown when adding two times the same parameter set class to a
//	 * settings profile.
//	 */
//	@Test
//	public void multipleAddingDetection() {
//		expect("Could not add parameter set class \"A\" to settings profile \"\": "
//				+ "Parameter set was already added to settings profile");
//
//		Settings settings = new Settings("", testDir);
//		settings.addParameterSetClass(A.class);
//		settings.addParameterSetClass(A.class);
//	}
//
//
//	//----------------------------------------- SET ----------------------------------------------\\
//
//	/**
//	 * Asserts that an exception is thrown when trying to set the value of a parameter that belongs
//	 * to a StrictParameterSet, which does not allow modifications.
//	 */
//	@Test
//	public void modifyingAParamFromAStrictParamSetDetection() {
//		expect("Could not set the value of parameter \"ITEM_2\" of class \"S_L$\" "
//				+ "to \"yolo\": Parameter belongs to a StrictParameterSet, which"
//				+ " does not allow the modification of its values");
//
//		Settings settings = new Settings("", testDir);
//		settings.addParameterSetClass(S_L$.class);
//
//		settings.set(S_L$.ITEM_2, "yolo");
//	}
//
//	/**
//	 * Asserts that an exception is thrown when trying to set the value of a parameter that belongs
//	 * to a AdvancedParameterSet for which the method {@link CustomParam#isModifiable()}
//	 * returns false.
//	 */
//	@Test
//	public void modifyingAnExplicitlyNotModifiableParamDetection() {
//		expect("Could not set the value of parameter \"ITEM_1\" of class \"A\" "
//				+ "to \"hey\": Parameter is not modifiable (isModifiable() == false)");
//
//		Settings settings = new Settings("", testDir);
//		settings.addParameterSetClass(A.class);
//
//		settings.set(A.ITEM_1, "hey");
//	}
//
//
//	//----------------------------------------- GET ----------------------------------------------\\
//
//	/**
//	 * Asserts that an exception is thrown when trying to get the value of a parameter that belongs
//	 * to a parameter set class that was not added the the settings profile.
//	 */
//	@Test
//	public void getParamFromUnaddedParamSetDetection() {
//		expect("Parameter set class of key \"A_S_L$L_L_A$$A$.L_L_A$$\" "
//				+ "was not added to settings profile");
//		Settings settings = new Settings("", testDir);
//
//		settings.get(L_L_A$$.ITEM_1);
//	}
//
//
//	//------------------------------- UPDATE PARAMS WITH FILE ------------------------------------\\
//
//	/**
//	 * Asserts that an exception is thrown when updating a settings profile with a csp file that
//	 * does not contain an entry for a parameter that belongs to a {@link CustomParam} that 
//	 * was marked as mandatory (the method {@link CustomParam#isMandatory()} returns true).
//	 */
//	@Test
//	public void missingMandatoryParameter() {
//		File testCSPFile = new File(TestFileDetector.detectSingleFileForCurrentTest());
//		String testCSPName = testCSPFile.getName().replace(Settings.SETTINGS_PROFILE_EXT, "");
//
//		expect("Error parsing parameter file at \"" + testDir + testCSPName + 
//				Settings.SETTINGS_PROFILE_EXT + "\": Mandatory parameter "
//				+ "\"ITEM_2\" was not found in parameter file: Advance parameter "
//				+ "set to mandatory (isMandatory == true).");
//
//		Settings settings = new Settings(testCSPName, testDir);
//
//		settings.addParameterSetClass(A.class);
//		settings.updateWithFile();
//	}
//
//	/**
//	 * Asserts that an exception is thrown when updating a settings profile with a csp file that
//	 * does not contain an entry for a parameter that belongs to a {@link StrictParam}.
//	 */
//	@Test
//	public void missingStrictParameter() {
//		File testCSPFile = new File(TestFileDetector.detectSingleFileForCurrentTest());
//		String testCSPName = testCSPFile.getName().replace(Settings.SETTINGS_PROFILE_EXT, "");
//
//		expect("Error parsing parameter file at \"" + testDir + testCSPName + 
//				Settings.SETTINGS_PROFILE_EXT + "\": Mandatory parameter \"ITEM_2\" was "
//				+ "not found in parameter file: Parameter belongs to a StrictParameterSet.");
//
//		Settings settings = new Settings(testCSPName, testDir);
//
//		settings.addParameterSetClass(S_L$.class);
//		settings.updateWithFile();
//	}
//
//	/**
//	 * Asserts that an exception is thrown when updating a settings profile with a csp file that
//	 * contains unlinked parameter entries.
//	 */
//	@Test
//	public void unlinkedParameter() {
//		File testCSPFile = new File(TestFileDetector.detectSingleFileForCurrentTest());
//		String testCSPName = testCSPFile.getName().replace(Settings.SETTINGS_PROFILE_EXT, "");
//
//		expect("Error parsing parameter file at \"" + testDir + testCSPName + 
//				Settings.SETTINGS_PROFILE_EXT + "\": 1 parameters"
//				+ " in parameter file are not linked to current settings.");
//
//		Settings settings = new Settings(testCSPName, testDir);
//
//		settings.addParameterSetClass(L_A$.class);
//		settings.updateWithFile();
//	}
}