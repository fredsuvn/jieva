package xyz.srclab.annotations.concurrent;

import java.lang.annotation.*;

/**
 * To specify target annotated is thread-safe if {@link #value()} was met.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
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
public @interface ThreadSafeIf {

    String value();
}