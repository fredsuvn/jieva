package xyz.srclab.common.reflect;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.annotations.OutParam;
import xyz.srclab.common.base.FsArray;
import xyz.srclab.common.base.FsString;
import xyz.srclab.common.base.FsUnsafe;
import xyz.srclab.common.cache.FsCache;
import xyz.srclab.common.collect.FsCollect;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Type utilities.
 *
 * @author fredsuvn
 */
public class FsType {

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
     * Returns upper bound type of given type (? extends).
     * If given type has lower bounds (? super), return null.
     *
     * @param type given type
     */
    @Nullable
    public static Type getUpperBound(WildcardType type) {
        Type[] lowerBounds = type.getLowerBounds();
        if (FsArray.isNotEmpty(lowerBounds)) {
            return null;
        }
        Type[] upperBounds = type.getUpperBounds();
        if (FsArray.isEmpty(upperBounds)) {
            return Object.class;
        }
        return upperBounds[0];
    }

    /**
     * Returns upper bound type of given type (? super).
     * If given type has no lower bounds, return null.
     *
     * @param type given type
     */
    @Nullable
    public static Type getLowerBound(WildcardType type) {
        Type[] lowerBounds = type.getLowerBounds();
        if (FsArray.isEmpty(lowerBounds)) {
            return null;
        }
        return lowerBounds[0];
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
        if (t1.isPrimitive() || t2.isPrimitive()) {
            return toWrapperClass(t1).isAssignableFrom(toWrapperClass(t2));
        }
        return t1.isAssignableFrom(t2);
    }

    /**
     * Returns whether given type 1 can be assigned by given type 2.
     * If given type is primitive, it will be converted to its wrapper type.
     * This method is similar as {@link #isAssignableFrom(Class, Class)} but supports {@link Type}.
     * <p>
     * Note in this type, {@link Object} can be assigned from any type, {@link WildcardType} can not assign to any type.
     *
     * @param t1 given type 1
     * @param t2 given type 2
     */
    public static boolean isAssignableFrom(Type t1, Type t2) {
        if (Objects.equals(t1, t2) || Objects.equals(t1, Object.class)) {
            return true;
        }
        if (t2 instanceof WildcardType) {
            return false;
        }
        if (t1 instanceof Class<?>) {
            Class<?> c1 = (Class<?>) t1;
            if (t2 instanceof Class<?>) {
                Class<?> c2 = (Class<?>) t2;
                return isAssignableFrom(c1, c2);
            }
            if (t2 instanceof ParameterizedType) {
                ParameterizedType p2 = (ParameterizedType) t2;
                Class<?> r2 = getRawType(p2);
                return isAssignableFrom(c1, r2);
            }
            if (t2 instanceof TypeVariable<?>) {
                TypeVariable<?> v2 = (TypeVariable<?>) t2;
                Type[] bounds = v2.getBounds();
                for (Type bound : bounds) {
                    if (isAssignableFrom(t1, bound)) {
                        return true;
                    }
                }
                return false;
            }
            if (t2 instanceof GenericArrayType) {
                if (!c1.isArray()) {
                    return false;
                }
                GenericArrayType g2 = (GenericArrayType) t2;
                return isAssignableFrom(c1.getComponentType(), g2.getGenericComponentType());
            }
            return false;
        }
        if (t1 instanceof ParameterizedType) {
            ParameterizedType p1 = (ParameterizedType) t1;
            if (t2 instanceof Class<?>) {
                ParameterizedType p2 = getGenericSuperType(t2, getRawType(p1));
                if (p2 == null) {
                    return false;
                }
                return isAssignableFrom(p1, p2);
            }
            if (t2 instanceof ParameterizedType) {
                ParameterizedType p2 = (ParameterizedType) t2;
                if (Objects.equals(p1.getRawType(), p2.getRawType())) {
                    Type[] a1 = p1.getActualTypeArguments();
                    Type[] a2 = p2.getActualTypeArguments();
                    if (a1.length != a2.length) {
                        return false;
                    }
                    return isAssignableFromForParameterizedType(a1, a2);
                }
                ParameterizedType pp2 = getGenericSuperType(p2, getRawType(p1));
                if (pp2 == null) {
                    return false;
                }
                return isAssignableFrom(p1, pp2);
            }
            if (t2 instanceof TypeVariable<?>) {
                TypeVariable<?> v2 = (TypeVariable<?>) t2;
                Type[] bounds = v2.getBounds();
                for (Type bound : bounds) {
                    if (isAssignableFrom(t1, bound)) {
                        return true;
                    }
                }
                return false;
            }
            if (t2 instanceof GenericArrayType) {
                if (!c1.isArray()) {
                    return false;
                }
                GenericArrayType g2 = (GenericArrayType) t2;
                return isAssignableFrom(c1.getComponentType(), g2.getGenericComponentType());
            }
            return false;
        }
        return false;
    }

