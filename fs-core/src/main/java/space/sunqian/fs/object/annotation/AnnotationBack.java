package space.sunqian.fs.object.annotation;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.Fs;
import space.sunqian.fs.cache.SimpleCache;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

final class AnnotationBack {

    static @Nonnull AnnotationGroup getGroup(
        @Nonnull AnnotatedElement annotatedElement,
        @Nonnull Function<@Nonnull AnnotatedElement, @Nonnull AnnotationGroup> function
    ) {
        return Cache.get(annotatedElement, function);
    }

    static @Nonnull AnnotationGroup newGroup(@Nonnull AnnotatedElement annotatedElement) {
        return new AnnotationGroupImpl(annotatedElement);
    }

    static @Nonnull AnnotationGroup combine(@Nonnull List<@Nonnull AnnotationGroup> annotationGroups) {
        return new CombinedAnnotationGroup(annotationGroups);
    }

    static @Nonnull AnnotationGroup empty() {
        return EmptyGroup.INST;
    }

    private static final class AnnotationGroupImpl implements AnnotationGroup {

        private final @Nonnull Annotation @Nonnull [] annotations;
        private final @Nonnull List<@Nonnull Annotation> annotationList;
        private final @Nonnull AnnotationDetail<?> @Nonnull [] details;
        private final @Nonnull List<@Nonnull AnnotationDetail<?>> detailList;

        private AnnotationGroupImpl(@Nonnull AnnotatedElement annotatedElement) {
            this.annotations = annotatedElement.getAnnotations();
            this.annotationList = Fs.list(annotations);
            this.details = new AnnotationDetail<?>[this.annotations.length];
            for (int i = 0; i < details.length; i++) {
                details[i] = AnnotationDetail.newDetail(this.annotations[i]);
            }
            this.detailList = Fs.list(details);
        }

        @Override
        public @Nonnull List<@Nonnull Annotation> annotations() {
            return annotationList;
        }

        @Override
        public <T extends Annotation> @Nullable T annotation(@Nonnull Class<T> annotationClass) {
            return Fs.as(
                annotationList.stream()
                    .filter(a -> a.annotationType().equals(annotationClass))
                    .findFirst()
                    .orElse(null)
            );
        }

        @Override
        public @Nonnull List<@Nonnull AnnotationDetail<?>> details() {
            return detailList;
        }

        @Override
        public <D extends AnnotationDetail<?>> @Nullable D detail(@Nonnull Class<D> detailClass) {
            return Fs.as(
                detailList.stream()
                    .filter(a -> a.getClass().equals(detailClass))
                    .findFirst()
                    .orElse(null)
            );
        }

        @Override
        public <T extends Annotation, D extends AnnotationDetail<T>> D detailFor(
            @Nonnull Class<T> annotationClass
        ) {
            for (int i = 0; i < annotations.length; i++) {
                if (annotations[i].annotationType().equals(annotationClass)) {
                    return Fs.as(details[i]);
                }
            }
            return null;
        }

        @Override
        public boolean isEmpty() {
            return annotationList.isEmpty();
        }
    }

    private static final class CombinedAnnotationGroup implements AnnotationGroup {

        private final @Nonnull List<@Nonnull AnnotationGroup> annotationGroups;

        private CombinedAnnotationGroup(@Nonnull @RetainedParam List<@Nonnull AnnotationGroup> annotationGroups) {
            this.annotationGroups = annotationGroups;
        }

        @Override
        public @Nonnull List<@Nonnull Annotation> annotations() {
            ArrayList<Annotation> annotationList = new ArrayList<>();
            for (AnnotationGroup annotationGroup : annotationGroups) {
                annotationList.addAll(annotationGroup.annotations());
            }
            annotationList.trimToSize();
            return annotationList;
        }

        @Override
        public <T extends Annotation> @Nullable T annotation(@Nonnull Class<T> annotationClass) {
            for (AnnotationGroup annotationGroup : annotationGroups) {
                T ret = annotationGroup.annotation(annotationClass);
                if (ret != null) {
                    return ret;
                }
            }
            return null;
        }

        @Override
        public @Nonnull List<@Nonnull AnnotationDetail<?>> details() {
            ArrayList<AnnotationDetail<?>> annotationDetailList = new ArrayList<>();
            for (AnnotationGroup annotationGroup : annotationGroups) {
                annotationDetailList.addAll(annotationGroup.details());
            }
            annotationDetailList.trimToSize();
            return annotationDetailList;
        }

        @Override
        public <D extends AnnotationDetail<?>> @Nullable D detail(@Nonnull Class<D> detailClass) {
            for (AnnotationGroup annotationGroup : annotationGroups) {
                D ret = annotationGroup.detail(detailClass);
                if (ret != null) {
                    return ret;
                }
            }
            return null;
        }

        @Override
        public <T extends Annotation, D extends AnnotationDetail<T>> @Nullable D detailFor(
            @Nonnull Class<T> annotationClass
        ) {
            for (AnnotationGroup annotationGroup : annotationGroups) {
                D ret = annotationGroup.detailFor(annotationClass);
                if (ret != null) {
                    return ret;
                }
            }
            return null;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }

    private enum EmptyGroup implements AnnotationGroup {
        INST;

        @Override
        public @Nonnull List<@Nonnull Annotation> annotations() {
            return Collections.emptyList();
        }

        @Override
        public <T extends Annotation> @Nullable T annotation(@Nonnull Class<T> annotationClass) {
            return null;
        }

        @Override
        public @Nonnull List<@Nonnull AnnotationDetail<?>> details() {
            return Collections.emptyList();
        }

        @Override
        public <D extends AnnotationDetail<?>> @Nullable D detail(@Nonnull Class<D> detailClass) {
            return null;
        }

        @Override
        public <T extends Annotation, D extends AnnotationDetail<T>> @Nullable D detailFor(@Nonnull Class<T> annotationClass) {
            return null;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    }

    private static final class Cache {

        private static final @Nonnull SimpleCache<
            @Nonnull AnnotatedElement,
            @Nonnull AnnotationGroup
            > CACHE = SimpleCache.ofSoft();

        static {
            Fs.registerGlobalCache(CACHE);
        }

        private static @Nonnull AnnotationGroup get(
            @Nonnull AnnotatedElement annotatedElement,
            @Nonnull Function<@Nonnull AnnotatedElement, @Nonnull AnnotationGroup> function
        ) {
            return CACHE.get(annotatedElement, function);
        }

        private Cache() {
        }
    }

    private AnnotationBack() {
    }
}
