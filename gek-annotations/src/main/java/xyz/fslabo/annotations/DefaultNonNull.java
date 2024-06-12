package xyz.fslabo.annotations;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.*;

/**
 * Declares the annotated element and all methods, fields, parameters, local variables and type uses under its scope
 * must be non-null.
 * It is equivalent to use {@link NonNull} on all those points.
 *
 * @author fredsuvn
 */
@Documented
@Nonnull
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifierNickname
@Target({
    ElementType.TYPE,
    ElementType.METHOD,
    ElementType.CONSTRUCTOR,
    ElementType.PACKAGE,
})
@TypeQualifierDefault({
    ElementType.METHOD,
    ElementType.FIELD,
    ElementType.PARAMETER,
    ElementType.LOCAL_VARIABLE,
    ElementType.TYPE_PARAMETER,
    ElementType.TYPE_USE,
})
public @interface DefaultNonNull {
}