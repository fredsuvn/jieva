package xyz.srclab.annotation.concurrent;

import javax.annotation.meta.TypeQualifier;
import java.lang.annotation.*;

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
public @interface ThreadSafe {

    ThreadSafeWhen when() default ThreadSafeWhen.TRUE;
}
