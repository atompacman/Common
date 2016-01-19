package com.atompacman.toolkat.exception;

import org.codehaus.plexus.util.reflection.Reflector;
import org.codehaus.plexus.util.reflection.ReflectorException;

public class Throw {

    //==================================== STATIC FIELDS =========================================\\

    static String 	 msg;
    static Throwable cause;



    //==================================== STATIC METHODS ========================================\\

    //----------------------------------------- THROW --------------------------------------------\\

    public static <AE extends AbstractException> 
    void a(Class<AE> exception, String msg, Object...args) throws AE {
        a(exception, null, msg, args);
    }

    public static <AE extends AbstractException> 
    void a(Class<AE> exception, Throwable cause, String msg, Object...args) throws AE {
        saveInfo(String.format(msg, args), cause);

        try {       
            throw exception.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Could not throw exception of class "
                    + "\"" + exception.getSimpleName() + "\".", e);
        }
    }
    
    public static <AE extends AbstractException> 
    void aParametrizable(Class<AE> exception, String msg, Object...params) throws AE {
        aParametrizable(exception, null, msg, params);
    }

    public static <AE extends AbstractException> 
    void aParametrizable(Class<AE> exception,Throwable cause, String msg, Object...params)throws AE{
        saveInfo(msg, cause);
        try {
            throw new Reflector().newInstance(exception, params);
        } catch (ReflectorException e) {
            throw new RuntimeException("Could not throw exception of class "
                    + "\"" + exception.getSimpleName() + "\".", e);
        }
    }
    
    public static <ARE extends AbstractRuntimeException> 
    void aRuntime(Class<ARE> exception, String msg, Object...args) {
        aRuntime(exception, null, msg, args);
    }

    public static <ARE extends AbstractRuntimeException> 
    void aRuntime(Class<ARE> exception, Throwable cause, String msg, Object...args) {
        saveInfo(msg, cause);

        try {		
            throw exception.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Could not throw exception of class \"" + 
                    exception.getSimpleName() + "\": " + e.getMessage() + ".", e);
        }
    }


    //-------------------------------------- SAVE INFOS ------------------------------------------\\

    private static void saveInfo(String msg, Throwable cause) {
        if (cause != null) {
            msg += ": ";
            msg += cause.getMessage();
        } else {
            msg += '.';
        }
        Throw.msg   = msg;
        Throw.cause = cause;
    }
}
