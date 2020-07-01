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
        return Cast.as(RawTypeFinder.find(type));
    }

    public static Type getGenericType(TypeRef<?> typeRef) {
        return typeRef.getType();
    }

    public static Type getGenericSignature(Type type, Class<?> target) {
        return target.isInterface() ? getGenericInterface(type, target) : getGenericSuperclass(type, target);
    }

    public static Type getGenericSuperclass(Type type, Class<?> targetClass) {
        return GenericSuperclassFinder.find(type, targetClass);
    }

    public static Type getGenericInterface(Type type, Class<?> targetInterface) {
        return GenericInterfaceFinder.find(type, targetInterface);
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

    public static Type getUpperType(Type type) {
        if (type instanceof TypeVariable) {
            return getUpperType(((TypeVariable<?>) type).getBounds()[0]);
        }
        if (type instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            if (upperBounds.length == 1) {
                return getUpperType(upperBounds[0]);
            }
        }
        return type;
    }

    private static final class RawTypeFinder {

        private static Class<?> find(Type type) {
            if (type instanceof Class) {
                return (Class<?>) type;
            }
            if (type instanceof ParameterizedType) {
                return find(((ParameterizedType) type).getRawType());
            }
            if (type instanceof TypeVariable) {
                Type boundType = ((TypeVariable<?>) type).getBounds()[0];
                if (boundType instanceof Class) {
                    return (Class<?>) boundType;
                }
                return find(boundType);
            }
            if (type instanceof WildcardType) {
                Type[] upperBounds = ((WildcardType) type).getUpperBounds();
                if (upperBounds.length == 1) {
                    return find(upperBounds[0]);
                }
            }
            return Object.class;
        }
    }

    private static final class GenericSuperclassFinder {

        private static Type find(Type type, Class<?> targetClass) {
            Type currentType = type;
            do {
                Class<?> currentClass = TypeKit.getRawType(currentType);
                if (targetClass.equals(currentClass)) {
                    return currentType;
                }
                currentType = currentClass.getGenericSuperclass();
            } while (currentType != null);
            throw new IllegalStateException(
                    "Cannot find generic super class: type = " + type + ", target = " + targetClass);
        }
    }

    private static final class GenericInterfaceFinder {

        private static final Cache<Key, Type> cache = Cache.newCommonCache();

        public static Type find(Type type, Class<?> targetInterface) {
            Class<?> rawType = TypeKit.getRawType(type);
            if (rawType.equals(targetInterface)) {
                return type;
            }
            return cache.getNonNull(Key.of(rawType, targetInterface), k -> find0(rawType, targetInterface));
        }

        private static Type find0(Class<?> rawType, Class<?> targetInterface) {
            Type[] types = rawType.getGenericInterfaces();
            if (!ArrayUtils.isEmpty(types)) {
                for (Type type : types) {
                    @Nullable Type type0 = find0(type, targetInterface);
                    if (type0 != null) {
                        return type0;
                    }
                }
            }
            throw new IllegalStateException(
                    "Cannot find generic super interface: type = " + rawType + ", target = " + targetInterface);
        }

        @Nullable
        private static Type find0(Type type, Class<?> targetInterface) {
            Class<?> rawType = TypeKit.getRawType(type);
            if (rawType.equals(targetInterface)) {
                return type;
            }
            Type[] types = rawType.getGenericInterfaces();
            if (ArrayUtils.isEmpty(types)) {
                return null;
            }
            for (Type t : types) {
                @Nullable Type type0 = find0(t, targetInterface);
                if (type0 != null) {
                    return type0;
                }
            }
            return null;
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
            Type genericSignature = getGenericSignature(owner, declaringClass);
            if (!(genericSignature instanceof ParameterizedType)) {
                return type;
            }
            Type[] actualTypeArguments = ((ParameterizedType) genericSignature).getActualTypeArguments();
            @Nullable Type result = findTypeVariable0(type, actualTypeArguments, declaringClass);
            if (result == null) {
                throw new IllegalStateException(
                        Format.fastFormat("Cannot find actual type {} from {} to {}",
                                type, declaringClass, owner));
            }
            return result;
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
