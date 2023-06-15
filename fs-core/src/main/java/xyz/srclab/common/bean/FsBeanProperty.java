package xyz.srclab.common.bean;

import xyz.srclab.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Represents property in {@link FsBean}.
 *
 * @author sunq62
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
     * Returns annotations on field.
     */
    List<Annotation> getFieldAnnotations();

    /**
     * Returns owner bean of this property.
     */
    FsBean getOwner();
}
