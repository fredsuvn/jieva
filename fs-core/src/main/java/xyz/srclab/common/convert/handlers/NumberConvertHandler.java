package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.reflect.FsType;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import static xyz.srclab.common.convert.FsConverter.CONTINUE;

/**
 * Convert handler implementation which is used to support the conversion from any object to number types.
 * <p>
 * Supported target types:
 * <ul>
 *     <li>Primitive number: byte, short, int, long, float, double;</li>
 *     <li>Primitive wrapper: {@link Byte}, {@link Short},
 *     {@link Integer}, {@link Long}, {@link Float}, {@link Double};</li>
 *     <li>char and {@link Character};</li>
 *     <li>{@link BigDecimal};</li>
 *     <li>{@link BigInteger};</li>
 * </ul>
 * Note if the {@code obj} is null, return {@link FsConverter#CONTINUE}.
 *
 * @author fredsuvn
 */
public class NumberConvertHandler implements FsConverter.Handler {

    @Override
    public @Nullable Object convert(
        @Nullable Object source, Type sourceType, Type targetType, FsConverter.Options options, FsConverter converter) {
        if (source == null) {
            return CONTINUE;
        }
        if (Objects.equals(targetType, byte.class) || Objects.equals(targetType, Byte.class)) {
            if (source instanceof Number) {
                return ((Number) source).byteValue();
            } else {
                return Byte.parseByte(source.toString());
            }
        } else if (Objects.equals(targetType, short.class) || Objects.equals(targetType, Short.class)) {
            if (source instanceof Number) {
                return ((Number) source).shortValue();
            } else {
                return Short.parseShort(source.toString());
            }
        } else if (Objects.equals(targetType, int.class) || Objects.equals(targetType, Integer.class)) {
            if (source instanceof Number) {
                return ((Number) source).intValue();
            } else {
                return Integer.parseInt(source.toString());
            }
        } else if (Objects.equals(targetType, long.class) || Objects.equals(targetType, Long.class)) {
            if (source instanceof Number) {
                return ((Number) source).longValue();
            } else {
                return Long.parseLong(source.toString());
            }
        } else if (Objects.equals(targetType, float.class) || Objects.equals(targetType, Float.class)) {
            if (source instanceof Number) {
                return ((Number) source).floatValue();
            } else {
                return Float.parseFloat(source.toString());
            }
        } else if (Objects.equals(targetType, double.class) || Objects.equals(targetType, Double.class)) {
            if (source instanceof Number) {
                return ((Number) source).doubleValue();
            } else {
                return Double.parseDouble(source.toString());
            }
        } else if (Objects.equals(targetType, char.class) || Objects.equals(targetType, Character.class)) {
            if (source instanceof Number) {
                return (char) (((Number) source).intValue());
            } else {
                return (char) (Integer.parseInt(source.toString()));
            }
        } else if (Objects.equals(targetType, BigDecimal.class)) {
            if (source instanceof BigDecimal) {
                return options.objectGenerationPolicy() == Op;
            }
            return new BigDecimal(source.toString());
        } else if (Objects.equals(targetType, BigInteger.class)) {
            return new BigInteger(source.toString());
        } else {
            return CONTINUE;
        }
    }
}
