package xyz.srclab.annotation.concurrent;

import javax.annotation.meta.TypeQualifierDefault;
import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Represents annotated method will return a {@link ThreadSafeDependOn} object.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@ReturnThreadSafe(when = ThreadSafeWhen.DEPEND_ON)
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
public @interface ReturnThreadSafeDependOn {
}
