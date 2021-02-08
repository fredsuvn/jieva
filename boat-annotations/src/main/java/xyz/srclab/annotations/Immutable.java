package xyz.srclab.annotations;

import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@javax.annotation.concurrent.Immutable
@TypeQualifierNickname
@Target({
        ElementType.TYPE,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.PARAMETER,
        //ElementType.CONSTRUCTOR,
        ElementType.LOCAL_VARIABLE,
        //ElementType.ANNOTATION_TYPE,
        ElementType.PACKAGE,
        ElementType.TYPE_PARAMETER,
        //ElementType.TYPE_USE,
})
public @interface Immutable {
}