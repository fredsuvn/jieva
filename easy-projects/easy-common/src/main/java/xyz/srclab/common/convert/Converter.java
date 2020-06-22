package xyz.srclab.common.convert;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Immutable
public interface Converter {

    static Converter defaultConverter() {
        return ConvertSupport.defaultConverter();
    }

    static ConverterBuilder newBuilder() {
        return ConverterBuilder.newBuilder();
    }

    <T> T convert(Object from, Class<T> to) throws UnsupportedOperationException;

    <T> T convert(Object from, Type to) throws UnsupportedOperationException;

    <T> T convert(Object from, TypeRef<T> to) throws UnsupportedOperationException;

    default String toString(Object any) throws UnsupportedOperationException {
        return convert(any, String.class);
    }

    default Date toDate(Object any) throws UnsupportedOperationException {
        return convert(any, Date.class);
    }

    default LocalDateTime toLocalDateTime(Object any) throws UnsupportedOperationException {
        return convert(any, LocalDateTime.class);
    }

    default Instant toInstant(Object any) throws UnsupportedOperationException {
        return convert(any, Instant.class);
    }

    default Duration toDuration(Object any) throws UnsupportedOperationException {
        return convert(any, Duration.class);
    }

    default boolean toBoolean(Object any) throws UnsupportedOperationException {
        return convert(any, boolean.class);
    }

    default byte toByte(Object any) throws UnsupportedOperationException {
        return convert(any, byte.class);
    }

    default short toShort(Object any) throws UnsupportedOperationException {
        return convert(any, short.class);
    }

    default char toChar(Object any) throws UnsupportedOperationException {
        return convert(any, char.class);
    }

    default int toInt(Object any) throws UnsupportedOperationException {
        return convert(any, int.class);
    }

    default long toLong(Object any) throws UnsupportedOperationException {
        return convert(any, long.class);
    }

    default float toFloat(Object any) throws UnsupportedOperationException {
        return convert(any, float.class);
    }

    default double toDouble(Object any) throws UnsupportedOperationException {
        return convert(any, double.class);
    }

    default BigInteger toBigInteger(Object any) throws UnsupportedOperationException {
        return convert(any, BigInteger.class);
    }

    default BigDecimal toBigDecimal(Object any) throws UnsupportedOperationException {
        return convert(any, BigDecimal.class);
    }
}
