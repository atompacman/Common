package com.atompacman.toolkat.construction;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class EnumRepresConstruc<A> {

	//====================================== CONSTANTS ===========================================\\

	private static final String DEFAULT_STATIC_STRING_CONSTRUCTORS_NAME = "valueOf";
	
	
	
	//======================================= FIELDS =============================================\\

	private Class<A> clazz;
	private String staticConstrucName;
	private List<Method> staticConstruc;

	

	//======================================= METHODS ============================================\\

	//--------------------------------- PUBLIC CONSTRUCTORS --------------------------------------\\
	
	public EnumRepresConstruc(Class<A> clazz) {
		this(clazz, DEFAULT_STATIC_STRING_CONSTRUCTORS_NAME);
	}

	public EnumRepresConstruc(Class<A> clazz, String staticConstrucName) {
		this.clazz = clazz;
		this.staticConstrucName = staticConstrucName;
		detectEnumBasedStaticConstructors();
	}
	
	private void detectEnumBasedStaticConstructors() {
		this.staticConstruc = new ArrayList<Method>();

		for (Method method : clazz.getMethods()) {
			if (!method.getName().equals(staticConstrucName)) {
				continue;
			}
			if (method.getParameterTypes().length == 0) {
				continue;
			}
			if (!Modifier.isStatic(method.getModifiers())) {
				continue;
			}
			boolean typesAreAllEnums = true;
			for (Class<?> clazz : method.getParameterTypes()) {
				if (!(clazz.isEnum())) {
					typesAreAllEnums = false;
					break;
				}
			}
			if (typesAreAllEnums) {
				staticConstruc.add(method);
			}
		}
		if (staticConstruc.isEmpty()) {
			throw new RuntimeException("\"" + clazz.getSimpleName() + "\" has no static "
					+ "constructors with name \"" + DEFAULT_STATIC_STRING_CONSTRUCTORS_NAME + 
					"(...)\" with only enums as parameters.");
		}
	}


	//------------------------------------ NEW INSTANCE ------------------------------------------\\

	@SuppressWarnings("unchecked")
	public A newInstance(String repres) {
		try {
			for (Method constructor : staticConstruc) {
				Class<?>[] paramTypes = constructor.getParameterTypes();
				int nbParams = paramTypes.length;
				String copy = repres;
				Object[] args = new Object[nbParams];
				for (int i = 0; i < nbParams; ++i) {
					Class<?> enumClass = constructor.getParameterTypes()[i];
					Object enumCnst = findMatchingEnumCnst(enumClass, copy);
					args[i] = enumCnst;
					copy = copy.substring(enumCnst.toString().length());
				}
				if (copy.isEmpty()) {
					return (A) constructor.invoke(null, args);
				}
			}
			throw new Exception();
		} catch (Exception e) {
			throw new IllegalArgumentException("\"" + repres + "\" is not a valid "
					+ "representation of a \"" + clazz.getSimpleName() + "\" object.", e);
		}
	}
	
	private static Object findMatchingEnumCnst(Class<?> enumClass, String repres) throws Exception {
		Object betterMatch = null;
		
		for (Object cnst : enumClass.getEnumConstants()) {
			String cnstRepres = cnst.toString();
			if (repres.indexOf(cnstRepres) == 0) {
				if (betterMatch == null || cnstRepres.length() > betterMatch.toString().length()) {
					betterMatch = cnst;
				}
			}
		}
		if (betterMatch == null) {
			throw new Exception("No enum constant of class \"" + enumClass.getSimpleName()
					+ "\" is represented by \"" + repres + "\".");
		}
		return betterMatch;
	}
}
