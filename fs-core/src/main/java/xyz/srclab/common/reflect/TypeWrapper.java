package xyz.srclab.common.reflect;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.FsUnsafe;
import xyz.srclab.common.cache.FsCache;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Wrapper for {@link Type}, is used to get type info conveniently and efficiently.
 * <p>
 * This class overrides the {@link #equals(Object)} and {@link #hashCode()} with wrapped type, that means,
 * wrappers have same wrapped types is equals for each other and have same hashcode
 * (so the wrapper can be the key of Map).
 *
 * @author fredsuvn
 */
public abstract class TypeWrapper<T extends Type> {

    private static final FsCache<TypeWrapper<?>> TYPE_WRAPPER_CACHE =
        FsUnsafe.ForCache.getOrCreateCache(FsUnsafe.ForCache.TYPE_WRAPPER);

    /**
     * Returns TypeWrapper from given type. The type must be in:
     * <ul>
     *     <li>{@link ParameterizedType}</li>
     *     <li>{@link WildcardType}</li>
     *     <li>{@link TypeVariable}</li>
     *     <li>{@link GenericArrayType}</li>
     * </ul>
     * Different between this method and {@link #newWrapper(Type)} is:
     * the {@link #newWrapper(Type)} always returns new instance,
     * but this method cached the value and returns cached value if it still exists.
     *
     * @param type given type
     * @see #newWrapper(Type)
     */
    public static <T extends Type> TypeWrapper<T> from(T type) {
        return (TypeWrapper<T>) TYPE_WRAPPER_CACHE.get(type, TypeWrapper::newWrapper);
    }

    /**
     * Returns a new TypeWrapper with given type. The type must be in:
     * <ul>
     *     <li>{@link ParameterizedType}</li>
     *     <li>{@link WildcardType}</li>
     *     <li>{@link TypeVariable}</li>
     *     <li>{@link GenericArrayType}</li>
     * </ul>
     *
     * @param type given type
     * @see #from(Type)
     */
    public static <T extends Type> TypeWrapper<T> newWrapper(T type) {
        if (type instanceof ParameterizedType) {
            return (TypeWrapper<T>) new ParameterizedTypeWrapper((ParameterizedType) type);
        }
        if (type instanceof WildcardType) {
            return (TypeWrapper<T>) new WildcardTypeWrapper((WildcardType) type);
        }
        if (type instanceof TypeVariable) {
            return (TypeWrapper<T>) new TypeVariableWrapper((TypeVariable) type);
        }
        if (type instanceof GenericArrayType) {
            return (TypeWrapper<T>) new GenericArrayTypeWrapper((GenericArrayType) type);
        }
        throw new IllegalArgumentException("Unsupported type: " + type + ".");
    }

    /**
     * Returns wrapped type.
     */
    public abstract T getType();

    /**
     * Returns type name of wrapped type.
     */
    public String getTypeName() {
        return getType().getTypeName();
    }

    /**
     * Returns actual type arguments of wrapped type.
     * This method is only supported for {@link ParameterizedType}.
     */
    public List<Type> getActualTypeArguments() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns actual type argument at specified index of wrapped type.
     * This method is only supported for {@link ParameterizedType}.
     *
     * @param index specified index
     */
    public Type getActualTypeArgument(int index) {
        return getActualTypeArguments().get(index);
    }

    /**
     * Returns raw type of wrapped type.
     * This method is only supported for {@link ParameterizedType}.
     */
    public Class<?> getRawType() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns owner type of wrapped type.
     * This method is only supported for {@link ParameterizedType}.
     */
    public Type getOwnerType() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns upper bound (? extends) of wrapped type.
     * This method is only supported for {@link WildcardType}.
     * If the type has lower bound, return null.
     */
    @Nullable
    public Type getUpperBound() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns lower bound (? super) of wrapped type.
     * This method is only supported for {@link WildcardType}.
     * If the type has no lower bound, return null.
     */
    @Nullable
    public Type getLowerBound() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns type variable upper bounds ({@link TypeVariable#getBounds()}) of wrapped type.
     * This method is only supported for {@link TypeVariable}.
     */
    public List<Type> getTypeVariableBounds() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns type variable upper bound ({@link TypeVariable#getBounds()}) at specified index of wrapped type.
     * This method is only supported for {@link TypeVariable}.
     *
     * @param index specified index
     */
    public Type getTypeVariableBound(int index) {
        return getTypeVariableBounds().get(index);
    }

    /**
     * Returns type variable name ({@link TypeVariable#getName()}) of wrapped type.
     * This method is only supported for {@link TypeVariable}.
     */
    public String getTypeVariableName() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns type variable annotated bounds ({@link TypeVariable#getAnnotatedBounds()}) of wrapped type.
     * This method is only supported for {@link TypeVariable}.
     */
    public List<AnnotatedType> getTypeVariableAnnotatedBounds() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns type variable annotated bound ({@link TypeVariable#getAnnotatedBounds()})
     * at specified index of wrapped type.
     * This method is only supported for {@link TypeVariable}.
     *
     * @param index specified index
     */
    public AnnotatedType getTypeVariableAnnotatedBound(int index) {
        return getTypeVariableAnnotatedBounds().get(index);
    }

    /**
     * Returns component type of wrapped type.
     * This method is only supported for {@link GenericArrayType}.
     */
    public Type getGenericComponentType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        return getType().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!Objects.equals(getClass(), obj.getClass())) {
            return false;
        }
        TypeWrapper<?> wrapper = (TypeWrapper<?>) obj;
        return Objects.equals(getType(), wrapper.getType());
    }

    @Override
    public String toString() {
        return "TypeWrapper(" + getType().getTypeName() + ")";
    }

    private static final class ParameterizedTypeWrapper extends TypeWrapper<ParameterizedType> {

        private final ParameterizedType type;
        private final List<Type> actualTypeArguments;

        private ParameterizedTypeWrapper(ParameterizedType type) {
            this.type = type;
            this.actualTypeArguments = Collections.unmodifiableList(Arrays.asList(type.getActualTypeArguments()));
        }

        @Override
        public ParameterizedType getType() {
            return type;
        }

        @Override
        public List<Type> getActualTypeArguments() {
            return actualTypeArguments;
        }

        @Override
        public Class<?> getRawType() {
            return (Class<?>) type.getRawType();
        }

        @Override
        public Type getOwnerType() {
            return type.getOwnerType();
        }
    }

    private static final class WildcardTypeWrapper extends TypeWrapper<WildcardType> {

        private final WildcardType type;
        private final Type upper;
        private final Type lower;

        private WildcardTypeWrapper(WildcardType type) {
            this.type = type;
            this.upper = FsType.getUpperBound(type);
            this.lower = FsType.getLowerBound(type);
        }

        @Override
        public WildcardType getType() {
            return type;
        }

        @Override
        public Type getUpperBound() {
            return upper;
        }

        @Override
        public Type getLowerBound() {
            return lower;
        }
    }

    private static final class TypeVariableWrapper<D extends GenericDeclaration> extends TypeWrapper<TypeVariable<D>> {

        private final TypeVariable<D> type;
        private final List<Type> bounds;
        private final List<AnnotatedType> annotatedBounds;

        private TypeVariableWrapper(TypeVariable<D> type) {
            this.type = type;
            this.bounds = Collections.unmodifiableList(Arrays.asList(type.getBounds()));
            this.annotatedBounds = Collections.unmodifiableList(Arrays.asList(type.getAnnotatedBounds()));
        }

        @Override
        public TypeVariable<D> getType() {
            return type;
        }

        @Override
        public List<Type> getTypeVariableBounds() {
            return bounds;
        }

        @Override
        public String getTypeVariableName() {
            return type.getName();
        }

        @Override
        public List<AnnotatedType> getTypeVariableAnnotatedBounds() {
            return annotatedBounds;
        }
    }

    private static final class GenericArrayTypeWrapper extends TypeWrapper<GenericArrayType> {

        private final GenericArrayType type;

        private GenericArrayTypeWrapper(GenericArrayType type) {
            this.type = type;
        }

        @Override
        public GenericArrayType getType() {
            return type;
        }

        @Override
        public Type getGenericComponentType() {
            return type.getGenericComponentType();
        }
    }
}
