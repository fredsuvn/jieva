package xyz.srclab.common.reflect;

import org.apache.commons.lang3.ArrayUtils;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Format;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.lang.key.Key;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public class TypeKit {

    public static <T> Class<T> getRawType(Type type) {
        if (type instanceof Class) {
            return Cast.as(type);
        }
        return Cast.as(RawTypeFinder.find(type));
    }

    public static Type getGenericSuperclass(Class<?> cls, Class<?> targetClass) {
        return GenericSuperclassFinder.find(cls, targetClass);
    }

    public static Type getActualType(Type type, Type owner, Class<?> declaringClass) {
        if (type instanceof TypeVariable) {
            return getActualType((TypeVariable<?>) type, owner, declaringClass);
        }
        return type;
    }

    public static Type getActualType(TypeVariable<?> typeVariable, Type owner, Class<?> declaringClass) {
        return ActualTypeFinder.find(typeVariable, owner, declaringClass);
    }

    private static final class RawTypeFinder {

        // Don't need cache now.
        //private static final Cache<Type, Class<?>> cache = Cache.newCommonCache();

        public static Class<?> find(Type type) {
            return find0(type);
        }

        private static Class<?> find0(Type type) {
            //if (type instanceof Class) {
            //    return (Class<?>) type;
            //}
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

    private static final class GenericSuperclassFinder {

        public static Type find(Class<?> cls, Class<?> targetClass) {
            if (!targetClass.isAssignableFrom(cls)) {
                throw new IllegalArgumentException("Target class must be super class of given class");
            }
            return find0(cls, targetClass);
        }

        private static Type find0(Class<?> cls, Class<?> targetClass) {
            Type current = cls;
            do {
                Class<?> currentClass = TypeKit.getRawType(current);
                if (targetClass.equals(currentClass)) {
                    return current;
                }
                current = currentClass.getGenericSuperclass();
            } while (current != null);
            throw new IllegalStateException("Unexpected error: cls = " + cls + ", targetClass = " + targetClass);
        }
    }

    private static final class ActualTypeFinder {

        private static final Cache<Key, Type> cache = Cache.newCommonCache();

        public static Type find(TypeVariable<?> type, Type owner, Class<?> declaringClass) {
            return cache.getNonNull(
                    Key.of(type, owner, declaringClass),
                    k -> findTypeVariable(type, owner, declaringClass)
            );
        }

        private static Type findTypeVariable(TypeVariable<?> type, Type owner, Class<?> declaringClass) {
            if (owner instanceof Class) {
                return findTypeVariableForClass(type, (Class<?>) owner, declaringClass);
            }
            if (owner instanceof ParameterizedType) {
                return findTypeVariableForParameterizedType(type, (ParameterizedType) owner, declaringClass);
            }
            throw new IllegalStateException(
                    Format.fastFormat("Cannot find actual type \"{}\" from {} to {}",
                            type, declaringClass, owner));
        }

        private static Type findTypeVariableForClass(TypeVariable<?> type, Class<?> owner, Class<?> declaringClass) {
            Type genericSuperclass = getGenericSuperclass(owner, declaringClass);
            if (!(genericSuperclass instanceof ParameterizedType)) {
                throw new IllegalStateException(
                        Format.fastFormat("Cannot find actual type \"{}\" from {} to {}",
                                type, declaringClass, owner));
            }
            Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
            @Nullable Type result = findTypeVariable0(type, actualTypeArguments, declaringClass);
            if (result == null) {
                throw new IllegalStateException(
                        Format.fastFormat("Cannot find actual type \"{}\" from {} to {}",
                                type, declaringClass, owner));
            }
            return result;
        }

        private static Type findTypeVariableForParameterizedType(
                TypeVariable<?> type, ParameterizedType owner, Class<?> declaringClass) {
            Class<?> rawOwner = TypeKit.getRawType(owner);
            if (rawOwner.equals(declaringClass)) {
                Type[] actualTypeArguments = owner.getActualTypeArguments();
                @Nullable Type result = findTypeVariable0(type, actualTypeArguments, declaringClass);
                if (result == null) {
                    throw new IllegalStateException(
                            Format.fastFormat("Cannot find actual type \"{}\" from {} to {}",
                                    type, declaringClass, owner));
                }
                return result;
            }
            return findTypeVariableForClass(type, rawOwner, declaringClass);
        }

        @Nullable
        private static Type findTypeVariable0(
                TypeVariable<?> type, Type[] actualTypeArguments, Class<?> declaringClass) {
            TypeVariable<?>[] typeVariables = declaringClass.getTypeParameters();
            int index = ArrayUtils.indexOf(typeVariables, type);
            if (index < 0) {
                return null;
            }
            return actualTypeArguments[index];
        }
    }
}
