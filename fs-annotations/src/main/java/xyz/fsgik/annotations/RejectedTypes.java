package xyz.fsgik.annotations;

import java.lang.annotation.*;

/**
 * Declares type of the annotated element is not allowed to be the specified types.
 * It is container of {@link RejectedType}.
 *
 * @author sunqian
 * @see RejectedType
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
public @interface RejectedTypes {

    RejectedType[] value();
}