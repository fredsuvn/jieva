package xyz.fsgek.common.bean;

import xyz.fsgek.annotations.Immutable;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.annotations.ThreadSafe;
import xyz.fsgek.common.reflect.FsReflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * Base info of property of a bean ({@link FsBean}).
 *
 * @author fredsuvn
 */
@Immutable
@ThreadSafe
public interface FsPropertyBase {

    /**
     * Returns name of this property.
     *
     * @return name of this property
     */
    String getName();

    /**
     * Returns property value of given bean instance.
     *
     * @param bean given bean instance
     * @return property value of given bean instance
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
     *
     * @return type of this property
     */
    Type getType();

    /**
     * Returns raw type of this property.
     *
     * @return raw type of this property
     */
    default Class<?> getRawType() {
        return FsReflect.getRawType(getType());
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
     * Returns first annotation of {@link #getAnnotations()} of which type is specified annotation type,
     * or null if not found.
     *
     * @param annotationType specified annotation type
     * @return first annotation of {@link #getAnnotations()} of which type is specified annotation type, or null
     */
    @Nullable
    default Annotation getAnnotation(Class<?> annotationType) {
        for (Annotation annotation : getAnnotations()) {
            if (Objects.equals(annotationType, annotation.getClass())) {
                return annotation;
            }
        }
        return null;
    }

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
