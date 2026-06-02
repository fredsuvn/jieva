package space.sunqian.fs.object.annotation;

import space.sunqian.annotation.CachedResult;
import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.collect.ListKit;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a collection of annotations along with their detailed information.
 *
 * @author sunqian
 */
@Immutable
public interface AnnotationSet {

    /**
     * Returns an instance of {@link AnnotationSet} from the given {@link AnnotatedElement}.
     *
     * @param annotatedElement the given {@link AnnotatedElement}
     * @return an instance of {@link AnnotationSet} from the given {@link AnnotatedElement}
     */
    @CachedResult
    static @Nonnull AnnotationSet from(@Nonnull AnnotatedElement annotatedElement) {
        return AnnotationBack.getSet(annotatedElement, AnnotationSet::newSet);
    }

    /**
     * Returns a new {@link AnnotationSet} from the given {@link AnnotatedElement}.
     *
     * @param annotatedElement the given {@link AnnotatedElement}
     * @return a new {@link AnnotationSet} from the given {@link AnnotatedElement}
     */
    static @Nonnull AnnotationSet newSet(@Nonnull AnnotatedElement annotatedElement) {
        return AnnotationBack.newSet(annotatedElement);
    }

    /**
     * Returns a new {@link AnnotationSet} whose contents are come from the given {@link AnnotationSet}s. The search
     * order of the search method is the order of the given {@link AnnotationSet}s.
     * <p>
     * If the given {@link AnnotationSet}s is empty, returns an empty {@link AnnotationSet}; if the given
     * {@link AnnotationSet}s is only one, returns the only {@link AnnotationSet}; otherwise, returns a new multi
     * {@link AnnotationSet}.
     *
     * @param annotationSets the given {@link AnnotationSet}s
     * @return a new {@link AnnotationSet} whose contents are come from the given {@link AnnotationSet}s
     */
    @SuppressWarnings("EnhancedSwitchMigration")
    static @Nonnull AnnotationSet multiSet(@Nonnull List<@Nonnull AnnotationSet> annotationSets) {
        switch (annotationSets.size()) {
            case 0:
                return emptySet();
            case 1:
                return annotationSets.get(0);
            default:
                for (AnnotationSet as : annotationSets) {
                    if (as.isEmpty()) {
                        return multiSet(annotationSets.stream()
                            .filter(a -> !a.isEmpty())
                            .collect(Collectors.toList()));
                    }
                }
                return AnnotationBack.multiSet(annotationSets);
        }
    }

    /**
     * Returns a new {@link AnnotationSet} whose contents are come from the given {@link AnnotationSet}s. The search
     * order of the search method is the order of the given {@link AnnotationSet}s.
     * <p>
     * If the given {@link AnnotationSet}s is empty, returns an empty {@link AnnotationSet}; if the given
     * {@link AnnotationSet}s is only one, returns the only {@link AnnotationSet}; otherwise, returns a new multi
     * {@link AnnotationSet}.
     *
     * @param annotationSets the given {@link AnnotationSet}s
     * @return a new {@link AnnotationSet} whose contents are come from the given {@link AnnotationSet}s
     */
    static @Nonnull AnnotationSet multiSet(@Nonnull AnnotationSet @Nonnull @RetainedParam ... annotationSets) {
        return multiSet(ListKit.list(annotationSets));
    }

    /**
     * Returns an empty {@link AnnotationSet}.
     *
     * @return an empty {@link AnnotationSet}
     */
    static @Nonnull AnnotationSet emptySet() {
        return AnnotationBack.emptySet();
    }

    /**
     * Returns all annotation instances contained in this collection.
     *
     * @return an immutable list containing all annotation instances in this collection
     */
    @Nonnull
    @Immutable
    List<@Nonnull Annotation> annotations();

    /**
     * Returns the annotation instance of the specified type from this collection.
     * <p>
     * If the annotation of the specified type is not present in this collection, returns {@code null}.
     *
     * @param <T>             the annotation type
     * @param annotationClass the class object representing the annotation type to retrieve
     * @return the annotation instance of the specified type, or {@code null} if not present
     */
    <T extends Annotation> @Nullable T get(@Nonnull Class<T> annotationClass);

    /**
     * Returns all annotation detail objects contained in this collection.
     *
     * @return an immutable list containing all annotation detail objects in this collection
     */
    @Nonnull
    @Immutable
    List<@Nonnull AnnotationDetail<?>> details();

    /**
     * Returns the annotation detail object of the specified type from this collection.
     * <p>
     * If the annotation detail of the specified type is not present in this collection, returns {@code null}.
     *
     * @param <D>         the annotation detail type
     * @param detailClass the class object representing the annotation detail type to retrieve
     * @return the annotation detail object of the specified type, or {@code null} if not present
     */
    <D extends AnnotationDetail<?>> @Nullable D getDetail(@Nonnull Class<D> detailClass);

    /**
     * Returns the annotation detail object of the specified annotation type from this collection.
     * <p>
     * If the annotation detail of the specified annotation type is not present in this collection, returns
     * {@code null}.
     *
     * @param <T>             the annotation type
     * @param <D>             the annotation detail type
     * @param annotationClass the class object representing the annotation type to retrieve
     * @return the annotation detail object of the specified annotation type, or {@code null} if not present
     */
    <T extends Annotation, D extends AnnotationDetail<T>> @Nullable D getDetailByAnnotationType(
        @Nonnull Class<T> annotationClass
    );

    /**
     * Returns whether this {@link AnnotationSet} is empty.
     *
     * @return whether this {@link AnnotationSet} is empty
     */
    boolean isEmpty();
}