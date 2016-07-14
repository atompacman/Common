package com.fxguild.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to tag class fields with values that can be derived from other fields at all time.
 */
@Retention (RetentionPolicy.SOURCE)
@Target    (ElementType.FIELD)
public @interface DerivableFrom {

    String[] value();
}
