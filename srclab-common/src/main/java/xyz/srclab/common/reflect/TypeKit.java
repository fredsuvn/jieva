package xyz.srclab.common.reflect;

import org.apache.commons.lang3.ArrayUtils;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.annotation.ReturnType;
import xyz.srclab.common.base.Format;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.base.Key;

import java.lang.reflect.*;

public class TypeKit {

    public static Class<?> getRawType(Type type) {
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

    @ReturnType({
            Class.class,
            ParameterizedType.class,
    })
    public static Type getGenericSignature(Type type, Class<?> target) {
        return target.isInterface() ? getGenericInterface(type, target) : getGenericSuperclass(type, target);
    }

    @ReturnType({
            Class.class,
            ParameterizedType.class,
    })
    public static Type getGenericSuperclass(Type type, Class<?> targetSuperclass) {
        return GenericSignatureFinder.findSuperclass(type, targetSuperclass);
    }

    @ReturnType({
            Class.class,
            ParameterizedType.class,
    })
    public static Type getGenericInterface(Type type, Class<?> targetInterface) {
        return GenericSignatureFinder.findInterface(type, targetInterface);
    }

    public static Type tryActualType(Type type, Type owner, Class<?> declaringClass) {
        if (type instanceof TypeVariable) {
            return tryActualType((TypeVariable<?>) type, owner, declaringClass);
        }
        return type;
    }

    public static Type tryActualType(
            TypeVariable<?> typeVariable, Type owner, Class<?> declaringClass) {
        return ActualTypeFinder.find(typeVariable, owner, declaringClass);
    }

    @ReturnType({
            Class.class,
            ParameterizedType.class,
            GenericArrayType.class,
    })
    public static Type getUpperBound(TypeVariable<?> type) {
        return getUpperBound(type.getBounds()[0]);
    }

    @ReturnType({
            Class.class,
            ParameterizedType.class,
            GenericArrayType.class,
    })
    public static Type getUpperBound(WildcardType type) {
        Type[] upperBounds = type.getUpperBounds();
        if (ArrayUtils.isNotEmpty(upperBounds)) {
            return getUpperBound(upperBounds[0]);
        }
        return Object.class;
    }

    @ReturnType({
            Class.class,
            ParameterizedType.class,
            GenericArrayType.class,
    })
    public static Type getUpperBound(Type type) {
        if (type instanceof TypeVariable) {
            return getUpperBound((TypeVariable<?>) type);
        }
        if (type instanceof WildcardType) {
            return getUpperBound((WildcardType) type);
        }
        return type;
    }

    private static final class GenericSignatureFinder {

        private static final Cache<Key, Type> cache = Cache.commonCache();

        @ReturnType({
                Class.class,
                ParameterizedType.class,
        })
        public static Type findSuperclass(Type type, Class<?> targetSuperclass) {
            @Nullable Type currentType = getUpperBound(type);
            do {
                Class<?> currentClass = TypeKit.getRawType(currentType);
                if (targetSuperclass.equals(currentClass)) {
                    return currentType;
                }
                currentType = currentClass.getGenericSuperclass();
            } while (currentType != null);
            throw new IllegalArgumentException(
                    "Cannot find generic super class: type = " + type + ", target = " + targetSuperclass);
        }

        @ReturnType({
                Class.class,
                ParameterizedType.class,
        })
        public static Type findInterface(Type type, Class<?> targetInterface) {
            Class<?> rawType = TypeKit.getRawType(getUpperBound(type));
            if (rawType.equals(targetInterface)) {
                return type;
            }
            return cache.getNonNull(Key.of(rawType, targetInterface), k -> findFromRawType(rawType, targetInterface));
        }

        private static Type findFromRawType(Class<?> rawType, Class<?> targetInterface) {
            Type[] genericInterfaces = rawType.getGenericInterfaces();
            if (!ArrayUtils.isEmpty(genericInterfaces)) {
                for (Type type : genericInterfaces) {
                    @Nullable Type type0 = find0(type, targetInterface);
                    if (type0 != null) {
                        return type0;
                    }
                }
            }
            throw new IllegalArgumentException(
                    "Cannot find generic super interface: type = " + rawType + ", target = " + targetInterface
            );
        }

        @Nullable
        private static Type find0(Type type, Class<?> targetInterface) {
            Class<?> rawType = TypeKit.getRawType(getUpperBound(type));
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

        private static final Cache<Key, Type> cache = Cache.commonCache();

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
                throw new IllegalArgumentException(
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
