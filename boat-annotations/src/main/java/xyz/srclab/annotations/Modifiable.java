package xyz.srclab.annotations;

import java.lang.annotation.*;

/**
 * Represents the parameter is an output parameter (may be modified).
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.PARAMETER,
        ElementType.TYPE_USE,
})
public @interface Modifiable {
}