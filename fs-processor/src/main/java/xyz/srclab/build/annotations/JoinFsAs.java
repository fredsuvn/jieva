package xyz.srclab.build.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks this static method will join into Fs as specified name in compile processing.
 *
 * @author fredsuvn
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface JoinFsAs {

    /**
     * The name in Fs.
     */
    String value();
}
