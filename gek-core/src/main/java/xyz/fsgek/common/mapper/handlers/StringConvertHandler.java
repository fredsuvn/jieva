package xyz.fsgek.common.mapper.handlers;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.mapper.JieMapper;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Convert handler implementation which is used to support the conversion from any object to string with
 * {@link Object#toString()}.
 * <p>
 * Supported target types:
 * <ul>
 *     <li>{@link String};</li>
 *     <li>{@link CharSequence};</li>
 *     <li>Char array (primitive or wrapper);</li>
 *     <li>{@link StringBuilder};</li>
 *     <li>{@link StringBuffer};</li>
 * </ul>
 * Note if the {@code obj} is null, return null.
 *
 * @author fredsuvn
 */
public class StringConvertHandler implements JieMapper.Handler {

    /**
     * An instance.
     */
    public static final StringConvertHandler INSTANCE = new StringConvertHandler();

    @Override
    public @Nullable Object map(@Nullable Object source, Type sourceType, Type targetType, JieMapper mapper) {
        if (source == null) {
            return null;
        }
        if (Objects.equals(targetType, String.class) || Objects.equals(targetType, CharSequence.class)) {
            return source.toString();
        } else if (Objects.equals(targetType, char[].class)) {
            return source.toString().toCharArray();
        } else if (Objects.equals(targetType, StringBuilder.class)) {
            return new StringBuilder(source.toString());
        } else if (Objects.equals(targetType, StringBuffer.class)) {
            return new StringBuffer(source.toString());
        } else {
            return null;
        }
    }
}
