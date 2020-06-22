package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.convert.ConvertHandler;
import xyz.srclab.common.convert.Converter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.function.Function;

/**
 * @author sunqian
 */
public class NumberConvertHandler implements ConvertHandler {

    @Override
    public @Nullable Object convert(Object from, Type to, Converter converter) {
        @Nullable ConvertFunction convertFunction = ConvertFunctionFinder.find(to);
        if (convertFunction == null) {
            return null;
        }
        return convertFunction.apply(from);
    }

    private static final class ConvertFunctionFinder {

        private static final Map<Type, ConvertFunction> table = MapKit.pairToMap(ConvertFunctionTable.TABLE);

        @Nullable
        public static ConvertFunction find(Type type) {
            return table.get(type);
        }

        private static final class ConvertFunctionTable {

            private static final ConvertFunction TO_BYTE = ConvertFunctionTable::toByte;
            private static final ConvertFunction TO_SHORT = ConvertFunctionTable::toShort;
            private static final ConvertFunction TO_CHAR = ConvertFunctionTable::toChar;
            private static final ConvertFunction TO_INT = ConvertFunctionTable::toInt;
            private static final ConvertFunction TO_LONG = ConvertFunctionTable::toLong;
            private static final ConvertFunction TO_FLOAT = ConvertFunctionTable::toFloat;
            private static final ConvertFunction TO_DOUBLE = ConvertFunctionTable::toDouble;
            private static final ConvertFunction TO_BIG_INTEGER = ConvertFunctionTable::toBigInteger;
            private static final ConvertFunction TO_BIG_DECIMAL = ConvertFunctionTable::toBigDecimal;

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

    private interface ConvertFunction extends Function<Object, Object> {
    }
}
