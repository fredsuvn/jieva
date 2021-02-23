package xyz.srclab.annotations;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Nonnull(when = When.MAYBE)
@TypeQualifierDefault({
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.PARAMETER,
})
@TypeQualifierNickname
@Target({
        ElementType.TYPE,
})
public @interface JavaBean {
}