package xyz.fslabo.common.reflect;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.annotations.OutParam;
import xyz.fslabo.common.base.GekString;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.cache.Cache;
import xyz.fslabo.common.collect.JieArray;
import xyz.fslabo.common.collect.JieColl;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utilities for reflection.
 *
 * @author fredsuvn
 */
public class JieReflect {

    private static final Type[] EMPTY_TYPE_ARRAY = {};
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
        int index = GekString.lastIndexOf(name, ".");
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
     * Returns first upper bound type of given type (? extends).
     * Note that if no upper bound is explicitly declared, return {@code Object.class}.
     *
     * @param type given type
     * @return first upper bound type of given type
     */
    public static Type getUpperBound(WildcardType type) {
        Type[] upperBounds = type.getUpperBounds();
        if (JieArray.isNotEmpty(upperBounds)) {
            return upperBounds[0];
        }
        return Object.class;
    }

    /**
     * Returns first upper bound type of given type (T extends).
     * Note that if no upper bound is explicitly declared, return {@code Object.class}.
     *
     * @param type given type
     * @return first upper bound type of given type
     */
    public static Type getUpperBound(TypeVariable<?> type) {
        Type[] bounds = type.getBounds();
        if (JieArray.isNotEmpty(bounds)) {
            return bounds[0];
        }
        return Object.class;
    }

