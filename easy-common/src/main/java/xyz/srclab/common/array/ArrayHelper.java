package xyz.srclab.common.array;

import xyz.srclab.common.base.KeyHelper;
import xyz.srclab.common.cache.threadlocal.ThreadLocalCache;

import java.lang.reflect.Array;

public class ArrayHelper {

    private static final ThreadLocalCache<Object, Class<?>> arrayTypeCache = new ThreadLocalCache<>();

    public static <A> A newArray(Class<?> componentType, int length) {
        return (A) Array.newInstance(componentType, length);
    }

    public static Class<?> findArrayType(Class<?> elementType) {
        return arrayTypeCache.getNonNull(
                buildArrayTypeKey(elementType, "findArrayType"),
                o -> findArrayType0(elementType)
        );
    }

    private static Class<?> findArrayType0(Class<?> elementType) {
        return Array.newInstance(elementType, 0).getClass();
    }

    private static Object buildArrayTypeKey(Class<?> cls, String typeScope) {
        return KeyHelper.buildKey(cls, typeScope);
    }
}
