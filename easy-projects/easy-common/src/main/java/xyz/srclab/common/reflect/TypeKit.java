package xyz.srclab.common.reflect;

import xyz.srclab.common.base.Cast;

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

    private static final class RawTypeFinder {

        // Don't need cache now.
        //private static final Cache<Type, Class<?>> cache = Cache.newL2();

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
}
