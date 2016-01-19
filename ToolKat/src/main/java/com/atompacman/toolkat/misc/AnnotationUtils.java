package com.atompacman.toolkat.misc;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class AnnotationUtils {

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
