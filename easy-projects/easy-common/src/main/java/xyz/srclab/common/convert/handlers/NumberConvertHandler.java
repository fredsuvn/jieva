package xyz.srclab.common.convert.handlers;

import xyz.srclab.common.lang.finder.Finder;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;

/**
 * @author sunqian
 */
public class NumberConvertHandler extends TypeFinderConvertHandler {

    private static final Finder<Type, Function<Object, Object>> finder =
            Finder.newPairFinder(ConvertFunctions.TABLE);

    @Override
    protected Finder<Type, Function<Object, Object>> getFinder() {
        return finder;
    }

    private static final class ConvertFunctions {

        private static final Function<Object, Object> TO_BYTE = ConvertFunctions::toByte;
        private static final Function<Object, Object> TO_SHORT = ConvertFunctions::toShort;
        private static final Function<Object, Object> TO_CHAR = ConvertFunctions::toChar;
        private static final Function<Object, Object> TO_INT = ConvertFunctions::toInt;
        private static final Function<Object, Object> TO_LONG = ConvertFunctions::toLong;
        private static final Function<Object, Object> TO_FLOAT = ConvertFunctions::toFloat;
        private static final Function<Object, Object> TO_DOUBLE = ConvertFunctions::toDouble;
        private static final Function<Object, Object> TO_BIG_INTEGER = ConvertFunctions::toBigInteger;
        private static final Function<Object, Object> TO_BIG_DECIMAL = ConvertFunctions::toBigDecimal;

        private static final Object[] TABLE = {
                byte.class, TO_BYTE,
                short.class, TO_SHORT,
                char.class, TO_CHAR,
                int.class, TO_INT,
                long.class, TO_LONG,
                float.class, TO_FLOAT,
                double.class, TO_DOUBLE,
                BigInteger.class, TO_BIG_INTEGER,
                BigDecimal.class, TO_BIG_DECIMAL,
                Byte.class, TO_BYTE,
                Short.class, TO_SHORT,
                Character.class, TO_CHAR,
                Integer.class, TO_INT,
                Long.class, TO_LONG,
                Float.class, TO_FLOAT,
                Double.class, TO_DOUBLE,
        };

        private static byte toByte(Object from) {
            if (from instanceof Number) {
                return ((Number) from).byteValue();
            }
            return Byte.parseByte(from.toString());
        }

        private static short toShort(Object from) {
            if (from instanceof Number) {
                return ((Number) from).shortValue();
            }
            return Short.parseShort(from.toString());
        }

        private static char toChar(Object from) {
            return (char) toInt(from);
        }

        private static int toInt(Object from) {
            if (from instanceof Number) {
                return ((Number) from).intValue();
            }
            return Integer.parseInt(from.toString());
        }

        private static long toLong(Object from) {
            if (from instanceof Number) {
                return ((Number) from).longValue();
            }
            return Long.parseLong(from.toString());
        }

        private static float toFloat(Object from) {
            if (from instanceof Number) {
                return ((Number) from).floatValue();
            }
            return Float.parseFloat(from.toString());
        }

        private static double toDouble(Object from) {
            if (from instanceof Number) {
                return ((Number) from).doubleValue();
            }
            return Double.parseDouble(from.toString());
        }

        private static BigInteger toBigInteger(Object from) {
            if (from instanceof BigInteger) {
                return (BigInteger) from;
            }
            if (from instanceof BigDecimal) {
                return ((BigDecimal) from).toBigInteger();
            }
            return new BigInteger(from.toString());
        }

        private static BigDecimal toBigDecimal(Object from) {
            if (from instanceof BigDecimal) {
                return (BigDecimal) from;
            }
            return new BigDecimal(from.toString());
        }
    }
}
