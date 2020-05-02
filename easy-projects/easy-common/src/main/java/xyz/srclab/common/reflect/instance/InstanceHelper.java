package xyz.srclab.common.reflect.instance;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.invoke.InvokerHelper;
import xyz.srclab.common.lang.key.Key;
import xyz.srclab.common.reflect.NullRole;
import xyz.srclab.common.reflect.classpath.ClassPathHelper;

import java.lang.reflect.Constructor;

public class InstanceHelper {

    private static final Cache<Key, Constructor<?>> constructorCache = Cache.newGcThreadLocalL2();

    public static <T> T newInstance(String className) {
        @Nullable Class<?> cls = ClassPathHelper.getClass(className);
        Checker.checkArguments(cls != null, "Class not found: " + className);
        return newInstance(cls);
    }

    public static <T> T newInstance(Class<?> cls) {
        Constructor<?> constructor = getConstructor(cls);
        return (T) InvokerHelper.getConstructorInvoker(constructor).invoke();
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
