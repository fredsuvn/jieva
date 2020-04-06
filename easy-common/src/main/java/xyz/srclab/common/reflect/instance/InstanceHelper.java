package xyz.srclab.common.reflect.instance;

import xyz.srclab.common.base.KeyHelper;
import xyz.srclab.common.cache.threadlocal.ThreadLocalCache;
import xyz.srclab.common.reflect.invoke.InvokerHelper;

import java.lang.reflect.Constructor;

public class InstanceHelper {

    private static final ThreadLocalCache<Object, Constructor<?>> CONSTRUCTOR_CACHE =
            new ThreadLocalCache<>();

    public static <T> T newInstance(Class<?> cls) {
        Constructor<?> constructor = CONSTRUCTOR_CACHE.getNonNull(
                KeyHelper.buildKey(cls),
                k -> {
                    try {
                        return cls.getConstructor();
                    } catch (NoSuchMethodException e) {
                        throw new IllegalArgumentException(e);
                    }
                });
        return (T) InvokerHelper.getConstructorInvoker(constructor).invoke();
    }

    public static Constructor<?> getConstructor(Class<?> cls, Class<?>[] parameterTypes) {
        return CONSTRUCTOR_CACHE.getNonNull(
                KeyHelper.buildKey(cls, parameterTypes),
                k -> getConstructor0(cls, parameterTypes)
        );
    }

    private static Constructor<?> getConstructor0(Class<?> cls, Class<?>[] parameterTypes) {
        try {
            return cls.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
