package xyz.srclab.common.reflect;

import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.Nullable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.lang.key.Key;
import xyz.srclab.common.reflect.NullRole;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.time.temporal.Temporal;
import java.util.Date;

public class TypeHelper {

    private static final Cache<Type, Class<?>> rawTypeCache = Cache.newGcThreadLocalL2();

    private static final Cache<Key, Type> genericSuperClassCache = Cache.newGcThreadLocalL2();

    public static boolean isBasic(Object any) {
        return any instanceof CharSequence
                || any instanceof Number
                || any instanceof Type
                || any instanceof Date
                || any instanceof Temporal;
    }

    public static boolean isAssignable(Object from, Class<?> to) {
        Class<?> fromType = from instanceof Class<?> ? (Class<?>) from : from.getClass();
        return ClassUtils.isAssignable(fromType, to);
    }

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

    @Nullable
    public static Type getGenericSuperclass(Class<?> cls, Class<?> targetSuperClass) {
        Checker.checkArguments(
                targetSuperClass.isAssignableFrom(cls),
                targetSuperClass + "is not parent of " + cls
        );
        Type result = genericSuperClassCache.getNonNull(
                Key.from(cls, targetSuperClass),
                k -> getGenericSuperclass0(cls, targetSuperClass)
        );
        return result == NullRole.getNullType() ? null : result;
    }

    private static Type getGenericSuperclass0(Class<?> cls, Class<?> superClass) {
        Type current = cls;
        do {
            Class<?> currentClass = getRawType(current);
            if (superClass.equals(currentClass)) {
                return current;
            }
            current = currentClass.getGenericSuperclass();
        } while (current != null);
        return NullRole.getNullType();
    }
}
