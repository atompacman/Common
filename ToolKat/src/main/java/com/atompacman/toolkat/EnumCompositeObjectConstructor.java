package com.atompacman.toolkat;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import com.atompacman.toolkat.AutoValue_EnumCompositeObjectConstructor;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

@AutoValue
public abstract class EnumCompositeObjectConstructor<A> {

    //
    //  ~  CONSTANTS  ~  //
    //

    private static final ImmutableSet<String> STD_STATIC_CTOR_NAMES = ImmutableSet.of(
            "of", "valueOf", "create", "getInstance", "make");


    //
    //  ~  FIELDS  ~  //
    //

    protected abstract Class<A>              getTargetClass();
    protected abstract ImmutableList<Method> getStaticCtors();


    //
    //  ~  INIT  ~  //
    //

    public static <A> EnumCompositeObjectConstructor<A> of(Class<A> clazz) {
      List<Method> ctors = new LinkedList<Method>();

      for (Method method : clazz.getMethods()) {
          if (!STD_STATIC_CTOR_NAMES.contains(method.getName()) ||
              method.getParameterTypes().length == 0            ||
              !Modifier.isStatic(method.getModifiers())) {
              continue;
          }
          boolean argAreAllEnums = true;
          for (Class<?> c : method.getParameterTypes()) {
              if (!(c.isEnum())) {
                  argAreAllEnums = false;
                  break;
              }
          }
          if (argAreAllEnums) {
              ctors.add(method);
          }
      }
      checkArgument(!ctors.isEmpty(), "\"%s\" has no static constructors with names \"%s\" "
              + "with only enums as parameters", clazz.getSimpleName(), STD_STATIC_CTOR_NAMES);

      return new AutoValue_EnumCompositeObjectConstructor<A>(clazz, ImmutableList.copyOf(ctors));
    }


    //
    //  ~  PARSE FROM STRING  ~  //
    //

    @SuppressWarnings("unchecked")
    public A parse(String repres) {
        try {
            for (Method constructor : getStaticCtors()) {
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
            throw new Exception("Could not find an appropriate constructor");
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("\"%s\" is not a valid representation "
                    + "of a \"%s\" object", repres, getTargetClass().getSimpleName()), e);
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
        checkArgument(betterMatch != null, "No enum constant of class \"%s\" is "
                + "represented by \"%s\"", enumClass.getSimpleName(), repres);

        return betterMatch;
    }
}
