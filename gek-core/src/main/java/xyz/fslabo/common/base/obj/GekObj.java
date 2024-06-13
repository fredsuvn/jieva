package xyz.fslabo.common.base.obj;

import xyz.fslabo.common.reflect.GekReflect;
import xyz.fslabo.common.reflect.GekType;
import xyz.fslabo.common.reflect.TypeRef;

import java.lang.reflect.*;
import java.util.Collections;

/**
 * Object and its type.
 * This class is usually used to specify clear type for an object.
 *
 * @author fredsuvn
 */
public interface GekObj<T> {

    /**
     * Wraps given object with its type.
     * <p>
     * This method will return instance of:
     * <ul>
     *     <li>{@link ClassObj};</li>
     *     <li>{@link ParameterizedObj};</li>
     *     <li>{@link WildcardObj};</li>
     *     <li>{@link TypeVariableObj};</li>
     *     <li>{@link GenericArrayObj};</li>
     * </ul>
     *
     * @param object given object
     * @param type   type of given object
     * @param <T>    type of object
     * @return wrapped {@link GekObj} or its subtypes
     */
    static <T> GekObj<T> wrap(T object, Type type) {
        if (type instanceof Class) {
            return new Impls.ClassImpl<>(object, (Class<?>) type);
        }
        if (type instanceof ParameterizedType) {
            return new Impls.ParameterizedImpl<>(object, (ParameterizedType) type);
        }
        if (type instanceof WildcardType) {
            return new Impls.WildcardImpl<>(object, (WildcardType) type);
        }
        if (type instanceof TypeVariable) {
            return new Impls.TypeVariableImpl<>(object, (TypeVariable<?>) type);
        }
        if (type instanceof GenericArrayType) {
            return new Impls.GenericArrayImpl<>(object, (GenericArrayType) type);
        }
        throw new UnsupportedOperationException(
            "Type must be one of Class, ParameterizedType, WildcardType, TypeVariable or GenericArrayType.");
    }

    /**
     * Wraps with given object and its type ref. This method will call {@link #wrap(Object, Type)}.
     *
     * @param object  given object
     * @param typeRef type ref of given object
     * @param <T>     type of object
     * @return wrapped {@link GekObj} or its subtypes
     */
    static <T> GekObj<T> wrap(T object, TypeRef<T> typeRef) {
        return wrap(object, typeRef.getType());
    }

    /**
     * Returns hold object.
     *
     * @return hold object
     */
    T getObject();

    /**
     * Return type of hold object.
     *
     * @return type of hold object
     */
    Type getType();

    /**
     * To {@link ClassObj}.
     * <p>
     * Note {@link ParameterizedObj} can convert to {@link Class} without its generic types.
     *
     * @return of {@link ClassObj}
     */
    default ClassObj<T> toClassObj() {
        if (this instanceof ClassObj) {
            return (ClassObj<T>) this;
        }
        if (this instanceof ParameterizedObj) {
            return new Impls.ClassImpl<>(getObject(), GekReflect.getRawType(getType()));
        }
        return new Impls.ClassImpl<>(getObject(), (Class<?>) getType());
    }

    /**
     * To {@link ParameterizedObj}.
     * <p>
     * Note {@link ClassObj} can convert to {@link ParameterizedObj} with a generic type of {@link Object} type.
     *
     * @return of {@link ParameterizedObj}
     */
    default ParameterizedObj<T> toParameterizedObj() {
        if (this instanceof ParameterizedObj) {
            return (ParameterizedObj<T>) this;
        }
        if (this instanceof ClassObj) {
            return new Impls.ParameterizedImpl<>(getObject(),
                GekType.paramType(getType(), Collections.singletonList(Object.class)));
        }
        return new Impls.ParameterizedImpl<>(getObject(), (ParameterizedType) getType());
    }

    /**
     * To {@link WildcardObj}.
     *
     * @return of {@link WildcardObj}
     */
    default WildcardObj<T> toWildcardObj() {
        if (this instanceof WildcardObj) {
            return (WildcardObj<T>) this;
        }
        return new Impls.WildcardImpl<>(getObject(), (WildcardType) getType());
    }

    /**
     * To {@link TypeVariableObj}.
     *
     * @return of {@link TypeVariableObj}
     */
    default TypeVariableObj<T> toTypeVariableObj() {
        if (this instanceof TypeVariableObj) {
            return (TypeVariableObj<T>) this;
        }
        return new Impls.TypeVariableImpl<>(getObject(), (TypeVariable<?>) getType());
    }

    /**
     * To {@link GenericArrayObj}.
     *
     * @return of {@link GenericArrayObj}
     */
    default GenericArrayObj<T> toGenericArrayObj() {
        if (this instanceof GenericArrayObj) {
            return (GenericArrayObj<T>) this;
        }
        return new Impls.GenericArrayImpl<>(getObject(), (GenericArrayType) getType());
    }
}
