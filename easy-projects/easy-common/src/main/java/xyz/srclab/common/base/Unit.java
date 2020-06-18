package xyz.srclab.common.base;

import java.util.function.Predicate;

/**
 * @author sunqian
 */
public class Unit {

    public static Predicate<Class<?>> defaultUnitPredicate() {
        return UnitPredicateHolder.INSTANCE;
    }

    public static UnitPredicateBuilder newUnitPredicateBuilder() {
        return UnitPredicateBuilder.newBuilder();
    }

    private static final class UnitPredicateHolder {

        private static final Class<?>[] PASS_TYPES = {

        };

        private static final Class<?>[] FAIL_TYPES = {

        };

        public static final Predicate<Class<?>> INSTANCE = newUnitPredicateBuilder()
                .passTypes(PASS_TYPES)
                .failTypes(FAIL_TYPES)
                .build();
    }
}
