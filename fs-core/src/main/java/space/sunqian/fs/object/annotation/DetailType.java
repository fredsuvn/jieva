package space.sunqian.fs.object.annotation;

import space.sunqian.fs.object.meta.ObjectMetaIntrospector;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify the detail type for an annotation. It is typically used for
 * {@link AnnotationDetail#newDetail(Annotation)} and default implementation of {@link ObjectMetaIntrospector}. Thus,
 * the detail type should have a public constructor with one parameter, which is the target annotation. For example:
 *
 * <pre>{@code
 * @DetailType(MyAnnotationDetail.class)
 * public @interface MyAnnotation {
 *     ...
 * }
 *
 * public class MyAnnotationDetail implements AnnotationDetail<MyAnnotation> {
 *
 *     public MyAnnotationDetail(MyAnnotation annotation) {
 *         ...
 *     }
 * }
 * }</pre>
 *
 * @author sunqian
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.ANNOTATION_TYPE,
})
public @interface DetailType {

    /**
     * The detail type to be instantiated.
     *
     * @return the detail type to be instantiated
     */
    Class<?> value();
}
