package xyz.srclab.annotation.concurrent;

import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.*;

/**
 * Means static methods of annotated type are dependent-thread-safe.
 * <p>
 * Note in default all static methods are dependent-thread-safe.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@StaticThreadSafe(when = ThreadSafeWhen.DEPEND_ON)
@TypeQualifierNickname
@Target({
        ElementType.TYPE,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.PARAMETER,
        ElementType.LOCAL_VARIABLE,
        ElementType.ANNOTATION_TYPE,
        ElementType.PACKAGE,
        ElementType.TYPE_PARAMETER,
        ElementType.TYPE_USE,
})
public @interface StaticThreadSafeDependOn {
}
