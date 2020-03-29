package xyz.srclab.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Means static methods of annotated type are dependent-thread-safe.
 * <p>
 * Note in default all static methods are dependent-thread-safe.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface StaticDependentThreadSafe {
}
