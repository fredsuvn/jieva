package xyz.srclab.common.base;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.pattern.builder.CachedBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author sunqian
 */
public class UnitPredicateBuilder extends CachedBuilder<Predicate<Class<?>>> {

    public static UnitPredicateBuilder newBuilder() {
        return new UnitPredicateBuilder();
    }

    private final List<Class<?>> includeTypes = new LinkedList<>();
    private final List<Class<?>> excludeTypes = new LinkedList<>();
    private @Nullable Predicate<Class<?>> extraPredicate;

    private UnitPredicateBuilder() {
    }



    @Override
    protected Predicate<Class<?>> buildNew() {
        return new UnitPredicateImpl(this);
    }

    private static final class UnitPredicateImpl implements Predicate<Class<?>> {

        private final Class<?>[] includeTypes;
        private final Class<?>[] excludeTypes;
        private final @Nullable Predicate<Class<?>> extraPredicate;

        private UnitPredicateImpl(UnitPredicateBuilder builder) {
            this.includeTypes = ArrayKit.toArray(builder.includeTypes, Class.class);
            this.excludeTypes = ArrayKit.toArray(builder.excludeTypes, Class.class);
            this.extraPredicate = builder.extraPredicate;
        }

        @Override
        public boolean test(Class<?> aClass) {
            if (extraPredicate == null) {
                return fastTest(aClass);
            }
            return slowTest(aClass);
        }

        private boolean fastTest(Class<?> aClass) {
            for (Class<?> includeType : includeTypes) {
                if (includeType.isAssignableFrom(aClass)) {
                    return true;
                }
            }
            return false;
        }

        private boolean slowTest(Class<?> aClass) {
            for (Class<?> includeType : includeTypes) {
                if (includeType.isAssignableFrom(aClass)) {
                    return true;
                }
            }
            for (Class<?> excludeType : excludeTypes) {
                if (excludeType.isAssignableFrom(aClass)) {
                    return false;
                }
            }
            assert extraPredicate != null;
            return extraPredicate.test(aClass);
        }
    }
}
