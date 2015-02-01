package com.atompacman.configuana;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.atompacman.configuana.param.Param;
import com.atompacman.toolkat.exception.Throw;

public abstract class Lib {

	//======================================= FIELDS =============================================\\

	private String 					  	libName;
	private String					  	libVersion;
	private String					  	libConfigFilePath;
	private String			 			libBinariesPath;
	private String						defaultProfileName;
	private Map<String, Settings> 	  	settingsProfiles;
	private App					  		parentApp;

	

	//=================================== ABSTRACT METHODS =======================================\\

	public abstract List<Class<? extends Param>> getParamsClasses();

	
	
	//======================================= METHODS ============================================\\

	//---------------------------------- PUBLIC CONSTRUCTOR --------------------------------------\\

	public Lib() {
		this.settingsProfiles = new HashMap<String, Settings>();
	}
	

	//----------------------------------------- INIT ---------------------------------------------\\

	public abstract void init();
	
	
	//--------------------------------------- SETTERS --------------------------------------------\\

	void setLibName(String name) {
		this.libName = name;
	}

	void setLibVersion(String version) {
		this.libVersion = version;
	}

	void setLibConfigFilePath(String configFilePath) {
		this.libConfigFilePath = configFilePath;
	}

	void setLibBinariesPath(String appBinariesPath) {
		this.libBinariesPath = appBinariesPath;
	}
	
	void setDefaultProfileName(String defaultProfileName) {
		this.defaultProfileName = defaultProfileName;
	}
	
	void addSettingsProfile(String profileFilePath, boolean existingFile) {
		if (settingsProfiles.containsKey(profileFilePath)) {
			Throw.aRuntime(ConfiguanaException.class, "Settings profile at \"" + 
					profileFilePath + "\" was already added to current lib config");
		}

		Settings profile = null;

		try {
			profile = new Settings(profileFilePath, new HashSet<>(getParamsClasses()), 
					existingFile, this);
		} catch (Exception e) {
			String word = existingFile ? "add" : "create";
			Throw.aRuntime(ConfiguanaException.class, "Could not " + word + 
					" settings " + "profile file at \"" + profileFilePath + "\"", e);
		}

		settingsProfiles.put(profileFilePath, profile);
	}

	void setParentApp(App parentApp) {
		this.parentApp = parentApp;
	}

	
	//--------------------------------------- GETTERS --------------------------------------------\\

	public String getLibName() {
		return libName;
	}
	
	public String getLibVersion() {
		return libVersion;
	}

	public String getLibConfigFilePath() {
		return libConfigFilePath;
	}
	
	public String getLibBinariesPath() {
		return libBinariesPath;
	}
	
	public Settings getDefaultProfile() {
		return getSettingsProfile(defaultProfileName);
	}
	
	public Settings getSettingsProfile(String profileName) {
		Settings profile = settingsProfiles.get(profileName);
		if (profile == null) {
			Throw.aRuntime(ConfiguanaException.class, "There is not a setting profile "
					+ "named \"" + profileName + "\" in current application configuration");
		}
		return profile;
	}

	public Set<String> getLoadedSettingsProfileNames() {
		return new HashSet<>(settingsProfiles.keySet());
	}
	
	public ReadOnlySettings getReadOnlySettingsProfile(String profileName) {
		return getSettingsProfile(profileName);
	}

	public App getParentApp() {
		return parentApp;
	}


	//--------------------------------------- SHUTDOWN -------------------------------------------\\

	public abstract void shutdown();
}