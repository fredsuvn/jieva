package xyz.srclab.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Means static methods of annotated type are non-thread-safe.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface NonThreadSafe {
}
