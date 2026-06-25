package space.sunqian.fs.object.annotation;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

/**
 * Represents the detail info for an annotation, including the original annotation instance and more detail infos from
 * the annotation attributes.
 * <p>
 * Typically, a detail type can be specified by {@link DetailType} on the target annotation.
 *
 * @param <T> the type of the annotation instance
 * @author sunqian
 */
public interface AnnotationDetail<T extends Annotation> {

    /**
     * Returns a new instance of {@link AnnotationDetail} as the detail type for the given annotation.
     * <p>
     * If the given annotation specifies the detail type by {@link DetailType}, then the detail type will be
     * instantiated following the conventions defined in {@link DetailType}. For example:
     * {@link DatePattern}/{@link DatePatternDetail}, {@link NumberPattern}/{@link NumberPatternDetail}.
     * <p>
     * If the given annotation does not specify the detail type by {@link DetailType}, or the specified detail type
     * cannot be instantiated following the conventions defined in {@link DetailType}, then a
     * {@link SimpleAnnotationDetail} will be created and returned.
     *
     * @param annotation the given annotation
     * @param <T>        the type of the given annotation
     * @param <D>        the type of the detail instance, must be a subtype of {@link AnnotationDetail}
     * @return a new instance of {@link AnnotationDetail} as the detail type for the given annotation
     */
    static <T extends Annotation, D extends AnnotationDetail<T>> @Nonnull D newDetail(@Nonnull T annotation) {
        // if (annotation.annotationType().equals(DatePattern.class)) {
        //     return Fs.as(new DatePatternDetail((DatePattern) annotation));
        // }
        // if (annotation.annotationType().equals(NumberPattern.class)) {
        //     return Fs.as(new NumberPatternDetail((NumberPattern) annotation));
        // }
        Class<?> annotationClass = annotation.annotationType();
        DetailType detailType = annotationClass.getAnnotation(DetailType.class);
        if (detailType == null) {
            return Fs.as(new SimpleAnnotationDetail<>(annotation));
        }
        try {
            Class<?> detailClass = detailType.value();
            Constructor<?> constructor = detailClass.getConstructor(annotationClass);
            return Fs.as(constructor.newInstance(annotation));
        } catch (Exception e) {
            return Fs.as(new SimpleAnnotationDetail<>(annotation));
        }
    }

    /**
     * Returns the original annotation instance.
     *
     * @return the original annotation instance
     */
    @Nonnull
    T annotation();
}
