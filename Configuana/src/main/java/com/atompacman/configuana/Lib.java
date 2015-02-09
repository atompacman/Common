package com.atompacman.configuana;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.atompacman.configuana.param.Param;
import com.atompacman.toolkat.exception.Throw;
import com.atompacman.toolkat.io.IO;

public abstract class Lib {

	//===================================== INNER TYPES ==========================================\\

	public static class LibInfo {
		
		//===================================== FIELDS ===========================================\\

		private String 					  	name;
		private String					  	version;
		private String					  	configFilePath;
		private String			 			binariesPath;
		private String						defaultProfileName;
		private String						libClassName;
		private List<String>				settingsProfileNames;
		
		
		
		//===================================== METHODS ==========================================\\

		//------------------------------------- SETTERS ------------------------------------------\\

		void setName(String libName) {
			this.name = libName;
		}
		
		void setVersion(String libVersion) {
			this.version = libVersion;
		}
		
		void setConfigFilePath(String libConfigFilePath) {
			this.configFilePath = libConfigFilePath;
		}
		
		void setBinariesPath(String libBinariesPath) {
			this.binariesPath = libBinariesPath;
		}
		
		void setDefaultProfileName(String defaultProfileName) {
			this.defaultProfileName = defaultProfileName;
		}

		void setLibClassName(String libClassName) {
			this.libClassName = libClassName;
		}
		
		void setSettingsProfileNames(List<String> settingsProfileNames) {
			this.settingsProfileNames = settingsProfileNames;
		}
		
		
		//------------------------------------- GETTERS ------------------------------------------\\

		public String getName() {
			return name;
		}
		
		public String getVersion() {
			return version;
		}
		
		public String getConfigFilePath() {
			return configFilePath;
		}
		
		public String getBinariesPath() {
			return binariesPath;
		}
		
		public String getDefaultProfileName() {
			return defaultProfileName;
		}

		public String getLibClassName() {
			return libClassName;
		}
		
		public List<String> getSettingsProfileNames() {
			return settingsProfileNames;
		}
	}
	
	
	
	//======================================= FIELDS =============================================\\

	private LibInfo 					info;
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

	void setLibInfo(LibInfo info) {
		this.info = info;
		List<String> settingsProfilePaths = new ArrayList<>();
		for (String settingsProfileFilePath : info.settingsProfileNames) {
			try {
				String fullPath = IO.getFile(settingsProfileFilePath).getAbsolutePath();
				settingsProfilePaths.add(fullPath);
				addSettingsProfile(fullPath, true);
			} catch (Exception e) {
				Throw.aRuntime(AppLauncherException.class, "Failed to add settings profile \"" 
						+ settingsProfileFilePath + "\" to library \"" + info.name + "\"", e);
			}
		}
		info.setDefaultProfileName(info.defaultProfileName);
		info.setSettingsProfileNames(settingsProfilePaths);
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

	public LibInfo getLibInfo() {
		return info;
	}
	
	public Settings getDefaultProfile() {
		return getSettingsProfile(info.defaultProfileName);
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