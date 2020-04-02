package xyz.srclab.common.reflect.type;

import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.Nullable;
import xyz.srclab.common.base.KeyHelper;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.cache.threadlocal.ThreadLocalCache;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.time.temporal.Temporal;
import java.util.Date;

public class TypeHelper {

    private static final Cache<Object, Type> typeCache = new ThreadLocalCache<>();

    private static final Cache<Object, Type[]> typesCache = new ThreadLocalCache<>();

    public static boolean isBasic(Object any) {
        return any instanceof CharSequence
                || any instanceof Number
                || any instanceof Date
                || any instanceof Temporal;
    }

    public static boolean isAssignable(Object from, Class<?> to) {
        Class<?> fromType = from instanceof Class<?> ? (Class<?>) from : from.getClass();
        return ClassUtils.isAssignable(fromType, to);
    }

    public static <T> Class<T> getRawClass(Type type) {
        return (Class<T>) typeCache.getNonNull(
                buildTypeKey(type, "getRawClass"),
                k -> getRawClass0(type)
        );
    }

    private static Class<?> getRawClass0(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }

        if (type instanceof ParameterizedType) {
            return getRawClass(((ParameterizedType) type).getRawType());
        }

        if (type instanceof TypeVariable) {
            Type boundType = ((TypeVariable<?>) type).getBounds()[0];
            if (boundType instanceof Class) {
                return (Class<?>) boundType;
            }
            return getRawClass(boundType);
        }

        if (type instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            if (upperBounds.length == 1) {
                return getRawClass(upperBounds[0]);
            }
        }

        return Object.class;
    }

    public static Type[] getGenericTypes(ParameterizedType parameterizedType) {
        return typesCache.getNonNull(buildTypeKey(
                parameterizedType, "getGenericTypes"),
                k -> getGenericTypes0(parameterizedType)
        );
    }

    private static Type[] getGenericTypes0(ParameterizedType parameterizedType) {
        return parameterizedType.getActualTypeArguments();
    }

    @Nullable
    public static Type findGenericSuperclass(Class<?> cls, Class<?> target) {
        Type returned = typeCache.getNonNull(
                buildTypeKey(cls, target, "findGenericSuperclass"),
                k -> findGenericSuperclass0(cls, target)
        );
        return returned == NullType.INSTANCE ? null : returned;
    }

    private static Type findGenericSuperclass0(Class<?> cls, Class<?> target) {
        Type current = cls;
        do {
            Class<?> currentClass = getRawClass(current);
            if (target.equals(currentClass)) {
                return current;
            }
            current = currentClass.getGenericSuperclass();
        } while (current != null);
        return NullType.INSTANCE;
    }

    private static Object buildTypeKey(Object... args) {
        return KeyHelper.buildKey(args);
    }

    private static final class NullType implements Type {

        private static final NullType INSTANCE = new NullType();
    }
}
