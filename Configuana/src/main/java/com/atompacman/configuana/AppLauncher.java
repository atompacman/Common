package com.atompacman.configuana;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.atompacman.configuana.Lib.LibInfo;
import com.atompacman.toolkat.exception.Throw;
import com.atompacman.toolkat.io.IO;
import com.atompacman.toolkat.io.TextFileReader;

public class AppLauncher {

	//====================================== CONSTANTS ===========================================\\

	//// JSON configuration file constants

	// App config files
	private static final String APP_CONFIG_FILE_ROOT_OBJ_FIELD 			= "AppConfig";
	private static final String APP_CONFIG_FILE_NAME_FIELD 				= "name";
	private static final String APP_CONFIG_FILE_VERSION_FIELD 			= "version";
	private static final String APP_CONFIG_FILE_MAIN_LIB_FIELD			= "main lib";
	private static final String APP_CONFIG_FILE_OTHER_LIB_FIELD 		= "other lib";

	// Lib config files
	private static final String LIB_CONFIG_FILE_ROOT_OBJ_FIELD 			= "LibConfig";
	private static final String LIB_CONFIG_FILE_NAME_FIELD 				= "name";
	private static final String LIB_CONFIG_FILE_VERSION_FIELD 			= "version";
	private static final String LIB_CONFIG_FILE_CLASS_FIELD				= "class";
	private static final String LIB_CONFIG_FILE_BINARIES_FIELD			= "bin";
	private static final String LIB_CONFIG_FILE_SETTINGS_PROFILES_FIELD = "profiles";
	private static final String LIB_CONFIG_FILE_DEFAULT_PROFILE_FIELD  	= "default profile";

	// Other
	private static final String CONFIGUANA_HOME_ENV_VAR					= "Configuana";


	//==================================== STATIC METHODS ========================================\\

	//----------------------------------------- MAIN ---------------------------------------------\\

	public static void main(String[] args) {
		try {
			launchApp(args);
		} catch (AppLauncherException e) {
			StringBuilder sb = new StringBuilder();
			for (String arg : args) {
				sb.append(arg);
				sb.append(' ');
			}
			Throw.aRuntime(AppLauncherException.class, "Failed to launch application "
					+ "with arguments \" " + sb.toString() + "\"", e);
		}
	}

	@SuppressWarnings("unchecked")
	private static <A extends App, F extends Flag> void launchApp(String[] args) {
		if (args.length == 0) {
			Throw.aRuntime(AppLauncherException.class, "Expected a path to a Configuana "
					+ "application configuration JSON file as first argument");
		}
		A app = null;

		try {
			app = (A) createApp(args[0]);
		} catch (Exception e) {
			Throw.aRuntime(AppLauncherException.class, "Failed to create the application"
					+ " described by the configuration file \"" + args[0] + "\"", e);
		}

		if (args.length == 1) {
			Throw.aRuntime(AppLauncherException.class, "Expected a command as second argument");
		}

		Cmd<A,F> cmd = (Cmd<A,F>) parseCmd(args[1], app.getCmdClasses());
		Class<F> flagClass = flagClassOf((Class<? extends Cmd<?,F>>)cmd.getClass(), app.getClass());
		CmdArgs<F> cmdArgs = parseCmdArgs(args, cmd, flagClass);

		cmd.execute(app, cmdArgs);

		app.shutdownApp();
	}

	public static App createApp(String appConfigFilePath) {
		setConfiguanaEnvVar();

		File appConfigFile = null;
		
		try {
			appConfigFile = IO.getFile(appConfigFilePath);
		} catch (FileNotFoundException e) {
			Throw.aRuntime(AppLauncherException.class, "Could not find a Configuana"
					+ "application configuration JSON file at \"" + appConfigFilePath + "\"");
		}
		
		String appName 					= null;
		String appVersion 				= null;
		String mainLibConfigFile 		= null;
		Set<String> otherLibConfigFiles = null;

		try {
			String jsonFileContent = TextFileReader.readAsSingleLine(appConfigFile);

			JSONObject root = new JSONObject(jsonFileContent);
			root = root.getJSONObject(APP_CONFIG_FILE_ROOT_OBJ_FIELD);

			appName = root.getString(APP_CONFIG_FILE_NAME_FIELD);
			appVersion = root.getString(APP_CONFIG_FILE_VERSION_FIELD);
			mainLibConfigFile = root.getString(APP_CONFIG_FILE_MAIN_LIB_FIELD);

			JSONArray libConfigPaths = root.getJSONArray(APP_CONFIG_FILE_OTHER_LIB_FIELD);
			otherLibConfigFiles = new LinkedHashSet<>(jSONArrayToStringList(libConfigPaths));
			otherLibConfigFiles.remove(mainLibConfigFile);
		} catch (Exception e) {
			Throw.aRuntime(AppLauncherException.class, "Failed to parse "
					+ "JSON application configuration file", e);
		}

		return createApp(appName, appVersion, mainLibConfigFile, 
				appConfigFilePath, otherLibConfigFiles);
	}

