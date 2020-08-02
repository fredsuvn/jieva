package xyz.srclab.common.convert;

import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author sunqian
 */
public class ConvertKit {

    private static final Converter converter = Converter.defaultConverter();

    public static <T> T convert(Object from, Class<T> to) throws UnsupportedOperationException {
        return converter.convert(from, to);
    }

    public static <T> T convert(Object from, Type to) throws UnsupportedOperationException {
        return converter.convert(from, to);
    }

    public static <T> T convert(Object from, TypeRef<T> to) throws UnsupportedOperationException {
        return converter.convert(from, to);
    }

    public static String toString(Object any) throws UnsupportedOperationException {
        return converter.toString(any);
    }

    public static Date toDate(Object any) throws UnsupportedOperationException {
        return converter.toDate(any);
    }

    public static LocalDateTime toLocalDateTime(Object any) throws UnsupportedOperationException {
        return converter.toLocalDateTime(any);
    }

    public static Instant toInstant(Object any) throws UnsupportedOperationException {
        return converter.toInstant(any);
    }

    public static Duration toDuration(Object any) throws UnsupportedOperationException {
        return converter.toDuration(any);
    }

    public static boolean toBoolean(Object any) throws UnsupportedOperationException {
        return converter.toBoolean(any);
    }

    public static byte toByte(Object any) throws UnsupportedOperationException {
        return converter.toByte(any);
    }

    public static short toShort(Object any) throws UnsupportedOperationException {
        return converter.toShort(any);
    }

    public static char toChar(Object any) throws UnsupportedOperationException {
        return converter.toChar(any);
    }

    public static int toInt(Object any) throws UnsupportedOperationException {
        return converter.toInt(any);
    }

    public static long toLong(Object any) throws UnsupportedOperationException {
        return converter.toLong(any);
    }

    public static float toFloat(Object any) throws UnsupportedOperationException {
        return converter.toFloat(any);
    }

    public static double toDouble(Object any) throws UnsupportedOperationException {
        return converter.toDouble(any);
    }

    public static BigInteger toBigInteger(Object any) throws UnsupportedOperationException {
        return converter.toBigInteger(any);
    }

    public static BigDecimal toBigDecimal(Object any) throws UnsupportedOperationException {
        return converter.toBigDecimal(any);
    }
}
