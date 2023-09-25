package xyz.srclab.annotations;

import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.*;

/**
 * Declares the annotated element is immutable.
 *
 * @author fredsuvn
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@javax.annotation.concurrent.Immutable
@TypeQualifierNickname
@Target({
    ElementType.TYPE,
    ElementType.METHOD,
    ElementType.FIELD,
    ElementType.PARAMETER,
    ElementType.LOCAL_VARIABLE,
    ElementType.TYPE_PARAMETER,
    ElementType.TYPE_USE,
})
public @interface Immutable {
}