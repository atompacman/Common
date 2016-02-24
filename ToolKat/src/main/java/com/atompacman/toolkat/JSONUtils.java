package com.atompacman.toolkat;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JSONUtils {

    //
    //  ~  CONSTANTS  ~  //
    //

    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    
    //
    //  ~  INIT  ~  //
    //

    private JSONUtils() {
        
    }
    
    
    //
    //  ~  PARSE  ~  //
    //

    public static <T> T parse(File jsonFile, Class<T> objClass) throws JsonProcessingException, 
                                                                       IOException {
        return MAPPER.readerFor(objClass).readValue(jsonFile);
    }
    
    public static <T> T parse(String json, Class<T> objClass) throws JsonParseException, 
                                                                     IOException {
        return MAPPER.readerFor(objClass).readValue(json);
    }
 
    public static <T extends Collection<E>,E> T parseCollection(String   json,
                                                                Class<T> collClass,
                                                                Class<E> elemClass)
                                                       throws JsonParseException, IOException {
        
        JavaType type = MAPPER.getTypeFactory().constructCollectionType(collClass, elemClass);
        return MAPPER.readerFor(type).readValue(json);
    }
    

    //
    //  ~  SERIALIZE  ~  //
    //

    public static void write(Object pojo, File jsonFile) throws IOException {
        MAPPER.writerFor(pojo.getClass()).writeValue(jsonFile, pojo);
    }
    
    public static String toJSONString(Object pojo) throws JsonProcessingException {
        return MAPPER.writerFor(pojo.getClass()).writeValueAsString(pojo);
    }
    
    public static String toPrettyJSONString(Object pojo) throws JsonProcessingException {
        return MAPPER.writerWithDefaultPrettyPrinter()
                .forType(pojo.getClass()).writeValueAsString(pojo);
    }
    
    public static String toQuietJSONString(Object pojo) {
        try {
            return toJSONString(pojo);
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    public static String toQuietPrettyJSONString(Object pojo) {
        try {
            return toPrettyJSONString(pojo);
        } catch (IOException e) {
            return e.getMessage();
        }
    }
}
