package com.fxguild.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to tag methods (usually private) that are only called by another method in the class. This
 * is to ease the understanding of the class construction. 
 */
@Retention (RetentionPolicy.SOURCE)
@Target    (ElementType.METHOD)
public @interface SubMethodOf {

    String value();
}
