package xyz.srclab.annotations;

import java.lang.annotation.*;

/**
 * To specify an accepted type.
 *
 * @author sunqian
 * @see Acceptable
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
@Repeatable(Acceptable.class)
public @interface Accepted {

    Class<?> value();
}