package xyz.fs404.annotations;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;
import java.lang.annotation.*;

/**
 * Declares the annotated element and all methods, fields parameters, local variables and type uses
 * in its scope may be null.
 * It is equivalent to use {@link Nullable} on those spaces.
 *
 * @author fredsuvn
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Nonnull(when = When.MAYBE)
@TypeQualifierDefault({
    ElementType.METHOD,
    ElementType.FIELD,
    ElementType.PARAMETER,
    ElementType.LOCAL_VARIABLE,
    ElementType.TYPE_PARAMETER,
    ElementType.TYPE_USE,
})
@TypeQualifierNickname
@Target({
    ElementType.TYPE,
    ElementType.METHOD,
    ElementType.CONSTRUCTOR,
    ElementType.PACKAGE,
})
public @interface DefaultNullable {
}