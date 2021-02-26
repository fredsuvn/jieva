package xyz.srclab.annotations;

import java.lang.annotation.*;

/**
 * To specify rejected types.
 *
 * @author sunqian
 * @see Rejected
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
public @interface Rejectable {

    Rejected[] value();
}