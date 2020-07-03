package xyz.srclab.common.object;

import xyz.srclab.common.record.Record;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @author sunqian
 */
final class UnitPredicateSupport {

    static UnitPredicate defaultPredicate() {
        return UnitPredicateHolder.INSTANCE;
    }

    private static final class UnitPredicateHolder {

        private static final Class<?>[] PASS_TYPES = {
                String.class,
                boolean.class, Boolean.class,
                byte.class, Byte.class,
                short.class, Short.class,
                char.class, Character.class,
                int.class, Integer.class,
                long.class, Long.class,
                float.class, Float.class,
                double.class, Double.class,
                void.class, Void.class,
                BigInteger.class,
                BigDecimal.class,
                Calendar.class,
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

        private static final Class<?>[] PREDICATE_PASS_TYPES = {
                CharSequence.class,
                Enum.class,
                Annotation.class,
                Number.class,
                Date.class,
                TemporalAccessor.class,
                TemporalAmount.class,
        };

        public static final UnitPredicate INSTANCE = UnitPredicate.newUBuilder()
                .passTypes(PASS_TYPES)
                .failTypes(FAIL_TYPES)
                .predicatePassTypes(PREDICATE_PASS_TYPES)
                .predicateFailTypes(FAIL_TYPES)
                .build();
    }
}
