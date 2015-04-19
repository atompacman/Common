package com.atompacman.configuana;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.atompacman.toolkat.exception.Throw;

public class Settings {

    //======================================= FIELDS =============================================\\

    private final String profileName;

    private final Map<String, Map<? extends StrictParam, Object>> params;



    //======================================= METHODS ============================================\\

    //---------------------------------- PACKAGE CONSTRUCTOR -------------------------------------\\

    Settings(String profileFile, Set<Class<? extends StrictParam>> rootParamClasses) {
        this.profileName = profileFile.substring(profileFile.indexOf('.') + 1,
                                                 profileFile.lastIndexOf('.'));
        
        this.params = new HashMap<>();
        
        // Add parameter classes to parameters
        for (Class<? extends StrictParam> paramClass : rootParamClasses) {
            addParameterSetClass(paramClass, false);
        }
        
        // Load properties from file
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            Properties parameters = new Properties();
            InputStream is = cl.getResourceAsStream(profileFile);
            parameters.load(is);
            int nbValuesSet = 0;

            for (String paramSetKey : params.keySet()) {
                nbValuesSet += updateParamSet(parameters, paramSetKey);
            }
            
            if (nbValuesSet != parameters.size()) {
                Throw.aRuntime(AppLauncherException.class, (parameters.size() - nbValuesSet) + 
                        " parameters in parameter file are not linked to valid settings");
            }
        } catch (Exception e) {
            Throw.aRuntime(AppLauncherException.class, "Error reading "
                    + "parameter file \"" + profileFile + "\"", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void addParameterSetClass(Class<?> paramSetClass, boolean ignoreUnrelatedInnerClasses) {
        try {
            if (!StrictParam.class.isAssignableFrom(paramSetClass)) {
                if (ignoreUnrelatedInnerClasses) {
                    return;
                } else {
                    Throw.aRuntime(AppLauncherException.class, "Class does not "
                            + "implement \"" + StrictParam.class.getSimpleName()
                            + "\" or \"" + Param.class.getSimpleName() + "\"");
                }
            }
            Class<?>[] interfaces = paramSetClass.getInterfaces();

            if (interfaces.length != 1) {
                Throw.aRuntime(AppLauncherException.class, "Class "
                        + "must implement only one interface");
            }
            if (!paramSetClass.isEnum()) {
                Throw.aRuntime(AppLauncherException.class, "Class must be an enum");
            }
            addParameterSet((Class<? extends StrictParam>) paramSetClass);
            
            for (Class<?> innerClass : paramSetClass.getDeclaredClasses()) {
                addParameterSetClass(innerClass, true);
            }
        } catch (AppLauncherException e) {
            Throw.aRuntime(AppLauncherException.class, "Could not add parameter "
                    + "class \"" + paramSetClass.getSimpleName() + "\"", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <P extends StrictParam> void addParameterSet(Class<P> paramSetClass) {
        String paramSetKey = paramSetKeyOf(paramSetClass);

        if (params.containsKey(paramSetKey)) {
            Throw.aRuntime(AppLauncherException.class, "Parameter "
                    + "set was already added to settings profile");
        }

        Map<P, Object> paramValueMap = (Map<P, Object>) createEmptyParamValueMap(paramSetClass);

        if (paramSetClass == Param.class) {
            setDefaultValuesToParamSet(paramValueMap, paramSetClass);
        }

        params.put(paramSetKey, paramValueMap);
    }

    @SuppressWarnings("unchecked")
    private <P extends Enum<P>> Map<P, Object> createEmptyParamValueMap(Class<?> paramSetClass) {
        Class<P> paramSetClazz = (Class<P>) paramSetClass;
        Map<P, Object> paramValueMap = new EnumMap<>(paramSetClazz);

        for (P param : paramSetClazz.getEnumConstants()) {
            paramValueMap.put(param, null);
        }
        return paramValueMap;
    }

    @SuppressWarnings("unchecked")
    private <P extends Param> void setDefaultValuesToParamSet(Map<?, Object> paramValueMap, 
                                                              Class<?>       paramClass) {

        Class<P> paramSetClass = (Class<P>) paramClass;
        Map<P, Object> paramValues = (Map<P, Object>) paramValueMap;
        for (P param : paramSetClass.getEnumConstants()) {
            paramValues.put(param, param.defaultValue());
        }
    }

    @SuppressWarnings("unchecked")
    private <P extends StrictParam> int updateParamSet(Properties parameters, String paramSetKey) {
        Map<P, Object> paramSetValues = (Map<P, Object>) getParamSetValuesMap(paramSetKey);
        Iterator<P> it = paramSetValues.keySet().iterator();
        boolean areParamMandatory = it.hasNext() ? it.next() instanceof StrictParam : false;
        int nbValuesSet = 0;

        for (P param : paramSetValues.keySet()) {
            String valueInFile = parameters.getProperty(paramSetKey + '.' + param);

            if (valueInFile == null && areParamMandatory) {
                Throw.aRuntime(AppLauncherException.class, "Mandatory parameter "
                        + "\"" + param + "\" was not found in parameter file");
            } else {
                paramSetValues.put(param, valueInFile);
                ++nbValuesSet;
            }
        }
        return nbValuesSet;
    }


    //----------------------------------------- SET ----------------------------------------------\\

    @SuppressWarnings("unchecked")
    private <P extends StrictParam> void set(P param, Object newValue) {
        String paramSetKey = paramSetKeyOf(param.getClass());
        Map<P, Object> categoryMap = (Map<P, Object>) getParamSetValuesMap(paramSetKey);
        categoryMap.put(param, newValue);
    }


    //----------------------------------------- GET ----------------------------------------------\\

    public <P extends StrictParam> Object get(P param) {
        return getParamSetValuesMap(paramSetKeyOf(param.getClass())).get(param);
    }

    public <P extends StrictParam> String getString(P param) {
        Object value = get(param);
        if (!(value instanceof String)) {
            value = param.toString();
            set(param, value);
        }
        return (String) value;
    }

    public <P extends StrictParam> int getInt(P param) {
        Object value = get(param);
        if (!(value instanceof Integer)) {
            value = Integer.parseInt(value.toString());
            set(param, value);
        }
        return (int) value;
    }

    public <P extends StrictParam> double getDouble(P param) {
        Object value = get(param);
        if (!(value instanceof Double)) {
            value = Double.parseDouble(value.toString());
            set(param, value);
        }
        return (double) value;
    }

    public <P extends StrictParam> long getLong(P param) {
        Object value = get(param);
        if (!(value instanceof Long)) {
            value = Long.parseLong(value.toString());
            set(param, value);
        }
        return (long) value;
    }

    public <P extends StrictParam> boolean getBoolean(P param) {
        Object value = get(param);
        if (!(value instanceof Boolean)) {
            value = Boolean.parseBoolean(value.toString());
            set(param, value);
        }
        return (boolean) value;
    }

    private Map<? extends StrictParam, Object> getParamSetValuesMap(String paramSetKey) {
        Map<? extends StrictParam, Object> paramValueMap = params.get(paramSetKey);
        if (paramValueMap == null) {
            Throw.aRuntime(AppLauncherException.class, "Parameter set class of key "
                    + "\"" + paramSetKey + "\" was not added to settings profile");
        }
        return paramValueMap;
    }


    //--------------------------------------- GETTERS --------------------------------------------\\

    public String getProfileName() {
        return profileName;
    }


    //--------------------------------------- HELPERS --------------------------------------------\\

    private static <P extends StrictParam> String paramSetKeyOf(Class<P> paramSetClass) {
        StringBuilder builder = new StringBuilder();
        builder.insert(0, paramSetClass.getSimpleName());
        Class<?> superSetClass = paramSetClass.getEnclosingClass();

        while (superSetClass != null && StrictParam.class.isAssignableFrom(superSetClass)) {
            builder.insert(0, '.');
            builder.insert(0, superSetClass.getSimpleName());
            superSetClass = superSetClass.getEnclosingClass();
        }
        return builder.toString();
    }
}
