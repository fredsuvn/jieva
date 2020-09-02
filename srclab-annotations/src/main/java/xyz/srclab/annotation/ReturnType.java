package xyz.srclab.annotation;

import java.lang.annotation.*;

/**
 * To specify limited type of return value.
 *
 * @author sunqian
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.METHOD,
})
public @interface ReturnType {

    Class<?>[] value() default {};

    Class<?>[] exclude() default {};
}
