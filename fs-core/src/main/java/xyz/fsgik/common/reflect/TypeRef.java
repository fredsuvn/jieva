package xyz.fsgik.common.reflect;

import xyz.fsgik.common.base.Fs;

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
    private Type type;

    /**
     * Empty constructor, used to get a generic type.
     */
    protected TypeRef() {
    }

    private Type reflectToActualType() {
        Type generic = getClass().getGenericSuperclass();
        if (generic instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) generic;
            if (Fs.equals(p.getRawType(), TypeRef.class)) {
                return p.getActualTypeArguments()[0];
            }
        }
        ParameterizedType parameterizedType = FsReflect.getGenericSuperType(generic, TypeRef.class);
        if (parameterizedType == null) {
            throw new IllegalStateException("Current type is not subtype of TypeRef: " + getClass().getName());
        }
        return parameterizedType.getActualTypeArguments()[0];
    }

    /**
     * Returns type referenced by this ref.
     */
    public Type getType() {
        if (type == null) {
            type = reflectToActualType();
        }
        return type;
    }

    /**
     * Returns {@link #getType()} as {@link ParameterizedType} referenced by this ref.
     */
    public ParameterizedType asParameterized() {
        return (ParameterizedType) getType();
    }

    /**
     * Returns {@link #getType()} as {@link Class} referenced by this ref.
     */
    public Class<T> asClass() {
        return (Class<T>) getType();
    }
}
