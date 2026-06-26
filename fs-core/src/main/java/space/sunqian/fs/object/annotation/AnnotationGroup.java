package space.sunqian.fs.object.annotation;

import space.sunqian.annotation.CachedResult;
import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.collect.ListKit;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a group of annotation instances along with their detailed information.
 *
 * @author sunqian
 */
@Immutable
public interface AnnotationGroup {

    /**
     * Returns an instance of {@link AnnotationGroup} from the given {@link AnnotatedElement}.
     * <p>
     * This method will use the global cache from {@link Fs#globalCaches()} to store the result.
     *
     * @param annotatedElement the given {@link AnnotatedElement}
     * @return an instance of {@link AnnotationGroup} from the given {@link AnnotatedElement}
     */
    @CachedResult
    static @Nonnull AnnotationGroup from(@Nonnull AnnotatedElement annotatedElement) {
        return AnnotationBack.getGroup(annotatedElement, AnnotationGroup::newGroup);
    }

    /**
     * Returns a new {@link AnnotationGroup} from the given {@link AnnotatedElement}.
     *
     * @param annotatedElement the given {@link AnnotatedElement}
     * @return a new {@link AnnotationGroup} from the given {@link AnnotatedElement}
     */
    static @Nonnull AnnotationGroup newGroup(@Nonnull AnnotatedElement annotatedElement) {
        return AnnotationBack.newGroup(annotatedElement);
    }

    /**
     * Combines the given {@link AnnotationGroup} list into a new {@link AnnotationGroup} and returns it.
     * <p>
     * If the given {@link AnnotationGroup} list is empty, returns an empty {@link AnnotationGroup}; if the given
     * {@link AnnotationGroup} list has only one element, returns the only element {@link AnnotationGroup}; otherwise,
     * returns a new {@link AnnotationGroup} whose contents come from the non-empty elements of the given
     * {@link AnnotationGroup} list, and the search order of the search methods are also in the order of the elements.
     *
     * @param annotationGroups the given {@link AnnotationGroup} list
     * @return the combined {@link AnnotationGroup} from the given {@link AnnotationGroup} list
     */
    @SuppressWarnings("EnhancedSwitchMigration")
    static @Nonnull AnnotationGroup combine(@Nonnull List<@Nonnull AnnotationGroup> annotationGroups) {
        switch (annotationGroups.size()) {
            case 0:
                return empty();
            case 1:
                return annotationGroups.get(0);
            default:
                for (AnnotationGroup as : annotationGroups) {
                    if (as.isEmpty()) {
                        return combine(annotationGroups.stream()
                            .filter(a -> !a.isEmpty())
                            .collect(Collectors.toList()));
                    }
                }
                return AnnotationBack.combine(annotationGroups);
        }
    }

    /**
     * Combines the given {@link AnnotationGroup} array into a new {@link AnnotationGroup} and returns it.
     * <p>
     * If the given {@link AnnotationGroup} array is empty, returns an empty {@link AnnotationGroup}; if the given
     * {@link AnnotationGroup} array has only one element, returns the only element {@link AnnotationGroup}; otherwise,
     * returns a new {@link AnnotationGroup} whose contents come from the non-empty elements of the given
     * {@link AnnotationGroup} array, and the search order of the search methods are also in the order of the elements.
     *
     * @param annotationGroups the given {@link AnnotationGroup} array
     * @return the combined {@link AnnotationGroup} from the given {@link AnnotationGroup} array
     */
    static @Nonnull AnnotationGroup combine(@Nonnull AnnotationGroup @Nonnull ... annotationGroups) {
        return combine(ListKit.list(annotationGroups));
    }

    /**
     * Returns an empty {@link AnnotationGroup}.
     *
     * @return an empty {@link AnnotationGroup}
     */
    static @Nonnull AnnotationGroup empty() {
        return AnnotationBack.empty();
    }

    /**
     * Returns all annotation instances contained in this group.
     *
     * @return an immutable list containing all annotation instances in this group
     */
    @Nonnull
    @Immutable
    List<@Nonnull Annotation> annotations();

    /**
     * Returns the annotation instance of the specified type from this group.
     * <p>
     * If the annotation of the specified type is not present in this group, returns {@code null}.
     *
     * @param <T>             the annotation type
     * @param annotationClass the class object representing the annotation type to retrieve
     * @return the annotation instance of the specified type, or {@code null} if not present
     */
    <T extends Annotation> @Nullable T annotation(@Nonnull Class<T> annotationClass);

    /**
     * Returns all annotation detail objects contained in this group.
     *
     * @return an immutable list containing all annotation detail objects in this group
     */
    @Nonnull
    @Immutable
    List<@Nonnull AnnotationDetail<?>> details();

    /**
     * Returns the annotation detail object of the specified type from this group.
     * <p>
     * If the annotation detail of the specified type is not present in this group, returns {@code null}.
     *
     * @param <D>         the annotation detail type
     * @param detailClass the class object representing the annotation detail type to retrieve
     * @return the annotation detail object of the specified type, or {@code null} if not present
     */
    <D extends AnnotationDetail<?>> @Nullable D detail(@Nonnull Class<D> detailClass);

    /**
     * Returns the annotation detail object for the specified annotation type from this group.
     * <p>
     * If the annotation detail for the specified annotation type is not present in this group, returns {@code null}.
     *
     * @param <T>             the annotation type
     * @param <D>             the annotation detail type
     * @param annotationClass the class object representing the annotation type to retrieve
     * @return the annotation detail object for the specified annotation type, or {@code null} if not present
     */
    <T extends Annotation, D extends AnnotationDetail<T>> @Nullable D detailFor(
        @Nonnull Class<T> annotationClass
    );

    /**
     * Returns whether this {@link AnnotationGroup} is empty.
     *
     * @return whether this {@link AnnotationGroup} is empty
     */
    boolean isEmpty();
}