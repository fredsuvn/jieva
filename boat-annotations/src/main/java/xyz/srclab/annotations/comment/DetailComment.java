package xyz.srclab.annotations.comment;

import java.lang.annotation.*;

/**
 * @author sunqian
 */
@Documented
@Retention(RetentionPolicy.CLASS)
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
public @interface DetailComment {
}