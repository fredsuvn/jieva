package xyz.srclab.annotation.concurrent;

import javax.annotation.meta.TypeQualifier;
import java.lang.annotation.*;

/**
 * Means static methods of annotated type are thread-safe.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifier
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
public @interface StaticThreadSafe {

    ThreadSafeWhen when() default ThreadSafeWhen.TRUE;
}
