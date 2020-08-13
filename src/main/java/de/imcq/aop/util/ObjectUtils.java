package de.imcq.aop.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 * @author CQ
 */
public final class ObjectUtils {


    public static <T> T newInstance(Class<T> instanceClass) {
        try {
            Constructor<T> constructor = instanceClass.getDeclaredConstructor();
            makeAccessible(constructor);
            return constructor.newInstance();
        } catch (Throwable e) {
            throw new IllegalArgumentException("Cannot instantiate " + instanceClass, e);
        }
    }

    public static void makeAccessible(Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers()) ||
                !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
            ctor.setAccessible(true);
        }
    }

    private ObjectUtils() {
    }
}
