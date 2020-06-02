package xyz.srclab.annotation;

import java.lang.annotation.*;

/**
 * Represents the parameter is output parameter.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.PARAMETER,
})
public @interface Out {
}
