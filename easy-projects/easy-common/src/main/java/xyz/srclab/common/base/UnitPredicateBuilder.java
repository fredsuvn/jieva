package xyz.srclab.common.base;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.collection.CollectionKit;
import xyz.srclab.common.design.builder.CachedBuilder;
import xyz.srclab.common.lang.finder.Finder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * @author sunqian
 */
public class UnitPredicateBuilder extends CachedBuilder<Predicate<Class<?>>> {

    public static UnitPredicateBuilder newBuilder() {
        return new UnitPredicateBuilder();
    }

    private @Nullable List<Class<?>> passTypes;
    private @Nullable List<Class<?>> failTypes;
    private @Nullable List<Class<?>> predicateTypes;
    private @Nullable BiPredicate<Class<?>, Class<?>> typePredicate;
    private @Nullable Predicate<Class<?>> extraPredicate;

    private UnitPredicateBuilder() {
    }

    public UnitPredicateBuilder passTypes(Class<?>... passTypes) {
        return passTypes(Arrays.asList(passTypes));
    }

    public UnitPredicateBuilder passTypes(Iterable<Class<?>> passTypes) {
        CollectionKit.addAll(passTypes(), passTypes);
        updateState();
        return this;
    }

    public UnitPredicateBuilder failTypes(Class<?>... failTypes) {
        return failTypes(Arrays.asList(failTypes));
    }

    public UnitPredicateBuilder failTypes(Iterable<Class<?>> failTypes) {
        CollectionKit.addAll(failTypes(), failTypes);
        updateState();
        return this;
    }

    public UnitPredicateBuilder predicateTypes(Class<?>... predicateTypes) {
        return failTypes(Arrays.asList(predicateTypes));
    }

    public UnitPredicateBuilder predicateTypes(Iterable<Class<?>> predicateTypes) {
        CollectionKit.addAll(predicateTypes(), predicateTypes);
        updateState();
        return this;
    }

    public UnitPredicateBuilder extraPredicate(Predicate<Class<?>> predicate) {
        this.extraPredicate = predicate;
        updateState();
        return this;
    }

    private List<Class<?>> passTypes() {
        if (passTypes == null) {
            passTypes = new LinkedList<>();
        }
        return passTypes;
    }

    private List<Class<?>> failTypes() {
        if (failTypes == null) {
            failTypes = new LinkedList<>();
        }
        return failTypes;
    }

    private List<Class<?>> predicateTypes() {
        if (predicateTypes == null) {
            predicateTypes = new LinkedList<>();
        }
        return predicateTypes;
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
            this.passTypeFinder = Finder.pairHashPredicateFinder(passTypesClasses, Cast::canCast);
            this.failTypeFinder = Finder.pairHashPredicateFinder(failTypesClasses, Cast::canCast);
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
