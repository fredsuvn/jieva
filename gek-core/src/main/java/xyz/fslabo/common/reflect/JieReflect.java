package xyz.fslabo.common.reflect;

import xyz.fslabo.annotations.Immutable;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.annotations.OutParam;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.base.JieString;
import xyz.fslabo.common.cache.Cache;
import xyz.fslabo.common.coll.JieArray;
import xyz.fslabo.common.coll.JieColl;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utilities for reflection.
 *
 * @author fredsuvn
 */
public class JieReflect {

    private static final Type[] SINGLETON_OBJECT_CLASS_ARRAY = {Object.class};
    private static final Cache<Type, Map<TypeVariable<?>, Type>> TYPE_PARAMETER_MAPPING_CACHE = Cache.softCache();

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
        int index = JieString.lastIndexOf(name, ".");
        if (index < 0) {
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
     * Returns first upper bound type of given wildcard type (? extends).
     * Note that if no upper bound is explicitly declared, return {@code Object.class}.
     *
     * @param type given wildcard type
     * @return first upper bound type of given wildcard type
     */
    public static Type getUpperBound(WildcardType type) {
        Type[] upperBounds = type.getUpperBounds();
        if (JieArray.isNotEmpty(upperBounds)) {
            return upperBounds[0];
        }
        return Object.class;
    }

    /**
     * Returns first lower bound type of given wildcard type (? super).
     * If given type has no lower bound, return null.
     *
     * @param type given wildcard type
     * @return first lower bound type of given wildcard type or null
     */
    @Nullable
    public static Type getLowerBound(WildcardType type) {
        Type[] lowerBounds = type.getLowerBounds();
        if (JieArray.isNotEmpty(lowerBounds)) {
            return lowerBounds[0];
        }
        return null;
    }

    /**
     * Returns first bound type of given type variable (T extends).
     * Note that if no upper bound is explicitly declared, return {@code Object.class}.
     *
     * @param type given type variable
     * @return first upper bound type of given type variable
     */
    public static Type getFirstBound(TypeVariable<?> type) {
        Type[] bounds = type.getBounds();
        if (JieArray.isNotEmpty(bounds)) {
            return bounds[0];
        }
        return Object.class;
    }

    /**
     * Searches and returns field of specified name from given type, returns {@code null} for searching failed. This
     * method is equivalent to {@link #getField(Type, String, boolean)}:
     * <pre>
     *     return getField(type, name, true);
     * </pre>
     *
     * @param type given type
     * @param name specified field name
     * @return field of specified name from given type
     */
    @Nullable
    public static Field getField(Type type, String name) {
        return getField(type, name, true);
    }

    /**
     * Searches and returns field of specified name from given type.
     * <p>
     * The searching in order of {@link Class#getField(String)} then {@link Class#getDeclaredField(String)}. if
     * {@code searchSuper} is true, and if searching failed in current type, this method will recursively call
     * {@link Class#getGenericSuperclass()} to search super types, if still not found, recursively call
     * {@link Class#getGenericInterfaces()}.
     * <p>
     * Returns {@code null} for searching failed.
     *
     * @param type        given type
     * @param name        specified field name
     * @param searchSuper whether recursively searches super types
     * @return field of specified name from given type
     */
    @Nullable
    public static Field getField(Type type, String name, boolean searchSuper) {
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
                if (!searchSuper) {
                    return null;
                }
                while (true) {
                    Type curType = rawType.getGenericSuperclass();
                    if (curType == null) {
                        break;
                    }
                    Field f = getField(curType, name, true);
                    if (f != null) {
                        return f;
                    }
                }
                while (true) {
                    Type[] curTypes = rawType.getGenericInterfaces();
                    if (JieArray.isEmpty(curTypes)) {
                        break;
                    }
                    for (Type curType : curTypes) {
                        Field f = getField(curType, name, true);
                        if (f != null) {
                            return f;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Searches and returns method of specified name and parameter types from given type. returns {@code null} for
     * searching failed. This method is equivalent to {@link #getMethod(Type, String, Class[], boolean)}:
     * <pre>
     *     return getMethod(type, name, parameterTypes, true);
     * </pre>
     *
     * @param type           given type
     * @param name           specified field name
     * @param parameterTypes specified parameter types
     * @return method of specified name and parameter types from given type
     */
    @Nullable
    public static Method getMethod(Type type, String name, Class<?>[] parameterTypes) {
        return getMethod(type, name, parameterTypes, true);
    }

    /**
     * Searches and returns method of specified name and parameter types from given type.
     * <p>
     * The searching in order of {@link Class#getMethod(String, Class[])} then
     * {@link Class#getDeclaredMethod(String, Class[])}.if {@code searchSuper} is true, and if searching failed in
     * current type, this method will recursively call {@link Class#getGenericSuperclass()} to search super types, if
     * still not found, recursively call {@link Class#getGenericInterfaces()}.
     * <p>
     * Returns {@code null} for searching failed.
     *
     * @param type           given type
     * @param name           specified field name
     * @param parameterTypes specified parameter types
     * @param searchSuper    whether recursively searches super types
     * @return method of specified name and parameter types from given type
     */
    @Nullable
    public static Method getMethod(Type type, String name, Class<?>[] parameterTypes, boolean searchSuper) {
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
                if (!searchSuper) {
                    return null;
                }
                while (true) {
                    Type curType = rawType.getGenericSuperclass();
                    if (curType == null) {
                        break;
                    }
                    Method m = getMethod(curType, name, parameterTypes, true);
                    if (m != null) {
                        return m;
                    }
                }
                while (true) {
                    Type[] curTypes = rawType.getGenericInterfaces();
                    if (JieArray.isEmpty(curTypes)) {
                        break;
                    }
                    for (Type curType : curTypes) {
                        Method m = getMethod(curType, name, parameterTypes, true);
                        if (m != null) {
                            return m;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns new instance for given class name.
     * <p>
     * This method first uses {@link Class#forName(String)} to load given class, then call {@link #newInstance(Class)}
     * to create instance.
     * <p>
     * Returns {@code null} if failed.
     *
     * @param className given class name
     * @param <T>       type of result
     * @return a new instance of given class name with empty constructor or null
     */
    @Nullable
    public static <T> T newInstance(String className) {
        try {
            Class<?> cls = Class.forName(className);
            return newInstance(cls);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Creates a new instance of given type with empty constructor, may be {@code null} if failed.
     *
     * @param type given type
     * @param <T>  type of instance
     * @return a new instance of given type with empty constructor or null
     */
    @Nullable
    public static <T> T newInstance(Class<?> type) {
        try {
            Constructor<?> constructor = type.getConstructor();
            return Jie.as(constructor.newInstance());
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
     * Returns array class of given component type with specified class loader.
     *
     * @param componentType given component type
     * @param classLoader   specified class loader
     * @return array class of given component type
     */
    public static Class<?> arrayClass(Type componentType, ClassLoader classLoader) {
        if (componentType instanceof Class) {
            String name = arrayClassName((Class<?>) componentType);
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
            StringBuilder name = new StringBuilder();
            Type cur = componentType;
            do {
                name.append("[");
                if (cur instanceof GenericArrayType) {
                    cur = ((GenericArrayType) cur).getGenericComponentType();
                } else if (cur instanceof Class) {
                    name.append("L").append(((Class<?>) cur).getName()).append(";");
                    break;
                } else if (cur instanceof ParameterizedType) {
                    name.append("L").append(((Class<?>) ((ParameterizedType) cur).getRawType()).getName()).append(";");
                    break;
                } else {
                    throw new IllegalArgumentException("Unsupported component type: " + componentType);
                }
            } while (true);
            try {
                return Class.forName(name.toString(), true, classLoader);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }
        throw new UnsupportedOperationException("Unsupported component type: " + componentType);
    }

    private static String arrayClassName(Class<?> componentType) {
        String name;
        if (componentType.isArray()) {
            name = "[" + componentType.getName();
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
            name = "[L" + componentType.getName() + ";";
        }
        return name;
    }

    /**
     * Returns wrapper class if given class is primitive, else return itself.
     *
     * @param cls given class
     * @return wrapper class if given class is primitive, else return itself
     */
    public static Class<?> wrapper(Class<?> cls) {
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
     * Returns whether current runtime exists the class specified by given class name.
     *
     * @param className given class name
     * @return whether current runtime exists the class specified by given class name
     */
    public static boolean classExists(String className) {
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * Returns whether a type can be assigned by another type.
     * This method is {@link Type} version of {@link Class#isAssignableFrom(Class)}, supporting {@link Class},
     * {@link ParameterizedType}, {@link WildcardType}, {@link TypeVariable} and {@link GenericArrayType}.
     *
     * @param assigned the type to be assigned
     * @param assignee the assignee type
     * @return whether a type can be assigned by another type
     */
    public static boolean isAssignable(Type assigned, Type assignee) {
        return TypePattern.defaultPattern().isAssignable(assigned, assignee);
    }

    /**
     * Return a list to describe the generalized representation of the given type from the specified raw type, each
     * element of the list is actual type argument type of the raw type on given type. For example, for the types:
     * <pre>
     *     private static interface Z&lt;B, T, U, R&gt; {}
     *     private static class ZS implements Z&lt;String, Integer, Long, Boolean&gt; {}
     * </pre>
     * The result of this method:
     * <pre>
     *     getActualTypeArguments(ZS.class, Z.class);
     * </pre>
     * will be:
     * <pre>
     *     [String.class, Integer.class, Long.class, Boolean.class]
     * </pre>
     * Typically, the given type is same with or subtype of the specified raw type, and it returns an empty list if
     * failed.
     *
     * @param type    given type
     * @param rawType specified raw type
     * @return a list to describe the generalized representation of the given type from the specified raw type
     */
    public static List<Type> getActualTypeArguments(Type type, Class<?> rawType) {
        boolean supportedType = false;
        if (type instanceof Class<?>) {
            supportedType = true;
            Class<?> subType = (Class<?>) type;
            if (!rawType.isAssignableFrom(subType)) {
                return Collections.emptyList();
            }
        }
        if (type instanceof ParameterizedType) {
            supportedType = true;
            ParameterizedType subType = (ParameterizedType) type;
            Class<?> subRawType = (Class<?>) subType.getRawType();
            if (!rawType.isAssignableFrom(subRawType)) {
                return Collections.emptyList();
            }
        }
        if (!supportedType) {
            return Collections.emptyList();
        }
        TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
        if (JieArray.isEmpty(typeParameters)) {
            return Collections.emptyList();
        }
        Map<TypeVariable<?>, Type> typeArguments = getTypeParameterMapping(type);
        Set<Type> stack = new HashSet<>();
        return Arrays.stream(typeParameters)
            .map(it -> {
                Type nestedValue = JieColl.getNested(typeArguments, it, stack);
                stack.clear();
                return nestedValue == null ? it : nestedValue;
            }).collect(Collectors.toList());
    }

    /**
     * Returns a type parameters mapping for given type, the key of mapping is type parameter, and the value is actual
     * type argument or inherited type parameter. For example, for these types:
     * <pre>
     *     private static class X extends Y&lt;Integer, Long&gt;{}
     *     private static class Y&lt;K, V&gt; implements Z&lt;Float, Double, V&gt; {}
     *     private static interface Z&lt;T, U, R&gt;{}
     * </pre>
     * The result of this method
     * <pre>
     *     getTypeParameterMapping(x.class)
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
    @Immutable
    public static Map<TypeVariable<?>, Type> getTypeParameterMapping(Type type) {
        return TYPE_PARAMETER_MAPPING_CACHE.compute(type, it -> {
            Map<TypeVariable<?>, Type> result = new HashMap<>();
            getTypeParameterMapping(type, result);
            return Collections.unmodifiableMap(result);
        });
    }

    private static void getTypeParameterMapping(Type type, @OutParam Map<TypeVariable<?>, Type> mapping) {
        if (type instanceof Class) {
            Class<?> cur = (Class<?>) type;
            while (true) {
                Type superType = cur.getGenericSuperclass();
                if (superType != null) {
                    mappingActualTypeArgument(superType, mapping);
                }
                Type[] superTypes = cur.getGenericInterfaces();
                mappingInterfaceActualTypeArguments(superTypes, mapping);
                cur = cur.getSuperclass();
                if (cur == null) {
                    return;
                }
            }
        }
        if (type instanceof ParameterizedType) {
            mappingActualTypeArgument(type, mapping);
            getTypeParameterMapping(((ParameterizedType) type).getRawType(), mapping);
        }
    }

    private static void mappingInterfaceActualTypeArguments(Type[] interfaceTypes, @OutParam Map<TypeVariable<?>, Type> mapping) {
        if (JieArray.isEmpty(interfaceTypes)) {
            return;
        }
        for (Type interfaceType : interfaceTypes) {
            mappingActualTypeArgument(interfaceType, mapping);
            Class<?> interfaceClass = getRawType(interfaceType);
            if (interfaceClass != null) {
                Type[] superInterfaces = interfaceClass.getGenericInterfaces();
                mappingInterfaceActualTypeArguments(superInterfaces, mapping);
            }
        }
    }

    private static void mappingActualTypeArgument(Type type, @OutParam Map<TypeVariable<?>, Type> mapping) {
        if (!(type instanceof ParameterizedType)) {
            return;
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Class<?> rawClass = (Class<?>) parameterizedType.getRawType();
        TypeVariable<?>[] typeParameters = rawClass.getTypeParameters();
        for (int i = 0; i < typeParameters.length; i++) {
            if (i >= actualTypeArguments.length) {
                continue;
            }
            TypeVariable<?> typeVariable = typeParameters[i];
            Type actualTypeArgument = actualTypeArguments[i];
            mapping.put(typeVariable, actualTypeArgument);
        }
    }

    /**
     * Replaces the types in given {@code type} (including itself) which equals to {@code matcher} with
     * {@code replacement}. This method supports:
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
     * If the {@code deep} parameter is true, this method will recursively resolve unmatched types to replace.
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
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            if (Objects.equals(rawType, matcher)) {
                matched = true;
                rawType = (Class<?>) replacement;
            } else if (deep) {
                Type newRawType = replaceType(rawType, matcher, replacement, true);
                if (!Objects.equals(rawType, newRawType)) {
                    matched = true;
                    rawType = (Class<?>) newRawType;
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
                return JieType.parameterized(rawType, actualTypeArguments, ownerType);
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
                return JieType.wildcard(upperBounds, lowerBounds);
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
                return JieType.array(componentType);
            } else {
                return type;
            }
        }
        return type;
    }
}
