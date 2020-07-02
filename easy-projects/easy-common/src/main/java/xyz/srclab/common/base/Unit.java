package xyz.srclab.common.base;

import xyz.srclab.common.record.Record;

import java.lang.annotation.Annotation;
import java.math.BigInteger;
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
                CharSequence.class,
                Enum.class,
                Annotation.class,
                Number.class,
                Date.class,
                TemporalAccessor.class,
                TemporalAmount.class,
                String.class,
                boolean.class, Integer.class,
                byte.class, Integer.class,
                short.class, Integer.class,
                char.class, Integer.class,
                int.class, Integer.class,
                long.class, Integer.class,
                float.class, Integer.class,
                double.class, Integer.class,
                void.class, Integer.class,
                BigInteger.class,

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
