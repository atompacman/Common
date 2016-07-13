package com.atompacman.toolkat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public final class IOUtils {

    //
    //  ~  INIT  ~  //
    //

    private IOUtils() {
        
    }
    
    
    //
    //  ~  MISC  ~  //
    //
    
    public static File getMavenResource(Class<?> callerClass, String fileName) 
            throws FileNotFoundException {
        
        String className = callerClass.getCanonicalName();
        String path = Paths.get(className.replace('.', File.separatorChar), fileName).toString();
        
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        if (url == null) {
            throw new FileNotFoundException("Could not find resource file \"" + path + "\"");
        }
        
        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Should not happen");
        }
    }
    
    public static File getResource(String path) throws FileNotFoundException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        if (url == null) {
            throw new FileNotFoundException("Cannot find resource \"" + path + "\".");
        }
        return new File(url.getPath());
    }
    
    public static InputStream getResourceAsStream(String path) throws FileNotFoundException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException("Cannot find resource \"" + path + "\".");
        }
        return is;
    }
}
