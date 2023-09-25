package xyz.srclab.annotations;

import java.lang.annotation.*;

/**
 * To specify a rejected type.
 *
 * @author sunqian
 * @see Rejectable
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
@Repeatable(Rejectable.class)
public @interface Rejected {

    Class<?> value();
}