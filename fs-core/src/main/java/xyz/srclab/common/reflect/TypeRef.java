package xyz.srclab.common.reflect;

import xyz.srclab.common.base.Fs;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Type reference usually to obtain type instance for java, for examples:
 * <p>
 * String.class:
 * <pre>
 *     TypeRef&lt;String> typeRef = new TypeRef&lt;String>(){};
 *     Type type = typeRef.getType();
 * </pre>
 * <p>
 * Generic type Map&lt;String, String>:
 * <pre>
 *     TypeRef&lt;Map&lt;String, String>> typeRef = new TypeRef&lt;Map&lt;String, String>>(){};
 *     Type type = typeRef.getType();
 * </pre>
 *
 * @author fredsuvn
 */
public abstract class TypeRef<T> {

    /**
     * Actual runtime type.
     */
    private final Type type;

    /**
     * Empty constructor, used to get a generic type.
     */
    protected TypeRef() {
        type = reflectToActualType();
    }

    private Type reflectToActualType() {
        Type generic = getClass().getGenericSuperclass();
        if (generic instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) generic;
            if (Fs.equals(p.getRawType(), TypeRef.class)) {
                return p.getActualTypeArguments()[0];
            }
        }
        throw new IllegalStateException();
    }

    /**
     * Returns type referenced by this ref.
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns type as parameterized type referenced by this ref.
     */
    public ParameterizedType getParameterizedType() {
        return (ParameterizedType) type;
    }
}
