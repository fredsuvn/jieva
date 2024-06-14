package xyz.fslabo.common.bean;

import xyz.fslabo.annotations.Immutable;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.annotations.ThreadSafe;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

/**
 * Information about the member ({@link BeanProperty} or {@link BeanMethod}) of {@link BeanInfo}.
 *
 * @author fredsuvn
 * @see BeanInfo
 * @see BeanProperty
 * @see BeanMethod
 */
@Immutable
@ThreadSafe
public interface BeanMember {

    /**
     * Returns owner bean info of this info.
     *
     * @return owner bean info of this info
     */
    BeanInfo getOwner();

    /**
     * Returns name of this member.
     *
     * @return name of this member
     */
    String getName();

    /**
     * Returns annotations on this member, may be empty if there is no.
     *
     * @return annotations on this member
     */
    List<Annotation> getAnnotations();

    /**
     * Returns first annotation of specified type on {@link #getAnnotations()}, or null if not found.
     *
     * @param annotationType specified type
     * @return first annotation of specified type on {@link #getAnnotations()}, or null if not found
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
}
