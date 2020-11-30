package xyz.srclab.annotations;

import java.lang.annotation.*;

/**
 * Represents the parameter is output parameter and return value.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.PARAMETER,
})
public @interface OutReturn {
}