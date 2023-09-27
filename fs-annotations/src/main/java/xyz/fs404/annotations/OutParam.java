package xyz.fs404.annotations;

import java.lang.annotation.*;

/**
 * Declares the annotated element is an output parameter that can be modified.
 *
 * @author fredsuvn
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.PARAMETER,
})
public @interface OutParam {
}