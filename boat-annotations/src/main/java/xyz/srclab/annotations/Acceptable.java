package xyz.srclab.annotations;

import java.lang.annotation.*;

/**
 * To specify accepted types.
 *
 * @author sunqian
 * @see Accepted
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.LOCAL_VARIABLE,
        ElementType.TYPE_PARAMETER,
        ElementType.TYPE_USE,
})
public @interface Acceptable {

    Accepted[] value();
}