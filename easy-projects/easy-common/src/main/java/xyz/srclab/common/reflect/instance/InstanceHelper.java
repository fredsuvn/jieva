package xyz.srclab.common.reflect.instance;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.cache.threadlocal.ThreadLocalCache;
import xyz.srclab.common.environment.ClassPathHelper;
import xyz.srclab.common.invoke.InvokerHelper;
import xyz.srclab.common.reflect.signature.SignatureHelper;

import java.lang.reflect.Constructor;

public class InstanceHelper {

    private static final ThreadLocalCache<String, Constructor<?>> constructorCache =
            new ThreadLocalCache<>();

    public static <T> T newInstance(String className) {
        @Nullable Class<T> cls = ClassPathHelper.getClass(className);
        Checker.checkArguments(cls != null, "Class not found: " + className);
        return newInstance(cls);
    }

    public static <T> T newInstance(Class<?> cls) {
        Constructor<?> constructor = constructorCache.getNonNull(
                SignatureHelper.signConstructor(cls),
                k -> {
                    try {
                        return cls.getConstructor();
                    } catch (NoSuchMethodException e) {
                        throw new IllegalArgumentException(e);
                    }
                });
        return (T) InvokerHelper.getConstructorInvoker(constructor).invoke();
    }

    public static <T> Constructor<T> getConstructor(Class<T> cls, Class<?>... parameterTypes) {
        Constructor<?> constructor = constructorCache.getNonNull(
                SignatureHelper.signConstructor(cls, parameterTypes),
                k -> getConstructor0(cls, parameterTypes)
        );
        return (Constructor<T>) constructor;
    }

    private static Constructor<?> getConstructor0(Class<?> cls, Class<?>... parameterTypes) {
        try {
            return cls.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
