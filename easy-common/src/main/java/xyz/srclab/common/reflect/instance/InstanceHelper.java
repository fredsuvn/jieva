package xyz.srclab.common.reflect.instance;

import xyz.srclab.common.cache.threadlocal.ThreadLocalCache;
import xyz.srclab.common.reflect.invoke.InvokerHelper;

import java.lang.reflect.Constructor;

public class InstanceHelper {

    private static final ThreadLocalCache<Class<?>, Constructor<?>> CONSTRUCTOR_CACHE =
            new ThreadLocalCache<>();

    public static <T> T newInstance(Class<?> cls) {
        Constructor<?> constructor = CONSTRUCTOR_CACHE.getNonNull(cls, c -> {
            try {
                return c.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(e);
            }
        });
        return (T) InvokerHelper.getConstructorInvoker(constructor).invoke();
    }
}
