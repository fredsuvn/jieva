package xyz.srclab.annotations;

import java.lang.annotation.*;

/**
 * To specify limited type of return value.
 *
 * @author sunqian
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
        //ElementType.TYPE,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.PARAMETER,
        //ElementType.CONSTRUCTOR,
        ElementType.LOCAL_VARIABLE,
        //ElementType.ANNOTATION_TYPE,
        //ElementType.PACKAGE,
        ElementType.TYPE_PARAMETER,
        ElementType.TYPE_USE,
})
public @interface PossibleTypes {

    Class<?>[] value() default {};

    Class<?>[] exclude() default {};
}