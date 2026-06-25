package space.sunqian.fs.object.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify the detail type for a target annotation. The detail type provides the more detail
 * info for the target annotation.
 * <p>
 * The detail type should follow the following conventions:
 * <ul>
 *     <li>It should have a public constructor with one parameter, of which the type is the target annotation type;</li>
 *     <li>It should implement {@link AnnotationDetail} interface;</li>
 * </ul>
 * For example:
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
