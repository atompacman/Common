package com.fxguild.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to tag a method that implements a method prototype defined by an interface or an abstract 
 * class.
 */
@Retention (RetentionPolicy.SOURCE)
@Target    (ElementType.METHOD)
public @interface Implement {

}
