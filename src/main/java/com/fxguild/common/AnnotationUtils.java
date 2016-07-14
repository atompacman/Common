package com.fxguild.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * Utilities for {@link Annotation}s.
 */
public final class AnnotationUtils {

    //
    //  ~  INIT  ~  //
    //

    private AnnotationUtils() {
        
    }
    
    
    //
    //  ~  HAS ANNOTATION  ~  //
    //

    public static boolean hasAnnotation(AnnotatedElement            elem,
                                        Class<? extends Annotation> annotClass) {
        
        for (Annotation annotation : elem.getAnnotations()) {
            if (annotation.annotationType() == annotClass) {
                return true;
            }
        }
        return false;
    }
}
