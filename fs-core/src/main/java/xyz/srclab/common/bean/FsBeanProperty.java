package xyz.srclab.common.bean;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.reflect.FsType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Represents property in {@link FsBean}.
 *
 * @author fredsuvn
 */
public interface FsBeanProperty {

    /**
     * Returns property value of given bean instance.
     *
     * @param bean given bean instance
     */
    @Nullable
    Object get(Object bean);

    /**
     * Sets property value of given bean instance.
     *
     * @param bean  given bean instance
     * @param value property value
     */
    void set(Object bean, @Nullable Object value);

    /**
     * Returns type of this property.
     */
    Type getType();

    /**
     * Returns raw type of this property.
     */
    default Class<?> getRawType() {
        return FsType.getRawType(getType());
    }

    /**
     * Returns getter method of this property, or null if it doesn't exist.
     */
    @Nullable
    Method getGetter();

    /**
     * Returns setter method of this property, or null if it doesn't exist.
     */
    @Nullable
    Method getSetter();

    /**
     * Returns backed field of this property, or null if it doesn't exist.
     */
    @Nullable
    Field getField();

    /**
     * Returns annotations on getter.
     */
    List<Annotation> getGetterAnnotations();

    /**
     * Returns annotations on setter.
     */
    List<Annotation> getSetterAnnotations();

    /**
     * Returns annotations on backed field.
     */
    List<Annotation> getFieldAnnotations();

    /**
     * Returns annotations on getter, setter and backed field (in this order).
     */
    List<Annotation> getAnnotations();

    /**
     * Returns owner bean of this property.
     */
    FsBean getOwner();

    /**
     * Returns whether this property is readable.
     */
    boolean isReadable();

    /**
     * Returns whether this property is writeable.
     */
    boolean isWriteable();
}
