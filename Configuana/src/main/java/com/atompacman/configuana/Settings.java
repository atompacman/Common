package com.atompacman.configuana;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.atompacman.configuana.param.CustomParam;
import com.atompacman.configuana.param.Param;
import com.atompacman.configuana.param.ParamWithDefault;
import com.atompacman.configuana.param.StrictParam;
import com.atompacman.toolkat.exception.Throw;

public class Settings implements ReadOnlySettings {

	//====================================== CONSTANTS ===========================================\\

	static final String SETTINGS_PROFILE_EXT = ".csp";



	//======================================= FIELDS =============================================\\

	private final String 									profileFilePath;
	private final Map<String, Map<? extends Param, Object>> paramValues;
	private final Lib										lib;


	//======================================= METHODS ============================================\\

	//---------------------------------- PACKAGE CONSTRUCTOR -------------------------------------\\

	Settings(String profileFilePath, 
			Set<Class<? extends Param>> paramClassRoots, 
			boolean existingFile,
			Lib lib) {

		this.profileFilePath = profileFilePath;
		this.paramValues 	 = new HashMap<String, Map<? extends Param, Object>>();
		this.lib 		 	 = lib;

		for (Class<? extends Param> paramClass : paramClassRoots) {
			addParameterSetClass(paramClass, false);
		}

		if (existingFile) {
			updateWithFile();
		} else {
			saveFile(false);
		}
	}

	@SuppressWarnings("unchecked")
	private void addParameterSetClass(Class<?> paramSetClass, boolean ignoreUnrelatedInnerClasses) {
		try {
			if (!Param.class.isAssignableFrom(paramSetClass)) {
				if (ignoreUnrelatedInnerClasses) {
					return;
				} else {
					Throw.aRuntime(ConfiguanaException.class, "Class does not "
							+ "implement an interface that extends ParameterSet");
				}
			}
			Class<?>[] interfaces = paramSetClass.getInterfaces();

			if (interfaces.length != 1) {
				Throw.aRuntime(ConfiguanaException.class, "Class must "
						+ "only implement one child interface of ParameterSet");
			}
			if (interfaces[0] == Param.class) {
				Throw.aRuntime(ConfiguanaException.class, "Class "
						+ "cannot implement ParameterSet directly; it "
						+ "must implements one of its child interfaces");
			} else if (interfaces[0] == ParamWithDefault.class) {
				Throw.aRuntime(ConfiguanaException.class, "Class "
						+ "cannot implement ParamSetWithDefault directly; it "
						+ "must implements one of its child interfaces");
			} 
			if (!paramSetClass.isEnum()) {
				Throw.aRuntime(ConfiguanaException.class, "Class must be an enum");
			}

			addParameterSet((Class<? extends Param>) paramSetClass);

			for (Class<?> innerClass : paramSetClass.getDeclaredClasses()) {
				addParameterSetClass(innerClass, true);
			}
		} catch (ConfiguanaException e) {
			Throw.aRuntime(ConfiguanaException.class, "Could not add "
					+ "parameter set class \"" + paramSetClass.getSimpleName() + 
					"\" to settings profile \"" + getProfileName() + "\"", e);
		}
	}

	@SuppressWarnings("unchecked")
	private <P extends Param> void addParameterSet(Class<P> paramSetClass) {
		String paramSetKey = paramSetKeyOf(paramSetClass);

		if (paramValues.containsKey(paramSetKey)) {
			Throw.aRuntime(ConfiguanaException.class, "Parameter "
					+ "set was already added to settings profile");
		}

		Map<P, Object> paramValueMap = (Map<P, Object>) createEmptyParamValueMap(paramSetClass);

		if (!StrictParam.class.isAssignableFrom(paramSetClass)) {
			setDefaultValuesToParamSet(paramValueMap, paramSetClass);
		}

		paramValues.put(paramSetKey, paramValueMap);
	}

	@SuppressWarnings("unchecked")
	private <P extends Enum<P>> Map<P, Object> createEmptyParamValueMap(Class<?> paramSetClass) {
		Class<P> paramSetClazz = (Class<P>) paramSetClass;
		Map<P, Object> paramValueMap = new EnumMap<P, Object>(paramSetClazz);

		for (P param : paramSetClazz.getEnumConstants()) {
			paramValueMap.put(param, null);
		}
		return paramValueMap;
	}