	private static void setConfiguanaEnvVar() {
		try {
			String value = System.getenv(CONFIGUANA_HOME_ENV_VAR);
			if (value == null) {
				Throw.aRuntime(AppLauncherException.class, "Environnement variable "
						+ "\"" + CONFIGUANA_HOME_ENV_VAR + "\" is not set.");
			}
			File dir = IO.getFile(value);
			if (!dir.isDirectory()) {
				Throw.aRuntime(AppLauncherException.class, "\"" + 
						dir.getCanonicalPath() + "\" is not a directory");
			}
			System.setProperty("user.dir", value);
		} catch (Exception e) {
			Throw.aRuntime(AppLauncherException.class, "Could not "
					+ "set Configuana working directory", e);
		}
	}

	@SuppressWarnings("unchecked")
	private static <A extends App> A createApp(String appName, 
			String appVersion, 
			String mainLibConfigFile, 
			String appConfigFilePath,
			Set<String> otherLibConfigFiles) {

		List<LibInfo> libsInfo = new ArrayList<>();

		for (String libConfigFilePath : otherLibConfigFiles) {
			libsInfo.add(parseLibInfo(libConfigFilePath));
		}
		libsInfo.add(parseLibInfo(mainLibConfigFile));

		addURLToClassLoader(libsInfo);

		List<Lib> libs = new ArrayList<>();

		for (LibInfo info : libsInfo) {
			libs.add(createLib(info));
		}
		Lib mainLib = libs.get(libs.size() - 1);

		if (!App.class.isAssignableFrom(mainLib.getClass())) {
			Throw.aRuntime(AppLauncherException.class, "Main library \"" + 
					mainLib.getLibInfo().getName() + "\" must implement "
					+ "the " + App.class + " interface");
		}

		A app = (A) mainLib;

		app.setAppName(appName);
		app.setAppVersion(appVersion);
		app.setAppConfigFilePath(appConfigFilePath);

		for (Lib lib : libs) {
			lib.setParentApp(app);
			app.addLib(lib);
		}

		return app;
	}

	private static void addURLToClassLoader(List<LibInfo> libsInfo) {
		URLClassLoader classLoader;
		try {
			classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
		} catch (Exception e) {
			throw new RuntimeException("Classloader must be a URLClassLoader.");
		}

		URL[] urls = classLoader.getURLs();
		URL[] newUrls = new URL[urls.length + libsInfo.size()];
		System.arraycopy(urls, 0, newUrls, 0, urls.length);

		String libBinPath = null;

		try {
			for (int i = 0; i < libsInfo.size(); ++i) {
				libBinPath = libsInfo.get(i).getBinariesPath();
				newUrls[urls.length + i] = IO.getFile(libBinPath).toURI().toURL();
			}
		} catch (Exception e) {
			Throw.aRuntime(AppLauncherException.class, "Invalid URL \"" + libBinPath + "\"", e);
		}

		classLoader = new URLClassLoader(newUrls);
		Thread.currentThread().setContextClassLoader(classLoader);
	}

