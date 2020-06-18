package xyz.srclab.common.base;

import java.util.function.Predicate;

/**
 * @author sunqian
 */
public class Unit {

    public static UnitPredicateBuilder newUnitPredicateBuilder() {
        return UnitPredicateBuilder.newBuilder();
    }

    public static Predicate<Class<?>> unitPredicate() {
        return UnitPredicate.INSTANCE;
    }

    private static final class UnitPredicateTable {

        public static final Predicate<Class<?>> UNIT_PREDICATE = newUnitPredicateBuilder()
                .

        private static final Class<?>[] INCLUDE_TYPES = {

        };

        private static final Class<?>[] EXCLUDE_TYPES = {

        };
    }
}
