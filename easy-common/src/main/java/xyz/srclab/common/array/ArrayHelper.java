package xyz.srclab.common.array;

import xyz.srclab.common.cache.threadlocal.ThreadLocalCache;
import xyz.srclab.common.collection.CollectionHelper;
import xyz.srclab.common.reflect.type.TypeHelper;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Collection;

public class ArrayHelper {

    public static <A> A newArray(Class<?> componentType, int length) {
        return (A) Array.newInstance(componentType, length);
    }

    public static <A> A newArray(Type componentType, int length) {
        return newArray(TypeHelper.getRawClass(componentType), length);
    }

    public static <T> T[] toArray(Iterable<? extends T> iterable, Class<T> componentType) {
        Collection<? extends T> collection = CollectionHelper.castToCollection(iterable);
        return collection.toArray(newArray(componentType, collection.size()));
    }

    private static final ThreadLocalCache<Type, Class<?>> arrayTypeCache = new ThreadLocalCache<>();

    public static Class<?> findArrayType(Type elementType) {
        return arrayTypeCache.getNonNull(elementType, ArrayHelper::findArrayType0);
    }

    private static Class<?> findArrayType0(Type elementType) {
        return Array.newInstance(TypeHelper.getRawClass(elementType), 0).getClass();
    }
}
