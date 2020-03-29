package xyz.srclab.annotation;

import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@javax.annotation.concurrent.Immutable
@TypeQualifierNickname
public @interface Immutable {
}
