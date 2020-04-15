package xyz.srclab.annotations.concurrent;

import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.*;

/**
 * Represents annotated method will return a {@link ThreadSafeDependOn} object.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@ReturnThreadSafe(when = ThreadSafeWhen.DEPEND_ON)
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
public @interface ReturnThreadSafeDependOn {
}