	private static List<String> jSONArrayToStringList(JSONArray jsonArray) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < jsonArray.length(); ++i) {
			list.add(jsonArray.getString(i));
		}
		return list;
	}

	private static LibInfo parseLibInfo(String libConfigFilePath) {
		File libConfigFile = null;

		try {
			libConfigFile = IO.getFile(libConfigFilePath);
		} catch (FileNotFoundException e) {
			Throw.aRuntime(AppLauncherException.class, "Could not found a Configuana "
					+ "library configuration JSON file at \"" + libConfigFilePath + "\"");
		}

		LibInfo info = new LibInfo();
		info.setConfigFilePath(libConfigFilePath);

		try {
			String jsonFileContent = TextFileReader.readAsSingleLine(libConfigFile);

			JSONObject root = new JSONObject(jsonFileContent);
			root = root.getJSONObject(LIB_CONFIG_FILE_ROOT_OBJ_FIELD);

			info.setName				(root.getString(LIB_CONFIG_FILE_NAME_FIELD));
			info.setVersion				(root.getString(LIB_CONFIG_FILE_VERSION_FIELD));
			info.setBinariesPath		(root.getString(LIB_CONFIG_FILE_BINARIES_FIELD));
			info.setDefaultProfileName	(root.getString(LIB_CONFIG_FILE_DEFAULT_PROFILE_FIELD));
			info.setLibClassName		(root.getString(LIB_CONFIG_FILE_CLASS_FIELD));

			JSONArray profileFilePaths = root.getJSONArray(LIB_CONFIG_FILE_SETTINGS_PROFILES_FIELD);
			info.setSettingsProfileNames(jSONArrayToStringList(profileFilePaths));
		} catch (Exception e) {
			Throw.aRuntime(AppLauncherException.class, "Failed to parse JSON library "
					+ "configuration file at \"" + libConfigFilePath + "\"", e);
		}

		return info;
	}

	@SuppressWarnings("unchecked")
	private static <L extends Lib> L createLib(LibInfo info) {
		Class<?> clazz = null;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try {
			try {
				clazz = classLoader.loadClass(info.getLibClassName());
			} catch (ClassNotFoundException e) {
				Throw.aRuntime(AppLauncherException.class, "Class not found");
			}
			if (!Lib.class.isAssignableFrom(clazz)) {
				Throw.aRuntime(AppLauncherException.class, "Class must implement "
						+ "the " + Lib.class.getSimpleName() + " interface");
			}
		} catch (Exception e) {
			Throw.aRuntime(AppLauncherException.class, "Invalid "+ Lib.class.getSimpleName() 
					+ " class \"" + info.getLibClassName() + "\"", e);
		}

		L lib = (L) createInstance(clazz);
		lib.setLibInfo(info);
		lib.init();

		return lib;
	}

	@SuppressWarnings("unchecked")
	private static <C> C createInstance(Class<C> clazz) {
		Constructor<C>[] constructors = (Constructor<C>[]) clazz.getConstructors();
		try {
			if (constructors.length != 1) {
				Throw.aRuntime(AppLauncherException.class, "Class "
						+ "must have only one constructor");
			}
			try {
				return clazz.newInstance();
			} catch (Exception e) {
				Throw.aRuntime(AppLauncherException.class, "Class "
						+ "constructor must have no argument",e);
			}
		} catch (AppLauncherException e) {
			Throw.aRuntime(AppLauncherException.class, "Failed to create an "
					+ "instance of " + clazz.getSimpleName() + "\"", e);
		}

		return null;
	}

	private static Cmd<?,?> parseCmd(String arg, List<Class<? extends Cmd<?,?>>> cmdClasses) {
		for (Class<? extends Cmd<?,?>> cmdClass : cmdClasses) {
			Cmd<?,?> cmd = null;
			try {
				cmd = cmdClass.newInstance();
			} catch (Exception e) {
				Throw.aRuntime(AppLauncherException.class, "Could not instantiate "
						+ "a Cmd of class \"" + cmdClass.getSimpleName() + "\"", e);
			}
			if (cmd.info().consoleName().equalsIgnoreCase(arg)) {
				return cmd;
			}
		}
		Throw.aRuntime(AppLauncherException.class, "No Cmd with console name \"" + 
				arg + "\" was defined in the Cmd classes specified by the ConsoleApp");
		return null;
	}

	@SuppressWarnings("unchecked")
	private static <F extends Flag, A extends App> Class<F> flagClassOf(
			Class<? extends Cmd<?,?>> cmdClass, Class<? extends App> trueAppClass) {

		Class<A> appClass = null;
		Class<F> flagClass = null;
		try {
			Type appInterface = cmdClass.getGenericInterfaces()[0];
			Type appType = ((ParameterizedType) appInterface).getActualTypeArguments()[0];
			appClass = (Class<A>) appType;
		} catch (Exception e) {
			Throw.aRuntime(AppLauncherException.class, "Could not extract the App "
					+ "class of Cmd class \"" + cmdClass.getSimpleName() + "\"", e);
		}
		try {
			Type flagInterface = cmdClass.getGenericInterfaces()[0];
			Type flagType = ((ParameterizedType) flagInterface).getActualTypeArguments()[1];
			flagClass = (Class<F>) flagType;
		} catch (Exception e) {
			Throw.aRuntime(AppLauncherException.class, "Could not extract the Flag "
					+ "class of Cmd class \"" + cmdClass.getSimpleName() + "\"", e);
		}
		if (appClass != trueAppClass) {
			Throw.aRuntime(AppLauncherException.class, "Generic app type of Cmd class \"" + 
					cmdClass.getSimpleName() + "\" must be the same than the calling app");
		}
		if (flagClass == Flag.class) {
			Throw.aRuntime(AppLauncherException.class, "Cmd class \"" + 
					cmdClass.getSimpleName() + "\" referenced type cannot be directly Flag");
		}
		if (!flagClass.isEnum()) {
			Throw.aRuntime(AppLauncherException.class, "Flag class \"" + 
					flagClass.getSimpleName() + "\" must be an enum");
		}
		return flagClass;
	}

	private static <F extends Flag> CmdArgs<F> parseCmdArgs(
			String[] args, Cmd<?,?> cmd, Class<F> flagClass) {

		CmdArgs<F> cmdArgs = new CmdArgs<F>(flagClass);
		List<String> mainArgs = parseMainArgs(args, cmd);
		cmdArgs.setMainArgs(mainArgs);

		for (int i = 2 + mainArgs.size(); i < args.length;) {			
			F flag = parseFlag(args[i], flagClass);
			List<String> flagArgs = new ArrayList<String>();
			int numArgs = flag.info().numArgs();

			if (numArgs < 0 && numArgs != Flag.UNLIMITED_ARGS) {
				Throw.aRuntime(AppLauncherException.class, "The num of "
						+ "args of Flag \"" + flag.info().consoleName() + "\" is negative");
			}

			for (int j = i + 1; j != args.length && args[j].charAt(0) != '-'; ++j) {
				flagArgs.add(args[j]);
			}

			if (flagArgs.size() != numArgs) {
				Throw.aRuntime(AppLauncherException.class, "Not the right "
						+ "number of arguments for flag \"" + flag.info().consoleName() + 
						"\": had " + flagArgs.size() + ", expected " + numArgs);
			}
			cmdArgs.setValues(flag, flagArgs);
			i += 1 + numArgs;
		}

		return cmdArgs;
	}

	private static List<String> parseMainArgs(String[] args, Cmd<?,?> cmd) {
		int numMainArgs = cmd.info().numMainArgs();

		if (numMainArgs < 0 && numMainArgs != Cmd.UNLIMITED_ARGS) {
			Throw.aRuntime(AppLauncherException.class, "The main num of args of "
					+ "Cmd \"" + cmd.info().consoleName() + "\" cannot be negative");
		}

		List<String> mainArgs = new ArrayList<String>();

		for (int i = 2; i < args.length; ++i) {
			if (args[i].charAt(0) == '-') {
				break;
			} else {
				mainArgs.add(args[i]);
			}
		}

		if (numMainArgs != Cmd.UNLIMITED_ARGS && mainArgs.size() != numMainArgs) {
			Throw.aRuntime(AppLauncherException.class, "Expected " + 
					numMainArgs + " main args for cmd \"" + cmd.info().consoleName() 
					+ "\" but got " + mainArgs.size() + " instead");
		}
		return mainArgs;
	}

	private static <F extends Flag> F parseFlag(String arg, Class<F> flagClass) {
		String consoleFlag = null;
		try {
			if (arg.length() == 1) {
				Throw.aRuntime(AppLauncherException.class, "Flag cannot be empty");
			}
			if (arg.length() == 2) {
				if (arg.charAt(1) == '-') {
					throw new AppLauncherException();
				}
				consoleFlag = arg.substring(1);
			} else {
				if (arg.charAt(1) != '-') {
					Throw.aRuntime(AppLauncherException.class, "Flag must "
							+ "start by \"--\" when it's longer than one character");
				}
				consoleFlag = arg.substring(2);
			}
		} catch (Exception e) {
			Throw.aRuntime(AppLauncherException.class, "Invalid flag \"" + 
					arg + "\" of class \"" + flagClass.getSimpleName() + "\"", e);
		}

		for (F flag : flagClass.getEnumConstants()) {
			if (flag.info().consoleName().equalsIgnoreCase(consoleFlag)) {
				return flag;
			}
		}
		Throw.aRuntime(AppLauncherException.class, "No flag with console name \"" + 
				consoleFlag + "\" defined for Flag class \"" + flagClass.getSimpleName() + "\"");
		
		return null;
	}
}