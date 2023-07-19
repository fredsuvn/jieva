package xyz.srclab.common.reflect;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Generic info of object.
 *
 * @author fredsuvn
 */
@EqualsAndHashCode
@ToString
public class GenericInfo<T> {

    private final T object;
    private final ParameterizedType parameterizedType;
    private final Class<?> rawType;
    private final List<Type> typeArguments;

    /**
     * Constructs with given object and parameterized type.
     *
     * @param object            given object
     * @param parameterizedType parameterized type
     */
    public GenericInfo(T object, ParameterizedType parameterizedType) {
        this.object = object;
        this.parameterizedType = parameterizedType;
        this.rawType = (Class<?>) parameterizedType.getRawType();
        this.typeArguments = Collections.unmodifiableList(Arrays.asList(parameterizedType.getActualTypeArguments()));
    }

    /**
     * Returns given object.
     */
    public T getObject() {
        return object;
    }

    /**
     * Returns parameterized type of given object.
     */
    public ParameterizedType getParameterizedType() {
        return parameterizedType;
    }

    /**
     * Returns raw type of given object.
     */
    public Class<?> getRawType() {
        return rawType;
    }

    /**
     * Returns type arguments of given object.
     */
    public List<Type> getTypeArguments() {
        return typeArguments;
    }

    /**
     * Returns type argument of given object at specified index.
     *
     * @param index specified index
     */
    public Type getTypeArgument(int index) {
        return typeArguments.get(index);
    }
}
