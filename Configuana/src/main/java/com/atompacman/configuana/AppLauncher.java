package com.atompacman.configuana;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.atompacman.toolkat.IO;
import com.atompacman.toolkat.exception.Throw;

public class AppLauncher {

    //====================================== CONSTANTS ===========================================\\

    // Configuana directory
    static final String CONFIGUANA_DIR_IN_JAR                  = "configuana/";

    // App file
    private static final String APP_FILE                       = "app";

    // Configuana info files
    private static final String ARTIFACT_NAME_PROPERTY         = "artifact.name";
    private static final String ARTIFACT_DESCRIPTION_PROPERTY  = "artifact.description";
    private static final String ARTIFACT_GROUP_ID_PROPERTY     = "artifact.groupId";
    private static final String ARTIFACT_ARTIFACT_ID_PROPERTY  = "artifact.artifactId";
    private static final String ARTIFACT_VERSION_PROPERTY      = "artifact.version";
    private static final String DEFAULT_SETTINGS_PROPERTY      = "configuana.default.settings";
    private static final String MAIN_CLASS_PROPERTY            = "configuana.main.class";
    private static final String LIBRARIES_PROPERTY             = "configuana.libraries";
    private static final String LIBRARY_SEPARATOR              = ",";
    private static final String INFO_FILE_EXTENSION            = ".info";



    //==================================== STATIC FIELDS =========================================\\

    private static final Map<String, Lib> loadedLibs = new HashMap<>();



    //==================================== STATIC METHODS ========================================\\

    //----------------------------------------- MAIN ---------------------------------------------\\

