package xyz.srclab.common.base;

import xyz.srclab.common.record.Record;

import java.lang.annotation.Annotation;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
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
                Enum.class,
                Annotation.class,
                CharSequence.class,
                Number.class,
                Date.class,
                TemporalAccessor.class,
                TemporalAmount.class,
        };

        private static final Class<?>[] FAIL_TYPES = {
                Record.class,
                Collection.class,
                Map.class,
                Object[].class,
                boolean[].class,
                byte[].class,
                short[].class,
                char[].class,
                int[].class,
                long[].class,
                float[].class,
                double[].class,
        };

        public static final Predicate<Class<?>> INSTANCE = newUnitPredicateBuilder()
                .passTypes(PASS_TYPES)
                .failTypes(FAIL_TYPES)
                .build();
    }
}
