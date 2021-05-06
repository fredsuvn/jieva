package xyz.srclab.annotations;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Nonnull(when = When.MAYBE)
@TypeQualifierNickname
@Target({
    ElementType.METHOD,
    ElementType.FIELD,
    ElementType.PARAMETER,
    ElementType.LOCAL_VARIABLE,
    ElementType.TYPE_PARAMETER,
    ElementType.TYPE_USE,
})
public @interface Nullable {
}