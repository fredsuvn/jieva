package xyz.srclab.common.reflect;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.base.Context;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.lang.Key;

import java.lang.reflect.Constructor;

public class ConstructorHelper {

    private static final Cache<Key, Constructor<?>> constructorCache = Cache.newGcThreadLocalL2();

    public static <T> T newInstance(String className) {
        @Nullable Class<?> cls = Context.getClass(className);
        Checker.checkArguments(cls != null, "Class not found: " + className);
        return newInstance(cls);
    }

    public static <T> T newInstance(String className, ClassLoader classLoader) {
        @Nullable Class<?> cls = Context.getClass(className, classLoader);
        Checker.checkArguments(cls != null, "Class not found: " + className);
        return newInstance(cls);
    }

    public static <T> T newInstance(Class<?> cls) {
        @Nullable Constructor<?> constructor = getConstructor(cls);
        Checker.checkArguments(constructor != null, "Constructor not found: " + cls);
        return (T) ConstructorInvoker.of(constructor).invoke();
    }

    @Nullable
    public static <T> Constructor<T> getConstructor(Class<T> cls, Class<?>... parameterTypes) {
        Constructor<?> constructor = constructorCache.getNonNull(
                Key.from(cls, parameterTypes),
                k -> getConstructor0(cls, parameterTypes)
        );
        if (constructor == NullRole.getNullConstructor()) {
            return null;
        }
        return (Constructor<T>) constructor;
    }

    private static Constructor<?> getConstructor0(Class<?> cls, Class<?>... parameterTypes) {
        try {
            return cls.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            return NullRole.getNullConstructor();
        }
    }
}
