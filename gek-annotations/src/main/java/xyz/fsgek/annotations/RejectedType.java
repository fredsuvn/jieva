package xyz.fsgek.annotations;

import java.lang.annotation.*;

/**
 * Declares type of the annotated element is not allowed to be the specified type.
 *
 * @author fredsuvn
 * @see RejectedTypes
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
@Repeatable(RejectedTypes.class)
public @interface RejectedType {

    /**
     * Rejected type.
     *
     * @return rejected type
     */
    Class<?> value();
}