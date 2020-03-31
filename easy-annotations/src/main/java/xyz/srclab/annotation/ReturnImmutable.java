package xyz.srclab.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.METHOD,
})
public @interface ReturnImmutable {
}
