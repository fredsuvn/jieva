package xyz.srclab.common.reflect;

import xyz.srclab.annotation.Nullable;

import java.lang.reflect.Constructor;

public class ConstructorKit {

    @Nullable
    public static <T> Constructor<T> getConstructor(Class<T> cls, Class<?>... parameterTypes) {
        try {
            return cls.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @Nullable
    public static <T> Constructor<T> getDeclaredConstructor(Class<T> cls, Class<?>... parameterTypes) {
        try {
            return cls.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