    /**
     * Returns upper bound of given type:
     * <ul>
     *     <li>
     *         If given type is instance of {@link Class}, return itself;
     *     </li>
     *     <li>
     *         If given type is instance of {@link WildcardType}, call {@link #getUpperBounds(WildcardType)};
     *     </li>
     *     <li>
     *         If given type is instance of {@link TypeVariable}, call {@link #getUpperBounds(TypeVariable)};
     *     </li>
     *     <li>
     *         If given type is instance of {@link GenericArrayType}, return a type of which component type is upper
     *         bound of {@link GenericArrayType#getGenericComponentType()};
     *     </li>
     *     <li>
     *         If given type is instance of {@link ParameterizedType}, return a type of which actual type arguments are
     *         upper bounds of {@link ParameterizedType#getActualTypeArguments()};
     *     </li>
     * </ul>
     *
     * @param type given type
     * @return upper bound of given type
     */
    public static Type getUpperBound(Type type) {
        if (type instanceof Class) {
            return type;
        }
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            return getUpperBound(wildcardType);
        }
        if (type instanceof TypeVariable) {
            TypeVariable<?> typeVariable = (TypeVariable<?>) type;
            return getUpperBound(typeVariable);
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) type;
            Type componentType = genericArrayType.getGenericComponentType();
            Type upperComponentType = getUpperBound(componentType);
            if (Objects.equals(componentType, upperComponentType)) {
                return type;
            }
            return JieType.arrayType(upperComponentType);
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] arguments = parameterizedType.getActualTypeArguments();
            if (JieArray.isEmpty(arguments)) {
                return type;
            }
            Type[] upperArgs = arguments.clone();
            for (int i = 0; i < arguments.length; i++) {
                Type upper = getUpperBound(arguments[i]);
                upperArgs[i] = upper;
            }
            if (Arrays.equals(arguments, upperArgs)) {
                return type;
            }
            return JieType.paramType(parameterizedType.getRawType(), parameterizedType.getOwnerType(), upperArgs);
        }
        throw new UnsupportedOperationException("Unknown type: " + type);
    }

    /**
     * Returns upper bounds of given type (? extends).
     * Note that if no upper bound is explicitly declared, return an empty array.
     *
     * @param type given type
     * @return upper bounds of given type
     */
    public static Type[] getUpperBounds(WildcardType type) {
        Type[] bounds = type.getUpperBounds();
        return Jie.orDefault(bounds, EMPTY_TYPE_ARRAY);
    }

    /**
     * Returns upper bounds of given type (T extends).
     * Note that if no upper bound is explicitly declared, return an empty array.
     *
     * @param type given type
     * @return upper bounds of given type
     */
    public static Type[] getUpperBounds(TypeVariable<?> type) {
        Type[] bounds = type.getBounds();
        return Jie.orDefault(bounds, EMPTY_TYPE_ARRAY);
    }

    /**
     * Returns first lower bound type of given type (? super).
     * If given type has no lower bound, return null.
     *
     * @param type given type
     * @return first lower bound type of given type or null
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
     * Returns lower bound of given type:
     * <ul>
     *     <li>
     *         If given type is instance of {@link Class}, return itself;
     *     </li>
     *     <li>
     *         If given type is instance of {@link WildcardType}, call {@link #getLowerBound(WildcardType)};
     *     </li>
     *     <li>
     *         If given type is instance of {@link TypeVariable}, return null;
     *     </li>
     *     <li>
     *         If given type is instance of {@link GenericArrayType}, return a type of which component type is lower
     *         bound (including null) of {@link GenericArrayType#getGenericComponentType()};
     *     </li>
     *     <li>
     *         If given type is instance of {@link ParameterizedType}, return a type of which actual type arguments are
     *         lower bounds (including null) of {@link ParameterizedType#getActualTypeArguments()};
     *     </li>
     * </ul>
     *
     * @param type given type
     * @return lower bound of given type
     */
    @Nullable
    private static Type getLowerBound(Type type) {
        if (type instanceof Class) {
            return type;
        }
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            return getLowerBound(wildcardType);
        }
        if (type instanceof TypeVariable) {
            return null;
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) type;
            Type componentType = genericArrayType.getGenericComponentType();
            Type lowerComponentType = getLowerBound(componentType);
            if (Objects.equals(componentType, lowerComponentType)) {
                return type;
            }
            return JieType.arrayType(lowerComponentType);
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] arguments = parameterizedType.getActualTypeArguments();
            if (JieArray.isEmpty(arguments)) {
                return type;
            }
            Type[] lowerArgs = arguments.clone();
            for (int i = 0; i < arguments.length; i++) {
                Type lower = getLowerBound(arguments[i]);
                lowerArgs[i] = lower;
            }
            if (Arrays.equals(arguments, lowerArgs)) {
                return type;
            }
            return JieType.paramType(parameterizedType.getRawType(), parameterizedType.getOwnerType(), lowerArgs);
        }
        throw new UnsupportedOperationException("Unknown type: " + type);
    }

    /**
     * Returns lower bounds of given type (? super).
     * If given type has no lower bound, return null.
     *
     * @param type given type
     * @return lower bounds of given type or null
     */
    @Nullable
    public static Type[] getLowerBounds(WildcardType type) {
        Type[] bounds = type.getLowerBounds();
        return JieArray.isEmpty(bounds) ? null : bounds;
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
     * Returns new instance for given class name.
     * This method first uses {@link Class#forName(String)} to load given class,
     * then call {@link #newInstance(Class)} to create instance.
     * Return null if failed.
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
     * Returns array class of given component type.
     *
     * @param componentType given component type
     * @param classLoader   class loader
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
        throw new IllegalArgumentException("Unsupported component type: " + componentType);
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
     * Returns whether the give matched type matches the specified pattern.
     * <p>
     * If given matched type and specified pattern are equal, return true. Otherwise, it returns true if and only if
     * upper and lower bounds of given type within the bounds of specified pattern.
     * <p>
     * Not if given type is not {@link WildcardType} or {@link TypeVariable}, the upper and lower bounds are commonly
     * same with its raw class type.
     * For examples:
     * <pre>
     *     matches(Object.class, Object.class);// true
     *     matches(Object.class, String.class);// false: String is lower than Object
     *     matches(Integer.class, Long.class);// false: they have no inheritance relationship
     *     matches(int.class, int.class);// true
     *     matches(int.class, long.class);// false: they have no inheritance relationship
     *     matches(
     *         new TypeRef&lt;List&lt;? extends String&gt;&gt;() {}.getType(),
     *         new TypeRef&lt;List&lt;String&gt;&gt;() {}.getType()
     *     );// true
     *     matches(
     *         new TypeRef&lt;List&lt;? super Object&gt;&gt;() {}.getType(),
     *         new TypeRef&lt;List&lt;String&gt;&gt;() {}.getType()
     *     );// false: String out of bounds of "? super Object"
     *     matches(
     *         new TypeRef&lt;List&lt;? extends String&gt;&gt;() {}.getType(),
     *         new TypeRef&lt;Collection&lt;String&gt;&gt;() {}.getType()
     *     );// false: Collection is upper than List;
     *       // upper bound: List&lt;String&gt;, lower bound: List&lt;no--lower&gt;
     *     matches(
     *         new TypeRef&lt;Collection&lt;? extends String&gt;&gt;() {}.getType(),
     *         new TypeRef&lt;List&lt;String&gt;&gt;() {}.getType()
     *     );// false: List is lower than Collection;
     *       // upper bound: Collection&lt;String&gt;, lower bound: Collection&lt;no--lower&gt;
     * </pre>
     *
     * @param matched given type to be matched
     * @param pattern specified pattern
     * @return whether the give matched type matches the specified pattern
     */
    public static boolean matches(Type pattern, Type matched) {
        return TypePattern.matches(pattern, matched);
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
        return TypePattern.isAssignable(assigned, assignee);
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
    public static ParamType getGenericSuperType(Type type, Class<?> target) {
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
        if (JieArray.isEmpty(typeParameters)) {
            throw new IllegalArgumentException("Given \"to\" type doesn't have type parameter.");
        }
        Map<TypeVariable<?>, Type> typeArguments = getTypeParameterMapping(type);
        Set<Type> stack = new HashSet<>();
        List<Type> actualTypeArguments = Arrays.stream(typeParameters)
            .map(it -> {
                Type nestedValue = JieColl.getNested(typeArguments, it, stack);
                stack.clear();
                return nestedValue == null ? it : nestedValue;
            }).collect(Collectors.toList());
        return JieType.paramType(target, target.getDeclaringClass(), actualTypeArguments);
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
        return TYPE_PARAMETER_MAPPING_CACHE.compute(type, it -> {
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
        if (JieArray.isNotEmpty(superTypes)) {
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
                return JieType.paramType(rawType, ownerType, actualTypeArguments);
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
                return JieType.arrayType(componentType);
            } else {
                return type;
            }
        }
        return type;
    }
}
