package com.atompacman.toolkat.misc;

import java.lang.annotation.Annotation;

public class EnumUtils {

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T prev(T value) {
        if (value.ordinal() == 0) {
            throw new IllegalArgumentException("Can't get previous enum value: ordinal == 0");
        }
        return (T) value.getClass().getEnumConstants()[value.ordinal() - 1];
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T next(T value) {
        T[] values = (T[]) value.getClass().getEnumConstants();
        if (value.ordinal() == values.length - 1) {
            throw new IllegalArgumentException("Can't get next enum value: ordinal == max");
        }
        return (T) values[value.ordinal() + 1];
    }

    public static <T extends Annotation> T extractAnnotation(Enum<?> enumCst, Class<T> annotClazz) {
        try {
            return enumCst.getClass().getField(enumCst.toString()).getAnnotation(annotClazz);
        } catch (Exception e) {
            throw new RuntimeException(enumCst + " is not an enum field with "
                    + "the " + annotClazz.getSimpleName() + " annotation", e);
        }
    }

    public static String formatName(Enum<?> value) {
        return value.name().toLowerCase().replace('_', ' ');
    }
}
