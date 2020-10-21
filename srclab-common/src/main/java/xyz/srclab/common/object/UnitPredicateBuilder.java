package xyz.srclab.common.object;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.CollectionKit;
import xyz.srclab.common.design.builder.BaseProductCachingBuilder;
import xyz.srclab.common.lang.finder.Finder;
import xyz.srclab.common.reflect.TypeKit;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * @author sunqian
 */
public class UnitPredicateBuilder extends BaseProductCachingBuilder<UnitPredicate> {

    public static UnitPredicateBuilder newBuilder() {
        return new UnitPredicateBuilder();
    }

    private @Nullable List<Type> passTypes;
    private @Nullable List<Type> failTypes;
    private @Nullable List<Type> predicatePassTypes;
    private @Nullable BiPredicate<Type, Type> passTypePredicate;
    private @Nullable List<Type> predicateFailTypes;
    private @Nullable BiPredicate<Type, Type> failTypePredicate;

    private @Nullable UnitPredicate extraPredicate;

    public UnitPredicateBuilder passTypes(Type... passTypes) {
        return passTypes(Arrays.asList(passTypes));
    }

    public UnitPredicateBuilder passTypes(Iterable<Type> passTypes) {
        CollectionKit.addAll(passTypes(), passTypes);
        updateState();
        return this;
    }

    public UnitPredicateBuilder failTypes(Type... failTypes) {
        return failTypes(Arrays.asList(failTypes));
    }

    public UnitPredicateBuilder failTypes(Iterable<Type> failTypes) {
        CollectionKit.addAll(failTypes(), failTypes);
        updateState();
        return this;
    }

    public UnitPredicateBuilder predicatePassTypes(Type... predicatePassTypes) {
        return predicatePassTypes(Arrays.asList(predicatePassTypes));
    }

    public UnitPredicateBuilder predicatePassTypes(Iterable<Type> predicatePassTypes) {
        CollectionKit.addAll(predicatePassTypes(), predicatePassTypes);
        updateState();
        return this;
    }

    public UnitPredicateBuilder passTypePredicate(BiPredicate<Type, Type> passTypePredicate) {
        this.passTypePredicate = passTypePredicate;
        updateState();
        return this;
    }

    public UnitPredicateBuilder predicateFailTypes(Type... predicateFailTypes) {
        return predicateFailTypes(Arrays.asList(predicateFailTypes));
    }

    public UnitPredicateBuilder predicateFailTypes(Iterable<Type> predicateFailTypes) {
        CollectionKit.addAll(predicateFailTypes(), predicateFailTypes);
        updateState();
        return this;
    }

    public UnitPredicateBuilder failTypePredicate(BiPredicate<Type, Type> failTypePredicate) {
        this.failTypePredicate = failTypePredicate;
        updateState();
        return this;
    }

    public UnitPredicateBuilder extraPredicate(UnitPredicate predicate) {
        this.extraPredicate = predicate;
        updateState();
        return this;
    }

    private List<Type> passTypes() {
        if (passTypes == null) {
            passTypes = new LinkedList<>();
        }
        return passTypes;
    }

    private List<Type> failTypes() {
        if (failTypes == null) {
            failTypes = new LinkedList<>();
        }
        return failTypes;
    }

    private List<Type> predicatePassTypes() {
        if (predicatePassTypes == null) {
            predicatePassTypes = new LinkedList<>();
        }
        return predicatePassTypes;
    }

    private List<Type> predicateFailTypes() {
        if (predicateFailTypes == null) {
            predicateFailTypes = new LinkedList<>();
        }
        return predicateFailTypes;
    }

    @Override
    protected UnitPredicate buildNew() {
        return new UnitPredicateImpl(this);
    }

    public UnitPredicate build() {
        return buildCaching();
    }

    private static final class UnitPredicateImpl implements UnitPredicate {

        private static final BiPredicate<Type, Type> DEFAULT_TYPE_PREDICATE =
                (input, type) -> TypeKit.getRawClass(type).isAssignableFrom(TypeKit.getRawClass(input));

        private final Finder<Type, Type> passTypeFinder;
        private final Finder<Type, Type> failTypeFinder;
        private final Finder<Type, Type> predicatePassTypeFinder;
        private final Finder<Type, Type> predicateFailTypeFinder;
        private final @Nullable UnitPredicate extraPredicate;

        private UnitPredicateImpl(UnitPredicateBuilder builder) {
            this.passTypeFinder = builder.passTypes().isEmpty() ? Finder.emptyFinder() :
                    Finder.hashFinder(builder.passTypes());
            this.failTypeFinder = builder.failTypes().isEmpty() ? Finder.emptyFinder() :
                    Finder.hashFinder(builder.failTypes());
            this.predicatePassTypeFinder = builder.predicatePassTypes().isEmpty() ? Finder.emptyFinder() :
                    Finder.hashPredicateFinder(builder.predicatePassTypes(),
                            builder.passTypePredicate == null ? DEFAULT_TYPE_PREDICATE : builder.passTypePredicate);
            this.predicateFailTypeFinder = builder.predicateFailTypes().isEmpty() ? Finder.emptyFinder() :
                    Finder.hashPredicateFinder(builder.predicateFailTypes(),
                            builder.failTypePredicate == null ? DEFAULT_TYPE_PREDICATE : builder.failTypePredicate);
            this.extraPredicate = builder.extraPredicate;
        }

        @Override
        public boolean test(Type type) {
            if (passTypeFinder.has(type)) {
                return true;
            }
            if (failTypeFinder.has(type)) {
                return false;
            }
            if (predicatePassTypeFinder.has(type)) {
                return true;
            }
            if (predicateFailTypeFinder.has(type)) {
                return false;
            }
            if (extraPredicate == null) {
                return false;
            }
            return extraPredicate.test(type);
        }
    }
}
