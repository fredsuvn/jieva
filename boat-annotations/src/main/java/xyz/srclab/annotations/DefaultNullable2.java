package xyz.srclab.annotations;

import javax.annotation.meta.TypeQualifierDefault;
import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@DefaultNullable
@TypeQualifierDefault({
        ElementType.TYPE,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.PARAMETER,
        ElementType.CONSTRUCTOR,
        ElementType.LOCAL_VARIABLE,
        ElementType.ANNOTATION_TYPE,
        ElementType.PACKAGE,
        ElementType.TYPE_PARAMETER,
        //ElementType.TYPE_USE,
})
@TypeQualifierNickname
@Target({
        //ElementType.TYPE,
        //ElementType.FIELD,
        //ElementType.METHOD,
        //ElementType.PARAMETER,
        //ElementType.CONSTRUCTOR,
        //ElementType.LOCAL_VARIABLE,
        //ElementType.ANNOTATION_TYPE,
        ElementType.PACKAGE,
        //ElementType.TYPE_PARAMETER,
        //ElementType.TYPE_USE,
})
public @interface DefaultNullable2 {
}