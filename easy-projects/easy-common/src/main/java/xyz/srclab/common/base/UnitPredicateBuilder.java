package xyz.srclab.common.base;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.collection.CollectionKit;
import xyz.srclab.common.design.builder.CachedBuilder;
import xyz.srclab.common.lang.finder.Finder;

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

        private final Finder<Class<?>, Class<?>> passTypeFinder;
        private final Finder<Class<?>, Class<?>> failTypeFinder;
        private final @Nullable Predicate<Class<?>> extraPredicate;

        private UnitPredicateImpl(UnitPredicateBuilder builder) {
            Class<?>[] passTypesClasses = ArrayKit.toArray(builder.passTypes, Class.class);
            Class<?>[] failTypesClasses = ArrayKit.toArray(builder.failTypes, Class.class);
            this.passTypeFinder = Finder.newPredicatePairFinder(passTypesClasses, Cast::canCast);
            this.failTypeFinder = Finder.newPredicatePairFinder(failTypesClasses, Cast::canCast);
            this.extraPredicate = builder.extraPredicate;
        }

        @Override
        public boolean test(Class<?> cls) {
            if (passTypeFinder.has(cls)) {
                return true;
            }
            if (extraPredicate == null) {
                return false;
            }
            if (failTypeFinder.has(cls)) {
                return false;
            }
            return extraPredicate.test(cls);
        }
    }
}