    public static void main(String[] args) {
        // Checking args
        if (args.length < 2) {
            Throw.aRuntime(AppLauncherException.class, "Expecting a jar archive "
                    + "as first argument and a command as second argument");
        }
        try {
            // Launch the app
            launchApp(args);
        } catch (AppLauncherException e) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; ++i) {
                sb.append(args[i]).append(' ');
            }
            Throw.aRuntime(AppLauncherException.class, "Failed to launch jar \"" + args[0] + 
                    "\" with arguments \"" + sb.substring(0, sb.length() - 1) + "\"", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <A extends App, F extends Flag> void launchApp(String[] args) {
        // Set a new URLClassLoader that has the jar in its classpath as current ClassLoader
        addJarToClassLoader(args[0]);
        
        // Create and initialize the App instance
        A app = createApp();
        
        // Parse the Command sent to the App
        Cmd<A,F> cmd = (Cmd<A,F>) parseCmd(args[1], app.getCmdClasses());
        
        // Get Flag class associated with that Command
        Class<F> flagClass = flagClassOf((Class<? extends Cmd<?,F>>)cmd.getClass(), app.getClass());
        
        // Parse input args considering this Command and these Flag
        CmdArgs<F> cmdArgs = parseCmdArgs(args, cmd, flagClass);
        
        // Actually EXECUTE the App
        cmd.execute(app, cmdArgs);
    }

    private static void addJarToClassLoader(String jarPath) {
        try {
            URL[] newUrl = new URL[]{ IO.getFile(jarPath).toURI().toURL() };
            ClassLoader currCL = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(new URLClassLoader(newUrl, currCL));
        } catch (Exception e) {
            Throw.aRuntime(AppLauncherException.class, "Could not add jar to classloader", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <A extends App> A createApp() {
        // Detect App file in jar under configuana directory
        String appFilePath = CONFIGUANA_DIR_IN_JAR + APP_FILE; 
        InputStream is = null;
        try {
            is = IO.getResourceasStream(appFilePath);
        } catch (FileNotFoundException e1) {
            Throw.aRuntime(AppLauncherException.class, "Could not find a "
                    + "configuana app file at \"" + appFilePath + "\"");
        }
        
        // Read the content of this App file (should be a single artifact ID)
        String appInfoFile = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            appInfoFile = reader.readLine() + INFO_FILE_EXTENSION;
            if (reader.read() != -1) {
                Throw.aRuntime(AppLauncherException.class, "App file \"" + appFilePath 
                        + "\" must only contain a single artifact ID");
            }
        } catch (IOException e) {
            Throw.aRuntime(AppLauncherException.class, "Error reading "
                    + "app file \"" + appFilePath + "\"", e);
        }
        
        // Create the App from this artifact ID
        try {
            return (A) createLib(appInfoFile);
        } catch (ClassCastException e) {
            Throw.aRuntime(AppLauncherException.class, "Main library specified in \"" + 
                    appInfoFile + "\" must implement the " + App.class + " interface", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <L extends Lib> L createLib(String infoFile) {
        // If this library as already been initialized, re-use it
        L lib = (L) loadedLibs.get(infoFile);
        if (lib != null) {
            return lib;
        }
        
        // Read the info file
        Properties libInfo = readInfoFile(infoFile);
        
        // Load the main class of the library
        Class<L> clazz = (Class<L>) loadMainClass(libInfo.getProperty(MAIN_CLASS_PROPERTY));
        
        // Create the instance of the library
        lib = createInstance(clazz);
        
        // Link the instance to its info
        lib.setInfo(libInfo.getProperty(ARTIFACT_NAME_PROPERTY),
                    libInfo.getProperty(ARTIFACT_DESCRIPTION_PROPERTY),
                    libInfo.getProperty(ARTIFACT_GROUP_ID_PROPERTY),
                    libInfo.getProperty(ARTIFACT_ARTIFACT_ID_PROPERTY),
                    libInfo.getProperty(ARTIFACT_VERSION_PROPERTY),
                    libInfo.getProperty(DEFAULT_SETTINGS_PROPERTY));
        
        // Add child libraries
        String libsStr = libInfo.getProperty(LIBRARIES_PROPERTY);
        for (String libName : libsStr == null ? new String[0] : libsStr.split(LIBRARY_SEPARATOR)) {
            lib.addChildLib(createLib(libName + INFO_FILE_EXTENSION));
        }
        
        // Initialize the library
        lib.init();
        
        // Add it to the loaded library
        loadedLibs.put(infoFile, lib);
        
        return lib;
    }

    @SuppressWarnings("serial")
    private static Properties readInfoFile(String infoFile) {
        Properties libInfo = new Properties() {
            public String getProperty(String key) {
                String value = super.getProperty(key);
                if (value == null && !key.equals(LIBRARIES_PROPERTY)) {
                    Throw.aRuntime(AppLauncherException.class, "Could not find "
                            + "property \"" + key + "\" in \"" + infoFile + "\"");
                }
                return value;
            }
        };
        try {
            libInfo.load(IO.getResourceasStream(CONFIGUANA_DIR_IN_JAR + infoFile));
        } catch (IOException e) {
            Throw.aRuntime(AppLauncherException.class, "Could not find "
                    + "configuana library info file \"" + infoFile + "\"");
        }
        
        String artifactID = libInfo.getProperty(ARTIFACT_ARTIFACT_ID_PROPERTY);
        if (!infoFile.replace(INFO_FILE_EXTENSION, "").equals(artifactID)) {
            Throw.aRuntime(AppLauncherException.class, "Library info file basename name ("+ infoFile
                    + ") must be the artifact ID of its described project (" + artifactID + ")");
        }
        
        return libInfo;
    }
    
    @SuppressWarnings("unchecked")
    private static Class<? extends Lib> loadMainClass(String mainClassName) {
        Class<?> clazz = null;
        try {
            try {
                clazz = Thread.currentThread().getContextClassLoader().loadClass(mainClassName);
            } catch (ClassNotFoundException e) {
                Throw.aRuntime(AppLauncherException.class, "Class not found");
            }
            if (!Lib.class.isAssignableFrom(clazz)) {
                Throw.aRuntime(AppLauncherException.class, "Class must implement "
                        + "the " + Lib.class.getCanonicalName() + " interface");
            }
        } catch (AppLauncherException e) {
            Throw.aRuntime(AppLauncherException.class, "Invalid " + Lib.class.getSimpleName() 
                    + " class \"" + mainClassName + "\"", e);
        }
        return (Class<? extends Lib>) clazz;
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
                        + "constructor must have no argument", e);
            }
        } catch (AppLauncherException e) {
            Throw.aRuntime(AppLauncherException.class, "Failed to create an "
                    + "instance of " + clazz.getSimpleName() + "\"", e);
        }
        return null;
    }

    private static Cmd<?,?> parseCmd(String arg, List<Class<? extends Cmd<?,?>>> cmdClasses) {
        // Try to match a command (using its consoleName()) with provided command
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
        
        // Extract App subclass from command class parameterization
        Type genericInterfaces = null;
        try {
            genericInterfaces = cmdClass.getGenericInterfaces()[0];
            Type appType = ((ParameterizedType) genericInterfaces).getActualTypeArguments()[0];
            if ((Class<A>) appType != trueAppClass) {
                Throw.aRuntime(AppLauncherException.class, "Generic app type of Cmd class \"" + 
                        cmdClass.getSimpleName() + "\" must be the same than the calling app");
            }
        } catch (Exception e) {
            Throw.aRuntime(AppLauncherException.class, "Could not extract the App "
                    + "class of Cmd class \"" + cmdClass.getSimpleName() + "\"", e);
        }
        
        // Extract Flag subclass from command class parameterization
        Class<F> flagClass = null;
        try {
            Type flagType = ((ParameterizedType) genericInterfaces).getActualTypeArguments()[1];
            flagClass = (Class<F>) flagType;
        } catch (Exception e) {
            Throw.aRuntime(AppLauncherException.class, "Could not extract the Flag "
                    + "class of Cmd class \"" + cmdClass.getSimpleName() + "\"", e);
        }
        
        // Some verifications
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