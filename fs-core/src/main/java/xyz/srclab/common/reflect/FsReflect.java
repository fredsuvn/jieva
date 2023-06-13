package xyz.srclab.common.reflect;

import xyz.srclab.annotations.OutParam;
import xyz.srclab.common.base.FsArray;
import xyz.srclab.common.base.FsString;
import xyz.srclab.common.base.FsUnsafe;
import xyz.srclab.common.cache.FsCache;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Reflection utilities.
 *
 * @author fredsuvn
 */
public class FsReflect {

    private static final FsCache<Map<TypeVariable<?>, Type>> TYPE_PARAMETER_MAPPING_CACHE =
        FsUnsafe.ForCache.getOrCreateCache(FsUnsafe.ForCache.TYPE_PARAMETER_MAPPING);

    /**
     * Returns last name of given class. The last name is sub-string after last dot, for example:
     * <p>
     * {@code String} is last name of {@code java.lang.String}.
     *
     * @param cls given class
     */
    public static String getLastName(Class<?> cls) {
        String name = cls.getName();
        int index = FsString.lastIndexOf(name, ".");
        if (index < 0 || index >= name.length() - 1) {
            return name;
        }
        return name.substring(index + 1);
    }

    /**
     * Returns raw type of given type, the given type must be a Class or ParameterizedType.
     *
     * @param type given type
     */
    public static Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
        throw new IllegalArgumentException("Given type must be Class or ParameterizedType.");
    }

    /**
     * Returns wrapper class if given class is primitive, else return itself.
     *
     * @param cls given class
     */
    public static Class<?> toWrapperClass(Class<?> cls) {
        if (cls.isPrimitive()) {
            if (Objects.equals(cls, boolean.class)) {
                return Boolean.class;
            }
            if (Objects.equals(cls, byte.class)) {
                return Byte.class;
            }
            if (Objects.equals(cls, short.class)) {
                return Short.class;
            }
            if (Objects.equals(cls, char.class)) {
                return Character.class;
            }
            if (Objects.equals(cls, int.class)) {
                return Integer.class;
            }
            if (Objects.equals(cls, long.class)) {
                return Long.class;
            }
            if (Objects.equals(cls, float.class)) {
                return Float.class;
            }
            if (Objects.equals(cls, double.class)) {
                return Double.class;
            }
            if (Objects.equals(cls, void.class)) {
                return Void.class;
            }
        }
        return cls;
    }

    /**
     * Returns whether given type 1 can be assigned by given type 2 with {@link Class#isAssignableFrom(Class)}.
     * If given type is primitive, it will be converted to its wrapper type.
     *
     * @param t1 given type 1
     * @param t2 given type 2
     */
    public static boolean isAssignableFrom(Class<?> t1, Class<?> t2) {
        if (t1.isAssignableFrom(t2)) {
            return true;
        }
        return toWrapperClass(t1).isAssignableFrom(toWrapperClass(t2));
    }

    /**
     * Returns generic super type with actual arguments for from given type to given target type
     * (target type is super type of given type).
     * <p>
     * For example, these types:
     * <pre>
     *     private static interface Z&lt;B, T, U, R> {}
     *     private static class ZS implements Z&lt;String, Integer, Long, Boolean> {}
     * </pre>
     * The result of this method:
     * <pre>
     *     getGenericSuperType(ZS.class, Z.class);
     * </pre>
     * will be:
     * <pre>
     *     Z&lt;String, Integer, Long, Boolean>
     * </pre>
     *
     * @param type   given type
     * @param target target type
     */
    public static ParameterizedType getGenericSuperType(Type type, Class<?> target) {
        TypeVariable<?>[] typeParameters = target.getTypeParameters();
        if (FsArray.isEmpty(typeParameters)) {
            throw new IllegalArgumentException("Given \"to\" type doesn't have type parameter.");
        }
        Map<TypeVariable<?>, Type> typeArguments = getTypeParameterMapping(type);
        List<Type> actualTypeArguments = Arrays.stream(typeParameters)
            .map(it -> {
                Type cur = it;
                while (true) {
                    Type value = typeArguments.get(cur);
                    if (value == null) {
                        return cur;
                    }
                    cur = value;
                }
            }).collect(Collectors.toList());
        return FsType.parameterizedType(target, target.getDeclaringClass(), actualTypeArguments);
    }

    /**
     * Returns a mapping of type parameters for given type.
     * The key of mapping is type parameter, and the value is actual type argument
     * or inherited type parameter of subtype.
     * <p>
     * For example, these types:
     * <pre>
     *     private static class X extends Y&lt;Integer, Long>{}
     *     private static class Y&lt;K, V> implements Z&lt;Float, Double, V> {}
     *     private static interface Z&lt;T, U, R>{}
     * </pre>
     * The result of this method
     * <pre>
     *     parseActualTypeMapping(x)
     * </pre>
     * will be:
     * <pre>
     *     T -> Float
     *     U -> Double
     *     R -> V
     *     K -> Integer
     *     V -> Long
     * </pre>
     *
     * @param type given type
     */
    public static Map<TypeVariable<?>, Type> getTypeParameterMapping(Type type) {
        return TYPE_PARAMETER_MAPPING_CACHE.get(type, it -> {
            Map<TypeVariable<?>, Type> result = new HashMap<>();
            parseTypeParameterMapping(type, result);
            return Collections.unmodifiableMap(result);
        });
    }

    private static void parseTypeParameterMapping(Type type, @OutParam Map<TypeVariable<?>, Type> typeMap) {
        if (type instanceof Class) {
            Class<?> cur = (Class<?>) type;
            while (true) {
                Type superClass = cur.getGenericSuperclass();
                if (superClass != null) {
                    parseSuperGeneric(superClass, typeMap);
                }
                Type[] superTypes = cur.getGenericInterfaces();
                parseSuperTypes(superTypes, typeMap);
                cur = cur.getSuperclass();
                if (cur == null) {
                    return;
                }
            }
        }
        if (type instanceof ParameterizedType) {
            parseSuperGeneric(type, typeMap);
            parseTypeParameterMapping(((ParameterizedType) type).getRawType(), typeMap);
            return;
        }
        throw new IllegalArgumentException("Given type must be Class or ParameterizedType.");
    }

    private static void parseSuperTypes(Type[] superTypes, @OutParam Map<TypeVariable<?>, Type> typeMap) {
        if (!FsArray.isEmpty(superTypes)) {
            for (Type superType : superTypes) {
                parseSuperGeneric(superType, typeMap);
                Class<?> superRaw = FsReflect.getRawType(superType);
                Type[] superSuperTypes = superRaw.getGenericInterfaces();
                parseSuperTypes(superSuperTypes, typeMap);
            }
        }
    }

    private static void parseSuperGeneric(Type superGeneric, @OutParam Map<TypeVariable<?>, Type> typeMap) {
        if (superGeneric instanceof ParameterizedType) {
            ParameterizedType superParameterized = (ParameterizedType) superGeneric;
            Class<?> superRaw = (Class<?>) superParameterized.getRawType();
            Type[] superActualTypeArgs = superParameterized.getActualTypeArguments();
            TypeVariable<?>[] superTypeVariables = superRaw.getTypeParameters();
            for (int i = 0; i < superActualTypeArgs.length; i++) {
                Type actualType = superActualTypeArgs[i];
                typeMap.put(superTypeVariables[i], actualType);
            }
        }
    }
}
