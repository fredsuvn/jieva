package xyz.fsgek.annotations;

import java.lang.annotation.*;

/**
 * Declares type of the annotated element is allowed to be the specified types.
 * It is container of {@link AcceptedType}.
 *
 * @author fredsuvn
 * @see AcceptedType
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
public @interface AcceptedTypes {

    AcceptedType[] value();
}