package xyz.srclab.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Represents annotated method will return a {@link DependentThreadSafe} object.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ReturnDependentThreadSafe {
}
