package com.fxguild.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to tag class fields whose lifetime is expected to be shorter than that of its enclosing 
 * object.
 */
@Retention (RetentionPolicy.SOURCE)
@Target    (ElementType.FIELD)
public @interface Temporary {

}
