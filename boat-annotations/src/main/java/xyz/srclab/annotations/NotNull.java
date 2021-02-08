package xyz.srclab.annotations;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Nonnull(when = When.ALWAYS)
@TypeQualifierNickname
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
        //ElementType.TYPE_USE,
})
public @interface NotNull {
}