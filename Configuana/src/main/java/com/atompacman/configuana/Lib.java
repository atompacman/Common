package com.atompacman.configuana;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.atompacman.toolkat.exception.Throw;

public abstract class Lib {

    //====================================== CONSTANTS ===========================================\\

    private static final String SETTINGS_PROFILE_EXTENSION = ".csp";



    //======================================= FIELDS =============================================\\

    private String name;
    private String description;
    private String groupID;
    private String artifactID;
    private String version;
    private String defaultProfile;
    
    private Map<String, Settings> settingsProfiles;
    private Map<String, Lib>      childLibs;
    


    //=================================== ABSTRACT METHODS =======================================\\

    public abstract void init();
    
    public abstract List<Class<? extends StrictParam>> getParamsClasses();

    

    //======================================= METHODS ============================================\\

    //---------------------------------- PUBLIC CONSTRUCTOR --------------------------------------\\

    public Lib() {
        this.settingsProfiles = new LinkedHashMap<>();
        this.childLibs        = new HashMap<>();
    }


    //--------------------------------------- SETTERS --------------------------------------------\\

    void setInfo(String name,
                 String description,
                 String groupID,
                 String artifactID,
                 String version,
                 String defaultProfile) {
        
        this.name            = name;
        this.description     = description;
        this.groupID         = groupID;
        this.artifactID      = artifactID;
        this.version         = version;
        this.defaultProfile  = defaultProfile;
        
        loadSettingsProfiles();
    }

    private void loadSettingsProfiles() {
        // Scan the configuana directory in the jar for settings profile
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        File configuanaDir = new File(cl.getResource(AppLauncher.CONFIGUANA_DIR_IN_JAR).getFile());
        String jarPath = configuanaDir.getParent().replace("!","").replace("file:\\", "");
        Set<Class<? extends StrictParam>> rootParamClasses = new HashSet<>(getParamsClasses());
        String profileName = null;
        try (ZipInputStream jis = new ZipInputStream(new FileInputStream(jarPath))) {
            ZipEntry entry;
            while ((entry = jis.getNextEntry()) != null) {
                String eName = entry.getName();
                // Check that it follows the format configuana/{artifactID}.{profileName}.cps
                if (!eName.startsWith(AppLauncher.CONFIGUANA_DIR_IN_JAR + artifactID + ".") || 
                    !eName.endsWith(SETTINGS_PROFILE_EXTENSION)) {
                    continue;
                }
                
                // Only keep the name of the profile
                profileName = eName.substring(eName.indexOf('.') + 1, eName.lastIndexOf('.'));
                settingsProfiles.put(profileName, new Settings(entry.getName(), rootParamClasses));
            }
        } catch (Exception e) {
            Throw.aRuntime(AppLauncherException.class, "Failed to add settings profile \"" 
                    + profileName + "\" to library \"" + name + "\"", e);
        }
    }

    void addChildLib(Lib child) {
        childLibs.put(child.artifactID, child);
    }


    //--------------------------------------- GETTERS --------------------------------------------\\

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getArtifactID() {
        return artifactID;
    }

    public String getVersion() {
        return version;
    }

    public Settings getDefaultProfile() {
        return getSettingsProfile(defaultProfile);
    }

    public Settings getSettingsProfile(String profileName) {
        Settings profile = settingsProfiles.get(profileName);
        if (profile == null) {
            throw new IllegalArgumentException("There is not a setting profile named "
                    + "\"" + profileName + "\" in current application configuration");
        }
        return profile;
    }

    public Set<String> getSettingsProfileNames() {
        return settingsProfiles.keySet();
    }

    public Lib getChildLib(String artifactID) {
        Lib lib = childLibs.get(artifactID);
        if (lib == null) {
            throw new IllegalArgumentException("\" Library " + name + "\" has "
                    + "no child library with artifact ID \"" + artifactID + "\"");
        }
        return lib;
    }
}