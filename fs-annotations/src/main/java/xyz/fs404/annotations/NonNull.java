package xyz.fs404.annotations;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.*;

/**
 * Declares the annotated element must not be null.
 *
 * @author fredsuvn
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Nonnull
@TypeQualifierNickname
@Target({
    ElementType.METHOD,
    ElementType.FIELD,
    ElementType.PARAMETER,
    ElementType.LOCAL_VARIABLE,
    ElementType.TYPE_PARAMETER,
    ElementType.TYPE_USE,
})
public @interface NonNull {
}