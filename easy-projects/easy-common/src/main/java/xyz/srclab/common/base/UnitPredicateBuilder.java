package xyz.srclab.common.base;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.collection.CollectionKit;
import xyz.srclab.common.pattern.builder.CachedBuilder;

import java.util.Arrays;
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

    private final List<Class<?>> passTypes = new LinkedList<>();
    private final List<Class<?>> failTypes = new LinkedList<>();
    private @Nullable Predicate<Class<?>> extraPredicate;

    private UnitPredicateBuilder() {
    }

    public UnitPredicateBuilder passTypes(Class<?>... passTypes) {
        return passTypes(Arrays.asList(passTypes));
    }

    public UnitPredicateBuilder passTypes(Iterable<Class<?>> passTypes) {
        CollectionKit.addAll(this.passTypes, passTypes);
        updateState();
        return this;
    }

    public UnitPredicateBuilder failTypes(Class<?>... failTypes) {
        return failTypes(Arrays.asList(failTypes));
    }

    public UnitPredicateBuilder failTypes(Iterable<Class<?>> failTypes) {
        CollectionKit.addAll(this.failTypes, failTypes);
        updateState();
        return this;
    }

    public UnitPredicateBuilder extraPredicate(Predicate<Class<?>> predicate) {
        this.extraPredicate = predicate;
        updateState();
        return this;
    }

    @Override
    protected Predicate<Class<?>> buildNew() {
        return new UnitPredicateImpl(this);
    }

    private static final class UnitPredicateImpl implements Predicate<Class<?>> {

        private final Class<?>[] passTypes;
        private final Class<?>[] failTypes;
        private final @Nullable Predicate<Class<?>> extraPredicate;

        private UnitPredicateImpl(UnitPredicateBuilder builder) {
            this.passTypes = ArrayKit.toArray(builder.passTypes, Class.class);
            this.failTypes = ArrayKit.toArray(builder.failTypes, Class.class);
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
            for (Class<?> includeType : passTypes) {
                if (includeType.isAssignableFrom(aClass)) {
                    return true;
                }
            }
            return false;
        }

        private boolean slowTest(Class<?> aClass) {
            for (Class<?> includeType : passTypes) {
                if (includeType.isAssignableFrom(aClass)) {
                    return true;
                }
            }
            for (Class<?> excludeType : failTypes) {
                if (excludeType.isAssignableFrom(aClass)) {
                    return false;
                }
            }
            assert extraPredicate != null;
            return extraPredicate.test(aClass);
        }
    }
}
