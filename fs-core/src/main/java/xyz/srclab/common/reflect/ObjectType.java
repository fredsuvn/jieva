package xyz.srclab.common.reflect;

import lombok.EqualsAndHashCode;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.collect.FsCollect;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

/**
 * Denotes an object and its specified type.
 *
 * @author fredsuvn
 */
@EqualsAndHashCode
public class ObjectType<T> {

    /**
     * Returns a new ObjectType with given object and its specified type.
     *
     * @param object given object
     * @param type   specified type
     */
    public static <T> ObjectType<T> of(T object, Type type) {
        if (type instanceof Class) {
            return new ObjectType<>(object, type);
        }
        if (type instanceof ParameterizedType) {
            return new OfParameterizedType<>(object, (ParameterizedType) type);
        }
        if (type instanceof WildcardType) {
            return new OfWildcardType<>(object, (WildcardType) type);
        }
        if (type instanceof TypeVariable) {
            return new OfTypeVariable<>(object, (TypeVariable<?>) type);
        }
        if (type instanceof GenericArrayType) {
            return new OfGenericArrayType<>(object, (GenericArrayType) type);
        }
        throw new IllegalArgumentException(
            "Specified type must be one of Class, ParameterizedType, WildcardType, TypeVariable or GenericArrayType.");
    }

    private final T object;
    private final Type type;

    /**
     * Constructs with given object and its specified type.
     *
     * @param object given object
     * @param type   specified type
     */
    protected ObjectType(@Nullable T object, Type type) {
        this.object = object;
        this.type = type;
    }

    /**
     * Returns object.
     */
    public T getObject() {
        return object;
    }

    /**
     * Returns specified type of object.
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns specified type of object as {@link Class}.
     * <p>
     * This method only supports specified type which is {@link Class}.
     */
    public Class<?> getClassType() {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        throw new IllegalStateException("Specified type is not Class.");
    }

    /**
     * Returns specified type of object as {@link ParameterizedType}.
     * <p>
     * This method only supports specified type which is {@link ParameterizedType}.
     */
    public ParameterizedType getParameterizedType() {
        if (type instanceof ParameterizedType) {
            return (ParameterizedType) type;
        }
        throw new IllegalStateException("Specified type is not ParameterizedType.");
    }

    /**
     * Returns raw type of specified type.
     * <p>
     * This method only supports specified type which is {@link ParameterizedType}.
     */
    public Class<?> getRawType() {
        return (Class<?>) getParameterizedType().getRawType();
    }

    /**
     * Returns owner type of specified type.
     * <p>
     * This method only supports specified type which is {@link ParameterizedType}.
     */
    public Type getOwnerType() {
        return getParameterizedType().getOwnerType();
    }

    /**
     * Returns actual type arguments of specified type.
     * <p>
     * This method only supports specified type which is {@link ParameterizedType}.
     */
    public List<Type> getActualTypeArguments() {
        throw new IllegalStateException("Specified type is not WildcardType.");
    }

    /**
     * Returns actual type argument of specified type at specified index.
     * <p>
     * This method only supports specified type which is {@link ParameterizedType}.
     *
     * @param index specified index
     */
    public Type getActualTypeArgument(int index) {
        return getActualTypeArguments().get(index);
    }

    /**
     * Returns specified type of object as {@link WildcardType}.
     * <p>
     * This method only supports specified type which is {@link WildcardType}.
     */
    public WildcardType getWildcardType() {
        if (type instanceof WildcardType) {
            return (WildcardType) type;
        }
        throw new IllegalStateException("Specified type is not WildcardType.");
    }

    /**
     * Returns upper bound (? extends) of specified type.
     * If the type has lower bound, return null.
     * <p>
     * This method only supports specified type which is {@link WildcardType}.
     */
    @Nullable
    public Type getUpperBound() {
        throw new IllegalStateException("Specified type is not WildcardType.");
    }

    /**
     * Returns lower bound (? super) of specified type.
     * If the type has no lower bound, return null.
     * <p>
     * This method only supports specified type which is {@link WildcardType}.
     */
    @Nullable
    public Type getLowerBound() {
        throw new IllegalStateException("Specified type is not WildcardType.");
    }

    /**
     * Returns specified type of object as {@link TypeVariable}.
     * <p>
     * This method only supports specified type which is {@link TypeVariable}.
     */
    public TypeVariable<?> getTypeVariable() {
        if (type instanceof TypeVariable) {
            return (TypeVariable<?>) type;
        }
        throw new IllegalStateException("Specified type is not TypeVariable.");
    }

