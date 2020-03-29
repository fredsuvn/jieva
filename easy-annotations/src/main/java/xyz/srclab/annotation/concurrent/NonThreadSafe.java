package xyz.srclab.annotation.concurrent;

import javax.annotation.meta.TypeQualifierDefault;
import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Means static methods of annotated type are non-thread-safe.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@ThreadSafe(when = ThreadSafeWhen.FALSE)
@TypeQualifierDefault({
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.LOCAL_VARIABLE,
        ElementType.TYPE_USE,
        ElementType.PACKAGE,
        ElementType.TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.TYPE_PARAMETER,
        ElementType.ANNOTATION_TYPE,
})
@TypeQualifierNickname
public @interface NonThreadSafe {
}
