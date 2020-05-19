package xyz.srclab.common.reflect;

import xyz.srclab.common.cache.Cache;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public class TypeHelper {

    private static final Cache<Type, Class<?>> rawTypeCache = Cache.newGcThreadLocalL2();

    public static <T> Class<T> getRawType(Type type) {
        Class<?> rawType = rawTypeCache.getNonNull(
                type,
                k -> getRawClass0(type)
        );
        return (Class<T>) rawType;
    }

    private static Class<?> getRawClass0(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            return getRawType(((ParameterizedType) type).getRawType());
        }
        if (type instanceof TypeVariable) {
            Type boundType = ((TypeVariable<?>) type).getBounds()[0];
            if (boundType instanceof Class) {
                return (Class<?>) boundType;
            }
            return getRawType(boundType);
        }
        if (type instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            if (upperBounds.length == 1) {
                return getRawType(upperBounds[0]);
            }
        }
        return Object.class;
    }
}
