package xyz.srclab.annotations;

import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.*;

/**
 * Declares the annotated element is thread-safe in its scope.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@javax.annotation.concurrent.ThreadSafe
@TypeQualifierNickname
@Target({
    ElementType.TYPE,
    ElementType.METHOD,
    ElementType.CONSTRUCTOR,
    ElementType.FIELD,
    ElementType.PARAMETER,
    ElementType.LOCAL_VARIABLE,
    ElementType.TYPE_PARAMETER,
    ElementType.TYPE_USE,
})
public @interface ThreadSafe {
}