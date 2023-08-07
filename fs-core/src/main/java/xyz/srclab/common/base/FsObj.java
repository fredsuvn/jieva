package xyz.srclab.common.base;

import lombok.EqualsAndHashCode;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.reflect.FsType;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class wraps an object and its type.
 * This class is usually used to specify generic type for an instance.
 *
 * @author fredsuvn
 */
@EqualsAndHashCode
public abstract class FsObj<T> {

    /**
     * Returns a new ObjectType with given object and its type.
     *
     * @param object given object
     * @param type   type
     */
    public static <T> FsObj<T> of(T object, Type type) {
        if (type instanceof Class) {
            return new OfClass<>(object, type);
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
        throw new UnsupportedOperationException(
            "Type must be one of Class, ParameterizedType, WildcardType, TypeVariable or GenericArrayType.");
    }

    private final T object;
    private final Type type;

    /**
     * Constructs with given object and its type.
     *
     * @param object given object
     * @param type   type of given object
     */
    protected FsObj(@Nullable T object, Type type) {
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
     * Returns type of object.
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns type of object as {@link Class}.
     * <p>
     * This method only supports the type which is instance of {@link Class}.
     */
    public Class<?> getClassType() {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        return throwUnsupported(Class.class);
    }

    /**
     * Returns type of object as {@link ParameterizedType}.
     * <p>
     * This method only supports the type which is instance of {@link ParameterizedType}.
     */
    public ParameterizedType getParameterizedType() {
        if (type instanceof ParameterizedType) {
            return (ParameterizedType) type;
        }
        return throwUnsupported(ParameterizedType.class);
    }

    /**
     * Returns raw type of type.
     * <p>
     * This method only supports the type which is instance of {@link ParameterizedType}.
     */
    public Class<?> getRawType() {
        return (Class<?>) getParameterizedType().getRawType();
    }

    /**
     * Returns owner type of type.
     * <p>
     * This method only supports the type which is instance of {@link ParameterizedType}.
     */
    public Type getOwnerType() {
        return getParameterizedType().getOwnerType();
    }

    /**
     * Returns actual type arguments of type.
     * <p>
     * This method only supports the type which is instance of {@link ParameterizedType}.
     */
    public List<Type> getActualTypeArguments() {
        return throwUnsupported(ParameterizedType.class);
    }

    /**
     * Returns actual type argument of type at specified index.
     * <p>
     * This method only supports the type which is instance of {@link ParameterizedType}.
     *
     * @param index specified index
     */
    public Type getActualTypeArgument(int index) {
        return getActualTypeArguments().get(index);
    }

    /**
     * Returns type of object as {@link WildcardType}.
     * <p>
     * This method only supports the type which is instance of {@link WildcardType}.
     */
    public WildcardType getWildcardType() {
        if (type instanceof WildcardType) {
            return (WildcardType) type;
        }
        return throwUnsupported(WildcardType.class);
    }

    /**
     * Returns upper bound (? extends) of type.
     * If the type has lower bound, return null.
     * <p>
     * This method only supports the type which is instance of {@link WildcardType}.
     */
    @Nullable
    public Type getUpperBound() {
        return throwUnsupported(WildcardType.class);
    }

    /**
     * Returns lower bound (? super) of type.
     * If the type has no lower bound, return null.
     * <p>
     * This method only supports the type which is instance of {@link WildcardType}.
     */
    @Nullable
    public Type getLowerBound() {
        return throwUnsupported(WildcardType.class);
    }

    /**
     * Returns type of object as {@link TypeVariable}.
     * <p>
     * This method only supports the type which is instance of {@link TypeVariable}.
     */
    public TypeVariable<?> getTypeVariable() {
        if (type instanceof TypeVariable) {
            return (TypeVariable<?>) type;
        }
        return throwUnsupported(TypeVariable.class);
    }

    /**
     * Returns type variable upper bounds ({@link TypeVariable#getBounds()}) of type.
     * <p>
     * This method only supports the type which is instance of {@link TypeVariable}.
     */
    public List<Type> getTypeVariableBounds() {
        return throwUnsupported(TypeVariable.class);
    }

    /**
     * Returns type variable upper bound ({@link TypeVariable#getBounds()}) of type at specified index.
     * <p>
     * This method only supports the type which is instance of {@link TypeVariable}.
     *
     * @param index specified index
     */
    public Type getTypeVariableBound(int index) {
        return getTypeVariableBounds().get(index);
    }

    /**
     * Returns type variable name ({@link TypeVariable#getName()}) of type.
     * <p>
     * This method only supports the type which is instance of {@link TypeVariable}.
     */
    public String getTypeVariableName() {
        return getTypeVariable().getName();
    }

    /**
     * Returns type variable annotated bounds ({@link TypeVariable#getAnnotatedBounds()}) of type.
     * <p>
     * This method only supports the type which is instance of {@link TypeVariable}.
     */
    public List<AnnotatedType> getTypeVariableAnnotatedBounds() {
        return throwUnsupported(TypeVariable.class);
    }

    /**
     * Returns type variable annotated bound ({@link TypeVariable#getAnnotatedBounds()}) of type
     * at specified index.
     * <p>
     * This method only supports the type which is instance of {@link TypeVariable}.
     *
     * @param index specified index
     */
    public AnnotatedType getTypeVariableAnnotatedBound(int index) {
        return getTypeVariableAnnotatedBounds().get(index);
    }

    /**
     * Returns type of object as {@link GenericArrayType}.
     * <p>
     * This method only supports the type which is instance of {@link GenericArrayType}.
     */
    public GenericArrayType getGenericArrayType() {
        if (type instanceof GenericArrayType) {
            return (GenericArrayType) type;
        }
        return throwUnsupported(GenericArrayType.class);
    }

    /**
     * Returns component type of type.
     * <p>
     * This method only supports the type which is instance of {@link GenericArrayType}.
     */
    public Type getGenericComponentType() {
        return getGenericArrayType().getGenericComponentType();
    }

    /**
     * Returns string of this ObjectType as format:
     * <pre>
     *     return "{" + type.getTypeName() + ": " + object + "}";
     * </pre>
     */
    @Override
    public String toString() {
        return "{" + type.getTypeName() + ": " + object + "}";
    }

    protected <X> X throwUnsupported(Class<?> needType) {
        throw new UnsupportedOperationException("Need " + needType.getClass().getName() + " type: " + type.getClass() + ".");
    }

    private static final class OfClass<T> extends FsObj<T> {
        private OfClass(@Nullable T object, Type type) {
            super(object, type);
        }
    }


    private static final class OfParameterizedType<T> extends FsObj<T> {

        private List<Type> actualTypeArguments;

        private OfParameterizedType(T object, ParameterizedType type) {
            super(object, type);
        }

        @Override
        public List<Type> getActualTypeArguments() {
            if (actualTypeArguments == null) {
                actualTypeArguments = Collections.unmodifiableList(
                    Arrays.asList(getParameterizedType().getActualTypeArguments()));
            }
            return actualTypeArguments;
        }
    }

    private static final class OfWildcardType<T> extends FsObj<T> {

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

    private static final class OfTypeVariable<T> extends FsObj<T> {

        private List<Type> bounds;
        private List<AnnotatedType> annotatedBounds;

        private OfTypeVariable(T object, TypeVariable<?> type) {
            super(object, type);
        }

        @Override
        public List<Type> getTypeVariableBounds() {
            if (bounds == null) {
                this.bounds = Collections.unmodifiableList(Arrays.asList(getTypeVariable().getBounds()));
            }
            return bounds;
        }

        @Override
        public List<AnnotatedType> getTypeVariableAnnotatedBounds() {
            if (annotatedBounds == null) {
                this.annotatedBounds = Collections.unmodifiableList(
                    Arrays.asList(getTypeVariable().getAnnotatedBounds()));
            }
            return annotatedBounds;
        }
    }

    private static final class OfGenericArrayType<T> extends FsObj<T> {

        private OfGenericArrayType(T object, GenericArrayType type) {
            super(object, type);
        }
    }
}
