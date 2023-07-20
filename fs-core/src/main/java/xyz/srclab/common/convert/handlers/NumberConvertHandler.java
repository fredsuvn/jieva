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
    public @Nullable Object convert(@Nullable Object obj, Type fromType, Type targetType, FsConverter converter) {
        if (obj == null) {
            return CONTINUE;
        }
        if (Objects.equals(targetType, byte.class) || Objects.equals(targetType, Byte.class)) {
            if (FsType.isAssignableFrom(Number.class, fromType)) {
                return ((Number) obj).byteValue();
            } else {
                return Byte.parseByte(obj.toString());
            }
        } else if (Objects.equals(targetType, short.class) || Objects.equals(targetType, Short.class)) {
            if (FsType.isAssignableFrom(Number.class, fromType)) {
                return ((Number) obj).shortValue();
            } else {
                return Short.parseShort(obj.toString());
            }
        } else if (Objects.equals(targetType, int.class) || Objects.equals(targetType, Integer.class)) {
            if (FsType.isAssignableFrom(Number.class, fromType)) {
                return ((Number) obj).intValue();
            } else {
                return Integer.parseInt(obj.toString());
            }
        } else if (Objects.equals(targetType, long.class) || Objects.equals(targetType, Long.class)) {
            if (FsType.isAssignableFrom(Number.class, fromType)) {
                return ((Number) obj).longValue();
            } else {
                return Long.parseLong(obj.toString());
            }
        } else if (Objects.equals(targetType, float.class) || Objects.equals(targetType, Float.class)) {
            if (FsType.isAssignableFrom(Number.class, fromType)) {
                return ((Number) obj).floatValue();
            } else {
                return Float.parseFloat(obj.toString());
            }
        } else if (Objects.equals(targetType, double.class) || Objects.equals(targetType, Double.class)) {
            if (FsType.isAssignableFrom(Number.class, fromType)) {
                return ((Number) obj).doubleValue();
            } else {
                return Double.parseDouble(obj.toString());
            }
        } else if (Objects.equals(targetType, char.class) || Objects.equals(targetType, Character.class)) {
            if (FsType.isAssignableFrom(Number.class, fromType)) {
                return (char) (((Number) obj).intValue());
            } else {
                return (char) (Integer.parseInt(obj.toString()));
            }
        } else if (Objects.equals(targetType, BigDecimal.class)) {
            return new BigDecimal(obj.toString());
        } else if (Objects.equals(targetType, BigInteger.class)) {
            return new BigInteger(obj.toString());
        } else {
            return CONTINUE;
        }
    }
}
