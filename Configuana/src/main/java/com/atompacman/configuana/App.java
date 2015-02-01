package com.atompacman.configuana;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atompacman.toolkat.exception.Throw;

public abstract class App extends Lib {

	//======================================= FIELDS =============================================\\

	private String 			 appName;
	private String			 appVersion;
	private String			 appConfigFilePath;
	private Map<String, Lib> libraries;



	//=================================== ABSTRACT METHODS =======================================\\

	public abstract List<Class<? extends Cmd<?, ?>>> getCmdClasses();



	//======================================= METHODS ============================================\\

	//---------------------------------- PUBLIC CONSTRUCTOR --------------------------------------\\

	public App() {
		this.libraries = new HashMap<String, Lib>();
	}


	//--------------------------------------- SETTERS --------------------------------------------\\

	void setAppName(String appName) {
		this.appName = appName;
	}

	void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	void setAppConfigFilePath(String appConfigFilePath) {
		this.appConfigFilePath = appConfigFilePath;
	}

	void addLib(Lib lib) {
		if (libraries.containsKey(lib.getLibName())) {
			Throw.aRuntime(AppLauncherException.class, "Library \"" + lib.getLibName()
					+ "\" was already added to app \"" + getAppName());		
		}
		libraries.put(lib.getLibName(), lib);
	}


	//--------------------------------------- GETTERS --------------------------------------------\\

	public String getAppName() {
		return appName;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public String getAppConfigFilePath() {
		return appConfigFilePath;
	}

	public Map<String, Lib> getLibraries() {
		return libraries;
	}

	public Lib getLib(String libName) {
		Lib lib = libraries.get(libName);
		if (lib == null) {
			throw new IllegalArgumentException("No library named \"" + 
					libName + "\" is part of app \"" + getLibName());
		}
		return lib;
	}

	public Settings getSettingsProfile(String libName, String settingsProfileName) {
		return getLib(libName).getSettingsProfile(settingsProfileName);
	}


	//--------------------------------------- SHUTDOWN -------------------------------------------\\

	void shutdownApp() {
		shutdown();
		for (Lib lib : libraries.values()) {
			lib.shutdown();
		}
	}
}
