package com.atompacman.toolkat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

public final class IOUtils {

    //
    //  ~  INIT  ~  //
    //

    private IOUtils() {
        
    }
    
    
    //
    //  ~  MISC  ~  //
    //
    
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
