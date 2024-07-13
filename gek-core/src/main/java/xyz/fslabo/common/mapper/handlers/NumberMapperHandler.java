package xyz.fslabo.common.mapper.handlers;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.mapper.Mapper;
import xyz.fslabo.common.mapper.MapperOptions;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Mapper handler which is used to map from any object to number types.
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
 * Note if source is null, return {@code 0}.
 *
 * @author fredsuvn
 */
public class NumberMapperHandler implements Mapper.Handler {

    /**
     * An instance.
     */
    public static final NumberMapperHandler INSTANCE = new NumberMapperHandler();

    @Override
    public Object map(@Nullable Object source, Type sourceType, Type targetType, Mapper mapper, MapperOptions options) {
        if (source == null) {
            return mapZero(targetType);
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
                return source;
            }
            return new BigDecimal(source.toString());
        } else if (Objects.equals(targetType, BigInteger.class)) {
            if (source instanceof BigInteger) {
                return source;
            }
            return new BigInteger(source.toString());
        } else {
            return Flag.CONTINUE;
        }
    }

    private Object mapZero(Type targetType) {
        if (Objects.equals(targetType, byte.class) || Objects.equals(targetType, Byte.class)) {
            return (byte) 0;
        } else if (Objects.equals(targetType, short.class) || Objects.equals(targetType, Short.class)) {
            return (short) 0;
        } else if (Objects.equals(targetType, int.class) || Objects.equals(targetType, Integer.class)) {
            return 0;
        } else if (Objects.equals(targetType, long.class) || Objects.equals(targetType, Long.class)) {
            return 0L;
        } else if (Objects.equals(targetType, float.class) || Objects.equals(targetType, Float.class)) {
            return 0.0f;
        } else if (Objects.equals(targetType, double.class) || Objects.equals(targetType, Double.class)) {
            return 0.0;
        } else if (Objects.equals(targetType, char.class) || Objects.equals(targetType, Character.class)) {
            return (char) 0;
        } else if (Objects.equals(targetType, BigDecimal.class)) {
            return BigDecimal.ZERO;
        } else if (Objects.equals(targetType, BigInteger.class)) {
            return BigInteger.ZERO;
        } else {
            return Flag.CONTINUE;
        }
    }
}