    private static boolean isAssignableFromForParameterizedType(Type[] p1, Type[] p2) {
        return false;
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
     * Note given type must be subtype of target type, if it is not, return null.
     *
     * @param type   given type
     * @param target target type
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
        if (FsArray.isEmpty(typeParameters)) {
            throw new IllegalArgumentException("Given \"to\" type doesn't have type parameter.");
        }
        Map<TypeVariable<?>, Type> typeArguments = getTypeParameterMapping(type);
        Set<Type> stack = new HashSet<>();
        List<Type> actualTypeArguments = Arrays.stream(typeParameters)
            .map(it -> {
                Type nestedValue = FsCollect.nestedGet(typeArguments, it, stack);
                stack.clear();
                return nestedValue == null ? it : nestedValue;
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
        if (FsArray.isNotEmpty(superTypes)) {
            for (Type superType : superTypes) {
                parseSuperGeneric(superType, typeMap);
                Class<?> superRaw = FsType.getRawType(superType);
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
                return parameterizedType(rawType, ownerType, actualTypeArguments);
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
                return wildcardType(upperBounds, lowerBounds);
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
                return genericArrayType(componentType);
            } else {
                return type;
            }
        }
        return type;
    }

    /**
     * Returns a ParameterizedType with given raw type, owner type and actual type arguments.
     *
     * @param rawType             given raw type
     * @param ownerType           given owner type
     * @param actualTypeArguments actual type arguments
     */
    public static ParameterizedType parameterizedType(
        Type rawType, @Nullable Type ownerType, Iterable<Type> actualTypeArguments) {
        return new FsParameterizedType(rawType, ownerType, FsCollect.toArray(actualTypeArguments, Type.class));
    }

    /**
     * Returns a ParameterizedType with given raw type and actual type arguments.
     *
     * @param rawType             given raw type
     * @param actualTypeArguments actual type arguments
     */
    public static ParameterizedType parameterizedType(Type rawType, Iterable<Type> actualTypeArguments) {
        return parameterizedType(rawType, null, actualTypeArguments);
    }

    /**
     * Returns a ParameterizedType with given raw type, owner type and actual type arguments.
     *
     * @param rawType             given raw type
     * @param ownerType           given owner type
     * @param actualTypeArguments actual type arguments
     */
    public static ParameterizedType parameterizedType(
        Type rawType, @Nullable Type ownerType, Type[] actualTypeArguments) {
        return new FsParameterizedType(rawType, ownerType, actualTypeArguments);
    }

    /**
     * Returns a ParameterizedType with given raw type and actual type arguments.
     *
     * @param rawType             given raw type
     * @param actualTypeArguments actual type arguments
     */
    public static ParameterizedType parameterizedType(Type rawType, Type[] actualTypeArguments) {
        return parameterizedType(rawType, null, actualTypeArguments);
    }

    /**
     * Returns a WildcardType with given upper bounds and lower bounds.
     *
     * @param upperBounds given upper bounds
     * @param lowerBounds given lower bounds
     */
    public static WildcardType wildcardType(
        @Nullable Iterable<Type> upperBounds, @Nullable Iterable<Type> lowerBounds) {
        return new FsWildcardType(
            upperBounds == null ? null : FsCollect.toArray(upperBounds, Type.class),
            lowerBounds == null ? null : FsCollect.toArray(lowerBounds, Type.class)
        );
    }

    /**
     * Returns a WildcardType with given upper bounds and lower bounds.
     *
     * @param upperBounds given upper bounds
     * @param lowerBounds given lower bounds
     */
    public static WildcardType wildcardType(
        @Nullable Type[] upperBounds, @Nullable Type[] lowerBounds) {
        return new FsWildcardType(upperBounds, lowerBounds);
    }

    /**
     * Returns a GenericArrayType with given component type.
     *
     * @param componentType given component type
     */
    public static GenericArrayType genericArrayType(Type componentType) {
        return new FsGenericArrayType(componentType);
    }

    private static final class FsParameterizedType implements ParameterizedType {

        private final Type rawType;
        private final @Nullable Type ownerType;
        private final Type[] actualTypeArguments;

        private FsParameterizedType(Type rawType, @Nullable Type ownerType, Type[] actualTypeArguments) {
            this.rawType = rawType;
            this.ownerType = ownerType == null ?
                ((rawType instanceof Class) ? ((Class<?>) rawType).getDeclaringClass() : null) : ownerType;
            this.actualTypeArguments = actualTypeArguments;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return actualTypeArguments.clone();
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o instanceof FsParameterizedType) {
                FsParameterizedType other = (FsParameterizedType) o;
                return Objects.equals(rawType, other.rawType)
                    && Objects.equals(ownerType, other.ownerType)
                    && Arrays.equals(actualTypeArguments, other.actualTypeArguments);
            }
            if (o instanceof ParameterizedType) {
                ParameterizedType other = (ParameterizedType) o;
                return Objects.equals(rawType, other.getRawType())
                    && Objects.equals(ownerType, other.getOwnerType())
                    && Arrays.equals(actualTypeArguments, other.getActualTypeArguments());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(this.actualTypeArguments)
                ^ (this.ownerType == null ? 0 : this.ownerType.hashCode())
                ^ (this.rawType == null ? 0 : this.rawType.hashCode());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (ownerType != null) {
                //test.A<String>
                sb.append(ownerType.getTypeName());
                //test.A$B
                String rawTypeName = rawType.getTypeName();
                //test.A
                String ownerRawTypeName;
                if (ownerType instanceof ParameterizedType) {
                    ownerRawTypeName = ((ParameterizedType) ownerType).getRawType().getTypeName();
                } else {
                    ownerRawTypeName = ownerType.getTypeName();
                }
                //test.A$B -> $B
                String lastName = FsString.replace(rawTypeName, ownerRawTypeName, "");
                //test.A<String>$B
                sb.append(lastName);
            } else {
                sb.append(rawType.getTypeName());
            }
            if (FsArray.isNotEmpty(actualTypeArguments)) {
                sb.append("<");
                boolean first = true;
                for (Type t : actualTypeArguments) {
                    if (!first) {
                        sb.append(", ");
                    }
                    if (t instanceof Class) {
                        sb.append(((Class<?>) t).getName());
                    } else {
                        sb.append(t.toString());
                    }
                    first = false;
                }
                sb.append(">");
            }
            return sb.toString();
        }
    }

    private static final class FsWildcardType implements WildcardType {

        private final Type[] upperBounds;
        private final Type[] lowerBounds;

        private FsWildcardType(@Nullable Type[] upperBounds, @Nullable Type[] lowerBounds) {
            this.upperBounds = FsArray.isEmpty(upperBounds) ? new Type[]{Object.class} : upperBounds;
            this.lowerBounds = FsArray.isEmpty(lowerBounds) ? new Type[0] : lowerBounds;
        }

        @Override
        public Type[] getUpperBounds() {
            return upperBounds.clone();
        }

        @Override
        public Type[] getLowerBounds() {
            return lowerBounds.clone();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o instanceof FsWildcardType) {
                FsWildcardType other = (FsWildcardType) o;
                return Arrays.equals(upperBounds, other.upperBounds)
                    && Arrays.equals(lowerBounds, other.lowerBounds);
            }
            if (o instanceof WildcardType) {
                WildcardType other = (WildcardType) o;
                return Arrays.equals(upperBounds, other.getUpperBounds())
                    && Arrays.equals(lowerBounds, other.getLowerBounds());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(upperBounds) ^ Arrays.hashCode(lowerBounds);
        }

        @Override
        public String toString() {
            StringBuilder builder;
            Type[] bounds;
            if (lowerBounds.length == 0) {
                if (upperBounds.length == 0 || Objects.equals(Object.class, upperBounds[0])) {
                    return "?";
                }

                bounds = upperBounds;
                builder = new StringBuilder("? extends ");
            } else {
                bounds = lowerBounds;
                builder = new StringBuilder("? super ");
            }
            for (int i = 0; i < bounds.length; ++i) {
                if (i > 0) {
                    builder.append(" & ");
                }

                builder.append(bounds[i] instanceof Class ? ((Class<?>) bounds[i]).getName() : bounds[i].toString());
            }
            return builder.toString();
        }
    }

    private static final class FsGenericArrayType implements GenericArrayType {

        private final Type componentType;

        private FsGenericArrayType(Type componentType) {
            this.componentType = componentType;
        }

        @Override
        public Type getGenericComponentType() {
            return componentType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o instanceof GenericArrayType) {
                GenericArrayType other = (GenericArrayType) o;
                return Objects.equals(componentType, other.getGenericComponentType());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(componentType);
        }

        @Override
        public String toString() {
            Type type = this.getGenericComponentType();
            StringBuilder builder = new StringBuilder();
            if (type instanceof Class) {
                builder.append(((Class<?>) type).getName());
            } else {
                builder.append(type.toString());
            }
            builder.append("[]");
            return builder.toString();
        }
    }
}
