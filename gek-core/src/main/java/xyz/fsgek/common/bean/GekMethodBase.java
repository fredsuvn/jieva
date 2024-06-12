package xyz.fsgek.common.bean;

import xyz.fslabo.annotations.Immutable;
import xyz.fslabo.annotations.ThreadSafe;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Base info of {@link GekMemberInfo}.
 *
 * @author fredsuvn
 */
@Immutable
@ThreadSafe
public interface GekMethodBase {

    /**
     * Returns name of this method.
     *
     * @return name of this method
     */
    String getName();

    /**
     * Invokes this method and returns result.
     *
     * @param bean bean object
     * @param args method arguments
     * @return the result
     */
    Object invoke(Object bean, Object... args);

    /**
     * Returns java method of this bean method.
     *
     * @return java method of this bean method
     */
    Method getMethod();

    /**
     * Returns annotations on this method, may be empty if there is no.
     *
     * @return annotations on this method
     */
    List<Annotation> getAnnotations();
}
