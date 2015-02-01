package com.atompacman.toolkat.exception;

public class Throw {

	//==================================== STATIC FIELDS =========================================\\

	static String 	 msg;
	static Throwable cause;



	//==================================== STATIC METHODS ========================================\\

	//----------------------------------------- THROW --------------------------------------------\\

	public static <AE extends AbstractException> 
	void a(Class<AE> exception, String msg) throws AE {
		a(exception, msg, null);
	}

	public static <AE extends AbstractException> 
	void a(Class<AE> exception, String msg, Throwable cause) throws AE {
		saveInfo(msg, cause);

		try {		
			throw exception.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Could not throw exception of class "
					+ "\"" + exception.getSimpleName() + "\".", e);
		}
	}

	public static <ARE extends AbstractRuntimeException> 
	void aRuntime(Class<ARE> exception, String msg) {
		aRuntime(exception, msg, null);
	}

	public static <ARE extends AbstractRuntimeException> 
	void aRuntime(Class<ARE> exception, String msg, Throwable cause) {
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
		Throw.msg =  msg;
		Throw.cause = cause;
	}
}
