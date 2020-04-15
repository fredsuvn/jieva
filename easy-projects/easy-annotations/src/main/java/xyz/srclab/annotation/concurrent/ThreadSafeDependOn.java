package xyz.srclab.annotation.concurrent;

import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.*;

/**
 * Represents annotated type or method itself, part without any calling of dependent object or argument, is thread-safe.
 * As for part of dependent objects or arguments, depends on themselves. For example:
 * <pre>
 *
 *     {@code @DependentThreadSafe}
 *     Class Some {
 *         private Other other;
 *
 *         void someSelf(){
 *             // No other calling.
 *             // This method should be thread-safe,
 *             // because this method belong to {@code Some} class itself.
 *         }
 *
 *         void someCallOther(){
 *             // Other calling.
 *             other.callOther();
 *             // Whether this method is tread-safe depends on {@code other.callOther()}.
 *         }
 *     }
 *
 *     {@code @DependentThreadSafe}
 *     static void doSomething(Other other){
 *         other.callOther();
 *         // Whether this method is tread-safe depends on {@code other.callOther()}.
 *     }
 * </pre>
 * <p>
 * If a type is annotated by this annotation, all methods should be dependent-thread-safe or thread-safe if it depends
 * on nothing.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@ThreadSafe(when = ThreadSafeWhen.DEPEND_ON)
@TypeQualifierNickname
@Target({
        ElementType.TYPE,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.PARAMETER,
        ElementType.CONSTRUCTOR,
        ElementType.LOCAL_VARIABLE,
        ElementType.ANNOTATION_TYPE,
        ElementType.PACKAGE,
        ElementType.TYPE_PARAMETER,
        ElementType.TYPE_USE,
})
public @interface ThreadSafeDependOn {
}
