package xyz.srclab.annotation.concurrent;

import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Represents annotated method will return a {@link ThreadSafe} object.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
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
public @interface ReturnThreadSafe {

    ThreadSafeWhen when() default ThreadSafeWhen.TRUE;
}
