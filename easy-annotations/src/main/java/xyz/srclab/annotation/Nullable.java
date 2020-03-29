package xyz.srclab.annotation;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Nonnull(when = When.MAYBE)
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
public @interface Nullable {
}
