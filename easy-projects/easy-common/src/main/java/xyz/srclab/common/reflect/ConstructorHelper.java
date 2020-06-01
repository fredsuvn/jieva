package xyz.srclab.common.reflect;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Null;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.lang.key.Key;

import java.lang.reflect.Constructor;

public class ConstructorHelper {

    private static final Cache<Key, Constructor<?>> constructorCache = Cache.newGcThreadLocalL2();

    @Nullable
    public static <T> Constructor<T> getConstructor(Class<T> cls, Class<?>... parameterTypes) {
        Constructor<?> constructor = constructorCache.getNonNull(
                Key.of(cls, parameterTypes),
                k -> getConstructor0(cls, parameterTypes)
        );
        return Null.isNull(constructor) ? null : (Constructor<T>) constructor;
    }

    private static Constructor<?> getConstructor0(Class<?> cls, Class<?>... parameterTypes) {
        try {
            return cls.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            return Null.asConstructor();
        }
    }
}
