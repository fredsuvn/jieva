package xyz.srclab.annotation.concurrent;

import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.*;

/**
 * Means static methods of annotated type are non-thread-safe.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@ThreadSafe(when = ThreadSafeWhen.FALSE)
@TypeQualifierNickname
@Target({
        ElementType.TYPE,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.PARAMETER,
        ElementType.CONSTRUCTOR,
        ElementType.LOCAL_VARIABLE,
        ElementType.ANNOTATION_TYPE,
        ElementType.PACKAGE,
        ElementType.TYPE_PARAMETER,
        ElementType.TYPE_USE,
})
public @interface NonThreadSafe {
}
