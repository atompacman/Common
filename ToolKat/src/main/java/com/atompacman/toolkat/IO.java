package com.atompacman.toolkat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import org.apache.commons.io.IOUtils;

public class IO {

    public static String getPath(String...pathElem) {
        File file = buildInitFile(pathElem);

        for (int i = 1; i < pathElem.length; ++i) {
            file = new File(file.getAbsolutePath(), pathElem[i]);
        }
        return file.getAbsolutePath();
    }

    public static File getFile(String...pathElem) throws FileNotFoundException {
        File file = buildInitFile(pathElem);

        try {
            for (int i = 1; i < pathElem.length; ++i) {
                file = new File(file.getCanonicalPath(), pathElem[i]);
            }
            file = file.getCanonicalFile();

            if (!file.exists()) {
                throw new FileNotFoundException("Cannot find file "
                        + "\"" + file.getCanonicalPath() + "\".");
            }
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            for (String elem : pathElem) {
                sb.append(elem).append(File.separatorChar);
            }
            throw new FileNotFoundException("Cannot find file \"" + sb.toString() + "\".");
        }
        return file;
    }

    private static File buildInitFile(String...pathElem) {
        if (pathElem == null) {
            throw new IllegalArgumentException("Null file path elements.");
        }
        if (pathElem.length == 0) {
            throw new IllegalArgumentException("File path elements cannot be empty.");
        }
        return new File(pathElem[0]);
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

    public static ByteBuffer getResourceByteBuffer(String path) throws IOException {
        return ByteBuffer.wrap(IOUtils.toByteArray(getResourceAsStream(path)));
    }
}
