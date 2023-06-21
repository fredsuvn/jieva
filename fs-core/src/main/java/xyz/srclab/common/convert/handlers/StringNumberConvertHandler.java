package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.convert.FsConvertHandler;
import xyz.srclab.common.convert.FsConverter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Convert handler implementation supports converting between string, number and boolean types:
 * <ul>
 *     <li>
 *         Supports any type to {@link String} with {@link String#valueOf(Object)};
 *     </li>
 *     <li>
 *         Subtype of {@link Number}: {@link Byte}/byte, {@link Short}/short, {@link Integer}/int, {@link Long}/long,
 *         {@link Float}/float, {@link Double}/double, {@link java.math.BigInteger}, {@link java.math.BigDecimal}.
 *     </li>
 *     <li>
 *         {@link Character}/char;
 *     </li>
 *     <li>
 *         {@link Boolean}/boolean (0 false otherwise true).
 *     </li>
 * </ul>
 *
 * @author fredsuvn
 */
public class StringNumberConvertHandler implements FsConvertHandler {

    @Override
    public @Nullable Object convert(@Nullable Object obj, Type fromType, Type targetType, FsConverter converter) {
        if (obj == null || !(fromType instanceof Class) || !(targetType instanceof Class)) {
            return FsConvertHandler.NOT_SUPPORTED;
        }
        Class<?> from = (Class<?>) fromType;
        Class<?> to = (Class<?>) targetType;
        if (Objects.equals(to, String.class)) {
            return String.valueOf(obj);
        } else if (Objects.equals(to, Byte.class) || Objects.equals(to, byte.class)) {
            if (Number.class.isAssignableFrom(from)) {
                return ((Number) obj).byteValue();
            }
            if (Objects.equals(from, String.class)) {
                return Byte.parseByte((String) obj);
            }
        } else if (Objects.equals(to, Short.class) || Objects.equals(to, short.class)) {
            if (Number.class.isAssignableFrom(from)) {
                return ((Number) obj).shortValue();
            }
            if (Objects.equals(from, String.class)) {
                return Short.parseShort((String) obj);
            }
        } else if (Objects.equals(to, Integer.class) || Objects.equals(to, int.class)) {
            if (Number.class.isAssignableFrom(from)) {
                return ((Number) obj).intValue();
            }
            if (Objects.equals(from, String.class)) {
                return Integer.parseInt((String) obj);
            }
        } else if (Objects.equals(to, Long.class) || Objects.equals(to, long.class)) {
            if (Number.class.isAssignableFrom(from)) {
                return ((Number) obj).longValue();
            }
            if (Objects.equals(from, String.class)) {
                return Long.parseLong((String) obj);
            }
        } else if (Objects.equals(to, Float.class) || Objects.equals(to, float.class)) {
            if (Number.class.isAssignableFrom(from)) {
                return ((Number) obj).floatValue();
            }
            if (Objects.equals(from, String.class)) {
                return Float.parseFloat((String) obj);
            }
        } else if (Objects.equals(to, Double.class) || Objects.equals(to, double.class)) {
            if (Number.class.isAssignableFrom(from)) {
                return ((Number) obj).doubleValue();
            }
            if (Objects.equals(from, String.class)) {
                return Double.parseDouble((String) obj);
            }
        } else if (Objects.equals(to, BigInteger.class)) {
            if (Number.class.isAssignableFrom(from) || Objects.equals(from, String.class)) {
                return new BigInteger(String.valueOf(obj));
            }
        } else if (Objects.equals(to, BigDecimal.class)) {
            if (Number.class.isAssignableFrom(from) || Objects.equals(from, String.class)) {
                return new BigDecimal(String.valueOf(obj));
            }
        } else if (Objects.equals(to, Character.class) || Objects.equals(to, char.class)) {
            if (Number.class.isAssignableFrom(from)) {
                return (char) (((Number) obj).intValue());
            }
            if (Objects.equals(from, String.class)) {
                String f = (String) obj;
                if (f.length() == 1) {
                    return f.charAt(0);
                }
            }
        } else if (Objects.equals(from, Boolean.class) || Objects.equals(to, boolean.class)) {
            if (Number.class.isAssignableFrom(from)) {
                if (((Number) obj).doubleValue() == 0) {
                    return false;
                } else {
                    return true;
                }
            }
            if (Objects.equals(from, String.class)) {
                return Boolean.parseBoolean((String) obj);
            }
        }
        return FsConvertHandler.NOT_SUPPORTED;
    }
}
