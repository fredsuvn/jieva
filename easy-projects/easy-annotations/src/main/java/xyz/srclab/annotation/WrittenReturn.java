package xyz.srclab.annotation;

import java.lang.annotation.*;

/**
 * Represents the parameter would be write and return.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.PARAMETER,
})
public @interface WrittenReturn {
}