	@SuppressWarnings("unchecked")
	private <P extends ParamWithDefault> void setDefaultValuesToParamSet(
			Map<?, Object> paramValueMap, Class<?> paramSet) {

		Class<P> paramSetClass = (Class<P>) paramSet;
		Map<P, Object> paramValues = (Map<P, Object>) paramValueMap;

		for (P param : paramSetClass.getEnumConstants()) {
			paramValues.put(param, param.defaultValue());
		}
	}

	private void updateWithFile() {
		try {
			Properties parameters = new Properties();

			File profile = new File(profileFilePath);

			if (!profile.exists()) {
				String libConfigDir = new File(lib.getLibConfigFilePath()).getParent();
				profile = new File(libConfigDir + File.separator + profileFilePath);
				if (!profile.exists()) {
					Throw.aRuntime(AppLauncherException.class, "Could not found a Configuana"
							+ "library configuration JSON file at \"" + profileFilePath + "\"");
				}
			}

			parameters.load(new FileReader(profile));

			int nbValuesSet = 0;

			for (String paramSetKey : paramValues.keySet()) {
				nbValuesSet += updateParamSet(parameters, paramSetKey);
			}

			if (nbValuesSet != parameters.size()) {
				int nbUnlinkedParams = parameters.size() - nbValuesSet;
				Throw.aRuntime(ConfiguanaException.class, nbUnlinkedParams + " parameters"
						+ " in parameter file are not linked to valid settings");
			}
		} catch (Exception e) {
			Throw.aRuntime(ConfiguanaException.class, "Error parsing parameter "
					+ "file at \"" + getProfileFilePath() + "\"", e);
		}
	}

	@SuppressWarnings("unchecked")
	private <P extends Param> int updateParamSet(Properties parameters, String paramSetKey) {
		Map<P, Object> paramSetValues = (Map<P, Object>) getParamSetValuesMap(paramSetKey);

		int nbValuesSet = 0;

		for (P param : paramSetValues.keySet()) {
			String valueInFile = parameters.getProperty(paramSetKey + '.' + param);

			if (valueInFile == null) {
				try {
					if (param instanceof CustomParam) {
						CustomParam apsParam = (CustomParam) param;
						if (apsParam.isMandatory()) {
							Throw.aRuntime(ConfiguanaException.class, "Advance parameter "
									+ "set to mandatory (isMandatory == true)");
						}
					} else if (param instanceof StrictParam) {
						Throw.aRuntime(ConfiguanaException.class, "Parameter "
								+ "belongs to a StrictParameterSet");
					}
				} catch (ConfiguanaException e) {
					Throw.aRuntime(ConfiguanaException.class, "Mandatory parameter "
							+ "\"" + param + "\" was not found in parameter file", e);
				}
			} else {
				paramSetValues.put(param, valueInFile);
				++nbValuesSet;
			}
		}
		return nbValuesSet;
	}


	//----------------------------------------- SET ----------------------------------------------\\

	@SuppressWarnings("unchecked")
	public <P extends Param> void set(P param, Object newValue) {
		Class<P> paramSetClass = (Class<P>) param.getClass();

		try {
			if (StrictParam.class.isAssignableFrom(paramSetClass)) {
				Throw.aRuntime(ConfiguanaException.class, "Parameter belongs to a Strict"
						+ "ParameterSet, which does not allow the modification of its values");
			}
			if (CustomParam.class.isAssignableFrom(paramSetClass)) {
				CustomParam apsParam = (CustomParam) param;
				if (!apsParam.isModifiable()) {
					Throw.aRuntime(ConfiguanaException.class, "Parameter "
							+ "is not modifiable (isModifiable() == false)");
				}
			}
		} catch (ConfiguanaException e) {
			Throw.aRuntime(ConfiguanaException.class, "Could not set the "
					+ "value of parameter \"" + param + "\" of class \"" + 
					paramSetClass.getSimpleName() + "\" to \"" + newValue + "\"", e);
		}

		uncheckedSet(param, newValue);
	}

