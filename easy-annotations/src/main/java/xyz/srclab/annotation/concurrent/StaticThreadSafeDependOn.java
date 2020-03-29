package xyz.srclab.annotation.concurrent;

import javax.annotation.meta.TypeQualifierDefault;
import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Means static methods of annotated type are dependent-thread-safe.
 * <p>
 * Note in default all static methods are dependent-thread-safe.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@StaticThreadSafe(when = ThreadSafeWhen.DEPEND_ON)
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
public @interface StaticThreadSafeDependOn {
}
