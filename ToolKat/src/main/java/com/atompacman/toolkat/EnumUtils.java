package com.atompacman.toolkat;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public final class EnumUtils {

    //
    //  ~  INIT  ~  //
    //

    private EnumUtils() {
        
    }
    
    
    //
    //  ~  PREV / NEXT  ~  //
    //
    
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T prev(T value) {
        checkArgument(value.ordinal() != 0, 
                "Can't get previous enum value: ordinal == 0");
        return (T) value.getClass().getEnumConstants()[value.ordinal() - 1];
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T next(T value) {
        T[] values = (T[]) value.getClass().getEnumConstants();
        checkArgument(value.ordinal() < values.length - 1, 
                "Can't get next enum value: ordinal == max");
        return (T) values[value.ordinal() + 1];
    }

    
    //
    //  ~  MISC  ~  //
    //
    
    public static <T extends Annotation> T extractAnnotation(Enum<?> enumCst, Class<T> annotClazz) {
        try {
            Field field = enumCst.getClass().getField(enumCst.toString());
            T annotation = field.getAnnotation(annotClazz);
            checkNotNull(annotation, "Could not find annotation \"%s\" on enum constant \"%s\"", 
                         annotClazz.getName(), enumCst.name());
            return annotation;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public static String formatName(Enum<?> value) {
        return value.name().toLowerCase().replace('_', ' ');
    }
}