	@SuppressWarnings("unchecked")
	private <P extends Param> void uncheckedSet(P param, Object newValue) {
		String paramSetKey = paramSetKeyOf(param.getClass());
		Map<P, Object> categoryMap = (Map<P, Object>) getParamSetValuesMap(paramSetKey);
		categoryMap.put(param, newValue);
	}


	//----------------------------------------- GET ----------------------------------------------\\

	public <P extends Param> Object get(P param) {
		return getParamSetValuesMap(paramSetKeyOf(param.getClass())).get(param);
	}

	public <P extends Param> String getString(P param) {
		Object value = get(param);
		if (!(value instanceof String)) {
			value = param.toString();
			uncheckedSet(param, value);
		}
		return (String) value;
	}

	public <P extends Param> int getInt(P param) {
		Object value = get(param);
		if (!(value instanceof Integer)) {
			value = Integer.parseInt(value.toString());
			uncheckedSet(param, value);
		}
		return (int) value;
	}

	public <P extends Param> double getDouble(P param) {
		Object value = get(param);
		if (!(value instanceof Double)) {
			value = Double.parseDouble(value.toString());
			uncheckedSet(param, value);
		}
		return (double) value;
	}

	public <P extends Param> long getLong(P param) {
		Object value = get(param);
		if (!(value instanceof Long)) {
			value = Long.parseLong(value.toString());
			uncheckedSet(param, value);
		}
		return (long) value;
	}

	public <P extends Param> boolean getBoolean(P param) {
		Object value = get(param);
		if (!(value instanceof Boolean)) {
			value = Boolean.parseBoolean(value.toString());
			uncheckedSet(param, value);
		}
		return (boolean) value;
	}

	private Map<? extends Param, Object> getParamSetValuesMap(String paramSetKey) {
		Map<? extends Param, Object> paramValueMap = paramValues.get(paramSetKey);
		if (paramValueMap == null) {
			Throw.aRuntime(ConfiguanaException.class, "Parameter set class of key "
					+ "\"" + paramSetKey + "\" was not added to settings profile");
		}
		return paramValueMap;
	}


	//--------------------------------------- GETTERS --------------------------------------------\\

	public String getProfileName() {
		return new File(profileFilePath).getName().replace(SETTINGS_PROFILE_EXT, "");
	}

	public String getProfileFilePath() {
		return profileFilePath;
	}

	public Lib getLibConfig() {
		return lib;
	}

	public App getAppConfig() {
		return lib.getParentApp();
	}


	//-------------------------------------- SAVE FILE -------------------------------------------\\

	public void saveFile(boolean canOverwrite) {
		try {
			File profileFile = new File(getProfileFilePath());

			if (profileFile.exists() && !canOverwrite) {
				Throw.aRuntime(ConfiguanaException.class, "Overwritting is not allowed");
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter(profileFile));

			for (Map<? extends Param, Object> categValues : paramValues.values()) {
				for (Entry<? extends Param, Object> entry : categValues.entrySet()) {
					if (entry.getValue() == null) {
						continue;
					}
					writer.append(paramSetKeyOf(entry.getKey().getClass()));
					writer.append('.');
					writer.append(entry.getKey().toString());
					writer.append(" = ");
					writer.append(entry.getValue().toString());
					writer.newLine();
				}
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			Throw.aRuntime(ConfiguanaException.class, "Error saving settings "
					+ "profile to \"" + getProfileFilePath() + "\"", e);
		}
	}



	//==================================== STATIC METHODS ========================================\\

	private static <P extends Param> String paramSetKeyOf(Class<P> paramSetClass) {
		StringBuilder builder = new StringBuilder();
		builder.insert(0, paramSetClass.getSimpleName());
		Class<?> superSetClass = paramSetClass.getEnclosingClass();

		while (superSetClass != null && Param.class.isAssignableFrom(superSetClass)) {
			builder.insert(0, '.');
			builder.insert(0, superSetClass.getSimpleName());
			superSetClass = superSetClass.getEnclosingClass();
		}
		return builder.toString();
	}
}
