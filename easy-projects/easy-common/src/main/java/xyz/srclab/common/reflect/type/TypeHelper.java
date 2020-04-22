package xyz.srclab.common.reflect.type;

import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.Nullable;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.cache.threadlocal.ThreadLocalCache;
import xyz.srclab.common.collection.list.ListHelper;
import xyz.srclab.common.lang.key.Key;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TypeHelper {

    private static final Cache<Type, Class<?>> rawTypeCache = new ThreadLocalCache<>();

    private static final Cache<Key, Type> typeCache = new ThreadLocalCache<>();

    private static final Cache<Key, List<Type>> typesCache = new ThreadLocalCache<>();

    private static final Cache<Key, List<TypeVariable<?>>> typeVariablesCache = new ThreadLocalCache<>();

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

    public static Class<?> getRawClass(Type type) {
        return rawTypeCache.getNonNull(
                type,
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

    @Nullable
    public static Type getGenericSuperclass(Class<?> cls, Class<?> target) {
        Type returned = typeCache.getNonNull(
                Key.from(cls, target, "getGenericSuperclass"),
                k -> getGenericSuperclass0(cls, target)
        );
        return returned == NullType.INSTANCE ? null : returned;
    }

    private static Type getGenericSuperclass0(Class<?> cls, Class<?> superClass) {
        Type current = cls;
        do {
            Class<?> currentClass = getRawClass(current);
            if (superClass.equals(currentClass)) {
                return current;
            }
            current = currentClass.getGenericSuperclass();
        } while (current != null);
        return NullType.INSTANCE;
    }

    @Immutable
    public static List<Type> getGenericTypes(ParameterizedType parameterizedType) {
        return typesCache.getNonNull(
                Key.from(parameterizedType, "getGenericTypes"),
                k -> ListHelper.immutable(Arrays.asList(getGenericTypes0(parameterizedType)))
        );
    }

    private static Type[] getGenericTypes0(ParameterizedType parameterizedType) {
        return parameterizedType.getActualTypeArguments();
    }

    @Immutable
    public static List<TypeVariable<?>> getTypeParameters(Class<?> cls) {
        return typeVariablesCache.getNonNull(
                Key.from(cls, "getTypeParameters"),
                k -> ListHelper.immutable(Arrays.asList(getTypeParameters0(cls)))
        );
    }

    private static TypeVariable<?>[] getTypeParameters0(Class<?> cls) {
        return cls.getTypeParameters();
    }

    private static final class NullType implements Type {

        private static final NullType INSTANCE = new NullType();
    }
}