    /**
     * Returns type variable upper bounds ({@link TypeVariable#getBounds()}) of specified type.
     * <p>
     * This method only supports specified type which is {@link TypeVariable}.
     */
    public List<Type> getTypeVariableBounds() {
        throw new IllegalStateException("Specified type is not TypeVariable.");
    }

    /**
     * Returns type variable upper bound ({@link TypeVariable#getBounds()}) of specified type at specified index.
     * <p>
     * This method only supports specified type which is {@link TypeVariable}.
     *
     * @param index specified index
     */
    public Type getTypeVariableBound(int index) {
        return getTypeVariableBounds().get(index);
    }

    /**
     * Returns type variable name ({@link TypeVariable#getName()}) of specified type.
     * <p>
     * This method only supports specified type which is {@link TypeVariable}.
     */
    public String getTypeVariableName() {
        return getTypeVariable().getName();
    }

    /**
     * Returns type variable annotated bounds ({@link TypeVariable#getAnnotatedBounds()}) of specified type.
     * <p>
     * This method only supports specified type which is {@link TypeVariable}.
     */
    public List<AnnotatedType> getTypeVariableAnnotatedBounds() {
        throw new IllegalStateException("Specified type is not TypeVariable.");
    }

    /**
     * Returns type variable annotated bound ({@link TypeVariable#getAnnotatedBounds()}) of specified type
     * at specified index.
     * <p>
     * This method only supports specified type which is {@link TypeVariable}.
     *
     * @param index specified index
     */
    public AnnotatedType getTypeVariableAnnotatedBound(int index) {
        return getTypeVariableAnnotatedBounds().get(index);
    }

    /**
     * Returns specified type of object as {@link GenericArrayType}.
     * <p>
     * This method only supports specified type which is {@link GenericArrayType}.
     */
    public GenericArrayType getGenericArrayType() {
        if (type instanceof GenericArrayType) {
            return (GenericArrayType) type;
        }
        throw new IllegalStateException("Specified type is not GenericArrayType.");
    }

    /**
     * Returns component type of specified type.
     * <p>
     * This method only supports specified type which is {@link GenericArrayType}.
     */
    public Type getGenericComponentType() {
        return getGenericArrayType().getGenericComponentType();
    }

    /**
     * Returns string of this ObjectType as format:
     * <pre>
     *     return "ObjectType{" + type.getTypeName() + ": " + object + "}";
     * </pre>
     */
    @Override
    public String toString() {
        return "ObjectType{" + type.getTypeName() + ": " + object + "}";
    }

    private static final class OfParameterizedType<T> extends ObjectType<T> {

        private final List<Type> actualTypeArguments;

        private OfParameterizedType(T object, ParameterizedType type) {
            super(object, type);
            this.actualTypeArguments = FsCollect.immutableList(Arrays.asList(type.getActualTypeArguments()));
        }

        @Override
        public List<Type> getActualTypeArguments() {
            return actualTypeArguments;
        }
    }

    private static final class OfWildcardType<T> extends ObjectType<T> {

        private final Type upperBound;
        private final Type lowerBound;

        private OfWildcardType(T object, WildcardType type) {
            super(object, type);
            this.upperBound = FsType.getUpperBound(type);
            this.lowerBound = FsType.getLowerBound(type);
        }

        @Override
        public @Nullable Type getUpperBound() {
            return upperBound;
        }

        @Override
        public @Nullable Type getLowerBound() {
            return lowerBound;
        }
    }

    private static final class OfTypeVariable<T> extends ObjectType<T> {

        private final List<Type> bounds;
        private final List<AnnotatedType> annotatedBounds;

        private OfTypeVariable(T object, TypeVariable<?> type) {
            super(object, type);
            this.bounds = FsCollect.immutableList(Arrays.asList(type.getBounds()));
            this.annotatedBounds = FsCollect.immutableList(Arrays.asList(type.getAnnotatedBounds()));
        }

        @Override
        public List<Type> getTypeVariableBounds() {
            return bounds;
        }

        @Override
        public List<AnnotatedType> getTypeVariableAnnotatedBounds() {
            return annotatedBounds;
        }
    }

    private static final class OfGenericArrayType<T> extends ObjectType<T> {

        private OfGenericArrayType(T object, GenericArrayType type) {
            super(object, type);
        }
    }
}
