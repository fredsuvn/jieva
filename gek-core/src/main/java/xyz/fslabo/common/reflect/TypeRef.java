package xyz.fslabo.common.reflect;

import xyz.fslabo.common.base.Jie;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * This class represents type reference, usually used to obtain type instance for java, for examples:
 * <p>
 * String.class:
 * <pre>
 *     TypeRef&lt;String&gt; typeRef = new TypeRef&lt;String&gt;(){};
 *     Type type = typeRef.getType();
 * </pre>
 * <p>
 * Generic type Map&lt;String, String&gt;:
 * <pre>
 *     TypeRef&lt;Map&lt;String, String&gt;&gt; typeRef = new TypeRef&lt;Map&lt;String, String&gt;&gt;(){};
 *     Type type = typeRef.getType();
 * </pre>
 *
 * @author fredsuvn
 */
public abstract class TypeRef<T> {

    /**
     * Actual runtime type.
     */
    private volatile Type type;

    /**
     * Empty constructor, used to get a generic type.
     */
    protected TypeRef() {
    }

    private Type reflectToActualType() {
        Type generic = getClass().getGenericSuperclass();
        if (generic instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) generic;
            if (Jie.equals(p.getRawType(), TypeRef.class)) {
                return p.getActualTypeArguments()[0];
            }
        }
        ParameterizedType parameterizedType = JieReflect.getGenericSuperType(generic, TypeRef.class);
        if (parameterizedType == null) {
            throw new IllegalStateException("Current type is not subtype of TypeRef: " + getClass().getName());
        }
        return parameterizedType.getActualTypeArguments()[0];
    }

    /**
     * Returns type of this type-ref.
     *
     * @return type of this type-ref
     */
    public Type getType() {
        if (type == null) {
            synchronized (this) {
                if (type == null) {
                    type = reflectToActualType();
                }
            }
        }
        return type;
    }

    /**
     * Returns {@link #getType()} as {@link ParameterizedType} of this type-ref.
     *
     * @return type as {@link ParameterizedType} of this type-ref
     */
    public ParameterizedType asParameterized() {
        return (ParameterizedType) getType();
    }

    /**
     * Returns {@link #getType()} as {@link Class} of this type-ref.
     *
     * @return type as {@link Class} of this type-ref
     */
    public Class<T> asClass() {
        return (Class<T>) getType();
    }
}
