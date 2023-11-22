package xyz.fsgek.common.reflect;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.annotations.OutParam;
import xyz.fsgek.common.base.Gek;
import xyz.fsgek.common.base.GekString;
import xyz.fsgek.common.cache.GekCache;
import xyz.fsgek.common.collect.GekArray;
import xyz.fsgek.common.collect.GekColl;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utilities for reflection.
 *
 * @author fredsuvn
 */
public class GekReflect {

    private static final GekCache<Type, Map<TypeVariable<?>, Type>> TYPE_PARAMETER_MAPPING_CACHE = GekCache.softCache();

    /**
     * Returns new instance for given class name.
     * This method first uses {@link Class#forName(String)} to load given class,
     * then call the none-arguments constructor to create instance.
     * If loading failed, return null.
     *
     * @param className given clas name
     * @param <T>       type of result
     * @return new instance for given class name or null
     */
    @Nullable
    public static <T> T load(String className) {
        try {
            Class<?> cls = Class.forName(className);
            return GekReflect.newInstance(cls);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Returns last name of given class. The last name is sub-string after last dot, for example:
     * <p>
     * {@code String} is last name of {@code java.lang.String}.
     *
     * @param cls given class
     * @return last name of given class
     */
    public static String getLastName(Class<?> cls) {
        String name = cls.getName();
        int index = GekString.lastIndexOf(name, ".");
        if (index < 0 || index >= name.length() - 1) {
            return name;
        }
        return name.substring(index + 1);
    }

    /**
     * Returns raw type of given type, the given type must be a Class or ParameterizedType.
     * Returns null if given type neither be Class nor ParameterizedType.
     *
     * @param type given type
     * @return raw type of given type or null
     */
    @Nullable
    public static Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
        return null;
    }

    /**
     * Returns actual type argument of given parameterized type at specified index
     *
     * @param type  given parameterized type
     * @param index specified index
     * @return actual type argument at specified index
     */
    public static Type getActualTypeArgument(ParameterizedType type, int index) {
        Type[] args = type.getActualTypeArguments();
        return args[index];
    }

    /**
     * Returns upper bound type of given type (? extends).
     * If given type has lower bounds (? super), return null.
     *
     * @param type given type
     * @return upper bound type of given type or null
     */
    @Nullable
    public static Type getUpperBound(WildcardType type) {
        Type[] lowerBounds = type.getLowerBounds();
        if (GekArray.isNotEmpty(lowerBounds)) {
            return null;
        }
        Type[] upperBounds = type.getUpperBounds();
        if (GekArray.isEmpty(upperBounds)) {
            return Object.class;
        }
        return upperBounds[0];
    }

    /**
     * Returns lower bound type of given type (? super).
     * If given type has no lower bounds, return null.
     *
     * @param type given type
     * @return lower bound type of given type or null
     */
    @Nullable
    public static Type getLowerBound(WildcardType type) {
        Type[] lowerBounds = type.getLowerBounds();
        if (GekArray.isEmpty(lowerBounds)) {
            return null;
        }
        return lowerBounds[0];
    }

    /**
     * Returns field of specified name from given type (first getField then getDeclaredField).
     * Returns null if not found.
     *
     * @param type given type
     * @param name specified field name
     * @return field of specified name from given type or null
     */
    @Nullable
    public static Field getField(Type type, String name) {
        Class<?> rawType = getRawType(type);
        if (rawType == null) {
            return null;
        }
        try {
            return rawType.getField(name);
        } catch (NoSuchFieldException e) {
            try {
                return rawType.getDeclaredField(name);
            } catch (NoSuchFieldException ex) {
                return null;
            }
        }
    }

    /**
     * Returns method of specified name from given type (first getField then getDeclaredField).
     * Returns null if not found.
     *
     * @param type           given type
     * @param name           specified method name
     * @param parameterTypes parameter types
     * @return method of specified name from given type or null
     */
    @Nullable
    public static Method getMethod(Type type, String name, Class<?>... parameterTypes) {
        Class<?> rawType = getRawType(type);
        if (rawType == null) {
            return null;
        }
        try {
            return rawType.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            try {
                return rawType.getDeclaredMethod(name, parameterTypes);
            } catch (NoSuchMethodException ex) {
                return null;
            }
        }
    }

    /**
     * Creates a new instance of given type with empty constructor.
     * Return null if failed.
     *
     * @param type given type
     * @param <T>  type of instance
     * @return a new instance of given type with empty constructor or null
     */
    @Nullable
    public static <T> T newInstance(Class<?> type) {
        try {
            Constructor<?> constructor = type.getConstructor();
            return Gek.as(constructor.newInstance());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns array class of given component type.
     *
     * @param componentType given component type
     * @return array class of given component type
     */
    public static Class<?> arrayClass(Type componentType) {
        return arrayClass(componentType, componentType.getClass().getClassLoader());
    }

    /**
     * Returns array class of given component type.
     *
     * @param componentType given component type
     * @param classLoader   class loader
     * @return array class of given component type
     */
    public static Class<?> arrayClass(Type componentType, ClassLoader classLoader) {
        if (componentType instanceof Class) {
            String name;
            if (((Class<?>) componentType).isArray()) {
                name = "[" + ((Class<?>) componentType).getName();
            } else if (Objects.equals(boolean.class, componentType)) {
                name = "[Z";
            } else if (Objects.equals(byte.class, componentType)) {
                name = "[B";
            } else if (Objects.equals(short.class, componentType)) {
                name = "[S";
            } else if (Objects.equals(char.class, componentType)) {
                name = "[C";
            } else if (Objects.equals(int.class, componentType)) {
                name = "[I";
            } else if (Objects.equals(long.class, componentType)) {
                name = "[J";
            } else if (Objects.equals(float.class, componentType)) {
                name = "[F";
            } else if (Objects.equals(double.class, componentType)) {
                name = "[D";
            } else if (Objects.equals(void.class, componentType)) {
                name = "[V";
            } else {
                name = "[L" + ((Class<?>) componentType).getName() + ";";
            }
            try {
                return Class.forName(name, true, classLoader);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }
        if (componentType instanceof ParameterizedType) {
            return arrayClass(((ParameterizedType) componentType).getRawType(), classLoader);
        }
        if (componentType instanceof GenericArrayType) {
            String name = "";
            Type cur = componentType;
            do {
                name += "[";
                if (cur instanceof GenericArrayType) {
                    cur = ((GenericArrayType) cur).getGenericComponentType();
                } else if (cur instanceof Class) {
                    name += "L" + ((Class<?>) cur).getName() + ";";
                    break;
                } else if (cur instanceof ParameterizedType) {
                    name += "L" + ((Class<?>) ((ParameterizedType) cur).getRawType()).getName() + ";";
                    break;
                } else {
                    throw new IllegalArgumentException("Unsupported component type: " + componentType);
                }
            } while (true);
            try {
                return Class.forName(name, true, classLoader);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }
        throw new IllegalArgumentException("Unsupported component type: " + componentType);
    }

    /**
     * Returns wrapper class if given class is primitive, else return itself.
     *
     * @param cls given class
     * @return wrapper class if given class is primitive, else return itself
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
     * Returns whether current runtime has specified class.
     *
     * @param className specified class name
     * @return whether current runtime has specified class
     */
    public static boolean hasClass(String className) {
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * Returns whether target type can be assigned by source type with {@link Class#isAssignableFrom(Class)}.
     * If given type is primitive, it will be converted to its wrapper type.
     *
     * @param targetType target type
     * @param sourceType source type
     * @return whether target type can be assigned by source type
     */
    public static boolean isAssignableFrom(Class<?> targetType, Class<?> sourceType) {
        if (targetType.isPrimitive() || sourceType.isPrimitive()) {
            return toWrapperClass(targetType).isAssignableFrom(toWrapperClass(sourceType));
        }
        return targetType.isAssignableFrom(sourceType);
    }

    /**
     * Returns whether target type can be assigned by source type.
     * If given type is primitive, it will be converted to its wrapper type.
     * This method is similar as {@link #isAssignableFrom(Class, Class)} but supports {@link Type}.
     * <p>
     * Note in this type, {@link Object} can be assigned from any type,
     * {@link WildcardType} can not assign to or be assigned from any type.
     *
     * @param targetType target type
     * @param sourceType source type
     * @return whether target type can be assigned by source type
     */
    public static boolean isAssignableFrom(Type targetType, Type sourceType) {
        if (Objects.equals(targetType, sourceType) || Objects.equals(targetType, Object.class)) {
            return true;
        }
        if ((targetType instanceof WildcardType)
            || (sourceType instanceof WildcardType)) {
            return false;
        }
        if (targetType instanceof Class<?>) {
            Class<?> c1 = (Class<?>) targetType;
            if (sourceType instanceof Class<?>) {
                Class<?> c2 = (Class<?>) sourceType;
                return isAssignableFrom(c1, c2);
            }
            if (sourceType instanceof ParameterizedType) {
                ParameterizedType p2 = (ParameterizedType) sourceType;
                Class<?> r2 = getRawType(p2);
                if (r2 == null) {
                    return false;
                }
                return isAssignableFrom(c1, r2);
            }
            if (sourceType instanceof TypeVariable<?>) {
                TypeVariable<?> v2 = (TypeVariable<?>) sourceType;
                Type[] bounds = v2.getBounds();
                for (Type bound : bounds) {
                    if (isAssignableFrom(targetType, bound)) {
                        return true;
                    }
                }
                return false;
            }
            if (sourceType instanceof GenericArrayType) {
                if (!c1.isArray()) {
                    return false;
                }
                GenericArrayType g2 = (GenericArrayType) sourceType;
                Class<?> gc2 = getRawType(g2.getGenericComponentType());
                if (gc2 == null) {
                    return false;
                }
                return isAssignableFrom(c1.getComponentType(), gc2);
            }
            return false;
        }
        if (targetType instanceof ParameterizedType) {
            ParameterizedType p1 = (ParameterizedType) targetType;
            if (sourceType instanceof Class<?>) {
                ParameterizedType p2 = getGenericSuperType(sourceType, (Class<?>) p1.getRawType());
                if (p2 == null) {
                    return false;
                }
                return isAssignableFrom(p1, p2);
            }
            if (sourceType instanceof ParameterizedType) {
                ParameterizedType p2 = (ParameterizedType) sourceType;
                if (Objects.equals(p1.getRawType(), p2.getRawType())) {
                    Type[] a1 = p1.getActualTypeArguments();
                    Type[] a2 = p2.getActualTypeArguments();
                    if (a1 == null || a2 == null || a1.length != a2.length) {
                        return false;
                    }
                    for (int i = 0; i < a1.length; i++) {
                        Type at1 = a1[i];
                        Type at2 = a2[i];
                        if (!isAssignableFromForParameterizedType(at1, at2)) {
                            return false;
                        }
                    }
                    return true;
                }
                ParameterizedType sp2 = getGenericSuperType(p2, (Class<?>) p1.getRawType());
                if (sp2 == null) {
                    return false;
                }
                return isAssignableFrom(p1, sp2);
            }
            if (sourceType instanceof TypeVariable<?>) {
                TypeVariable<?> v2 = (TypeVariable<?>) sourceType;
                Type[] bounds = v2.getBounds();
                for (Type bound : bounds) {
                    if (isAssignableFrom(targetType, bound)) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
        if (targetType instanceof TypeVariable<?>) {
            TypeVariable<?> v1 = (TypeVariable<?>) targetType;
            if (sourceType instanceof TypeVariable<?>) {
                TypeVariable<?> v2 = (TypeVariable<?>) sourceType;
                Type[] bounds = v2.getBounds();
                for (Type bound : bounds) {
                    if (isAssignableFrom(targetType, bound)) {
                        return true;
                    }
                }
                return false;
            }
        }
        if (targetType instanceof GenericArrayType) {
            GenericArrayType g1 = (GenericArrayType) targetType;
            if (sourceType instanceof Class<?>) {
                Class<?> c2 = (Class<?>) sourceType;
                if (!c2.isArray()) {
                    return false;
                }
                Class<?> tc1 = getRawType(g1.getGenericComponentType());
                if (tc1 == null) {
                    return false;
                }
                Class<?> tc2 = c2.getComponentType();
                return isAssignableFrom(tc1, tc2);
            }
            if (sourceType instanceof GenericArrayType) {
                GenericArrayType g2 = (GenericArrayType) sourceType;
                Type tc1 = g1.getGenericComponentType();
                Type tc2 = g2.getGenericComponentType();
                return isAssignableFrom(tc1, tc2);
            }
            return false;
        }
        return false;
    }

    private static boolean isAssignableFromForParameterizedType(Type t1, Type t2) {
        if (Objects.equals(t1, t2)) {
            return true;
        }
        if (t1 instanceof WildcardType) {
            WildcardType w1 = (WildcardType) t1;
            Type upperBound = getUpperBound(w1);
            //? extends
            if (upperBound != null) {
                if (t2 instanceof WildcardType) {
                    WildcardType w2 = (WildcardType) t2;
                    Type upperBound2 = getUpperBound(w2);
                    //? extends
                    if (upperBound2 != null) {
                        return isAssignableFrom(upperBound, upperBound2);
                    }
                    return false;
                }
                return isAssignableFrom(upperBound, t2);
            }
            Type lowerBound = getLowerBound(w1);
            //? super
            if (lowerBound != null) {
                if (t2 instanceof WildcardType) {
                    WildcardType w2 = (WildcardType) t2;
                    Type lowerBound2 = getLowerBound(w2);
                    //? super
                    if (lowerBound2 != null) {
                        return isAssignableFrom(lowerBound2, lowerBound);
                    }
                    return false;
                }
                return isAssignableFrom(t2, lowerBound);
            }
        }
        return false;
    }

    /**
     * Returns generic super type with actual arguments from given type to given target type
     * (target type is super type of given type).
     * <p>
     * For example, these types:
     * <pre>
     *     private static interface Z&lt;B, T, U, R&gt; {}
     *     private static class ZS implements Z&lt;String, Integer, Long, Boolean&gt; {}
     * </pre>
     * The result of this method:
     * <pre>
     *     getGenericSuperType(ZS.class, Z.class);
     * </pre>
     * will be:
     * <pre>
     *     Z&lt;String, Integer, Long, Boolean&gt;
     * </pre>
     * Note given type must be subtype of target type, if it is not, return null.
     *
     * @param type   given type
     * @param target target type
     * @return generic super type with actual arguments
     */
    @Nullable
    public static ParameterizedType getGenericSuperType(Type type, Class<?> target) {
        boolean supportedType = false;
        if (type instanceof Class<?>) {
            supportedType = true;
            Class<?> cType = (Class<?>) type;
            if (!target.isAssignableFrom(cType)) {
                return null;
            }
        }
        if (type instanceof ParameterizedType) {
            supportedType = true;
            ParameterizedType pType = (ParameterizedType) type;
            Class<?> rawType = (Class<?>) pType.getRawType();
            if (!target.isAssignableFrom(rawType)) {
                return null;
            }
        }
        if (!supportedType) {
            return null;
        }
        TypeVariable<?>[] typeParameters = target.getTypeParameters();
        if (GekArray.isEmpty(typeParameters)) {
            throw new IllegalArgumentException("Given \"to\" type doesn't have type parameter.");
        }
        Map<TypeVariable<?>, Type> typeArguments = getTypeParameterMapping(type);
        Set<Type> stack = new HashSet<>();
        List<Type> actualTypeArguments = Arrays.stream(typeParameters)
            .map(it -> {
                Type nestedValue = GekColl.getNested(typeArguments, it, stack);
                stack.clear();
                return nestedValue == null ? it : nestedValue;
            }).collect(Collectors.toList());
        return GekType.parameterizedType(target, target.getDeclaringClass(), actualTypeArguments);
    }

    /**
     * Returns a mapping of type parameters for given type.
     * The key of mapping is type parameter, and the value is actual type argument
     * or inherited type parameter of subtype.
     * <p>
     * For example, these types:
     * <pre>
     *     private static class X extends Y&lt;Integer, Long&gt;{}
     *     private static class Y&lt;K, V&gt; implements Z&lt;Float, Double, V&gt; {}
     *     private static interface Z&lt;T, U, R&gt;{}
     * </pre>
     * The result of this method
     * <pre>
     *     parseActualTypeMapping(x)
     * </pre>
     * will be:
     * <pre>
     *     T -&gt; Float
     *     U -&gt; Double
     *     R -&gt; V
     *     K -&gt; Integer
     *     V -&gt; Long
     * </pre>
     *
     * @param type given type
     * @return a mapping of type parameters for given type
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
        if (GekArray.isNotEmpty(superTypes)) {
            for (Type superType : superTypes) {
                parseSuperGeneric(superType, typeMap);
                Class<?> superRaw = getRawType(superType);
                if (superRaw != null) {
                    Type[] superSuperTypes = superRaw.getGenericInterfaces();
                    parseSuperTypes(superSuperTypes, typeMap);
                }
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

    /**
     * Replaces the types in given {@code type} (including itself) that same with the {@code matcher}
     * with {@code replacement}.
     * This method supports:
     * <ul>
     *     <li>
     *         ParameterizedType: rawType, ownerType, actualTypeArguments;
     *     </li>
     *     <li>
     *         WildcardType: upperBounds, lowerBounds;
     *     </li>
     *     <li>
     *         GenericArrayType: componentType;
     *     </li>
     * </ul>
     * If the {@code deep} parameter is true, this method will recursively replace unmatched types.
     * <p>
     * If no type is matched or the type is not supported for replacing, return given type itself.
     *
     * @param type        type to be replaced
     * @param matcher     matcher type
     * @param replacement replacement type
     * @param deep        whether to recursively replace unmatched types
     * @return type after replacing
     */
    public static Type replaceType(Type type, Type matcher, Type replacement, boolean deep) {
        if (Objects.equals(type, matcher)) {
            return replacement;
        }
        boolean matched = false;
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            if (Objects.equals(rawType, matcher)) {
                matched = true;
                rawType = replacement;
            } else if (deep) {
                Type newRawType = replaceType(rawType, matcher, replacement, true);
                if (!Objects.equals(rawType, newRawType)) {
                    matched = true;
                    rawType = newRawType;
                }
            }
            Type ownerType = parameterizedType.getOwnerType();
            if (Objects.equals(ownerType, matcher)) {
                matched = true;
                ownerType = replacement;
            } else if (ownerType != null && deep) {
                Type newOwnerType = replaceType(ownerType, matcher, replacement, true);
                if (!Objects.equals(ownerType, newOwnerType)) {
                    matched = true;
                    ownerType = newOwnerType;
                }
            }
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            for (int i = 0; i < actualTypeArguments.length; i++) {
                Type actualTypeArgument = actualTypeArguments[i];
                if (Objects.equals(actualTypeArgument, matcher)) {
                    matched = true;
                    actualTypeArguments[i] = replacement;
                } else if (deep) {
                    Type newActualTypeArgument = replaceType(actualTypeArgument, matcher, replacement, true);
                    if (!Objects.equals(actualTypeArgument, newActualTypeArgument)) {
                        matched = true;
                        actualTypeArguments[i] = newActualTypeArgument;
                    }
                }
            }
            if (matched) {
                return GekType.parameterizedType(rawType, ownerType, actualTypeArguments);
            } else {
                return type;
            }
        }
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            Type[] upperBounds = wildcardType.getUpperBounds();
            for (int i = 0; i < upperBounds.length; i++) {
                Type bound = upperBounds[i];
                if (Objects.equals(bound, matcher)) {
                    matched = true;
                    upperBounds[i] = replacement;
                } else if (deep) {
                    Type newBound = replaceType(bound, matcher, replacement, true);
                    if (!Objects.equals(bound, newBound)) {
                        matched = true;
                        upperBounds[i] = newBound;
                    }
                }
            }
            Type[] lowerBounds = wildcardType.getLowerBounds();
            for (int i = 0; i < lowerBounds.length; i++) {
                Type bound = lowerBounds[i];
                if (Objects.equals(bound, matcher)) {
                    matched = true;
                    lowerBounds[i] = replacement;
                } else if (deep) {
                    Type newBound = replaceType(bound, matcher, replacement, true);
                    if (!Objects.equals(bound, newBound)) {
                        matched = true;
                        lowerBounds[i] = newBound;
                    }
                }
            }
            if (matched) {
                return GekType.wildcardType(upperBounds, lowerBounds);
            } else {
                return type;
            }
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) type;
            Type componentType = genericArrayType.getGenericComponentType();
            if (Objects.equals(componentType, matcher)) {
                matched = true;
                componentType = replacement;
            } else if (deep) {
                Type newComponentType = replaceType(componentType, matcher, replacement, true);
                if (!Objects.equals(componentType, newComponentType)) {
                    matched = true;
                    componentType = newComponentType;
                }
            }
            if (matched) {
                return GekType.genericArrayType(componentType);
            } else {
                return type;
            }
        }
        return type;
    }
}
