package xyz.fsgek.common.bean;

import xyz.fslabo.annotations.Immutable;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.annotations.ThreadSafe;
import xyz.fsgek.common.reflect.GekReflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Base info of {@link GekPropertyInfo}.
 *
 * @author fredsuvn
 */
@Immutable
@ThreadSafe
public interface GekPropertyBase {

    /**
     * Returns name of this property.
     *
     * @return name of this property
     */
    String getName();

    /**
     * Returns property value of given bean.
     *
     * @param bean given bean
     * @return property value of given bean
     */
    @Nullable
    Object getValue(Object bean);

    /**
     * Sets property value of given bean.
     *
     * @param bean  given bean
     * @param value property value
     */
    void setValue(Object bean, @Nullable Object value);

    /**
     * Returns type of this property.
     *
     * @return type of this property
     */
    Type getType();

    /**
     * Returns raw type of this property:
     * <pre>
     *     return GekReflect.getRawType(getType());
     * </pre>
     *
     * @return raw type of this property
     */
    @Nullable
    default Class<?> getRawType() {
        return GekReflect.getRawType(getType());
    }

    /**
     * Returns getter method of this property, or null if it doesn't exist.
     *
     * @return getter method of this property, or null
     */
    @Nullable
    Method getGetter();

    /**
     * Returns setter method of this property, or null if it doesn't exist.
     *
     * @return setter method of this property, or null
     */
    @Nullable
    Method getSetter();

    /**
     * Returns backed field of this property, or null if it doesn't exist.
     *
     * @return backed field of this property, or null
     */
    @Nullable
    Field getField();

    /**
     * Returns annotations on getter.
     *
     * @return annotations on getter
     */
    List<Annotation> getGetterAnnotations();

    /**
     * Returns annotations on setter.
     *
     * @return annotations on setter
     */
    List<Annotation> getSetterAnnotations();

    /**
     * Returns annotations on backed field.
     *
     * @return annotations on backed field
     */
    List<Annotation> getFieldAnnotations();

    /**
     * Returns annotations on getter, setter and backed field (in this order).
     *
     * @return annotations on getter, setter and backed field
     */
    List<Annotation> getAnnotations();

    /**
     * Returns whether this property is readable.
     *
     * @return whether this property is readable
     */
    boolean isReadable();

    /**
     * Returns whether this property is writeable.
     *
     * @return whether this property is writeable
     */
    boolean isWriteable();
}
