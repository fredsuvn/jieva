package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.convert.FsConvertHandler;
import xyz.srclab.common.convert.FsConverter;

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
 * Note if the {@code obj} is null, return {@link #CONTINUE}.
 *
 * @author fredsuvn
 */
public class StringConvertHandler implements FsConvertHandler {

    @Override
    public @Nullable Object convert(@Nullable Object obj, Type fromType, Type targetType, FsConverter converter) {
        if (obj == null) {
            return CONTINUE;
        }
        if (Objects.equals(targetType, String.class) || Objects.equals(targetType, CharSequence.class)) {
            return obj.toString();
        } else if (Objects.equals(targetType, char[].class)) {
            return obj.toString().toCharArray();
        } else if (Objects.equals(targetType, Character[].class)) {
            String str = obj.toString();
            Character[] result = new Character[str.length()];
            for (int i = 0; i < result.length; i++) {
                result[i] = str.charAt(i);
            }
            return result;
        } else if (Objects.equals(targetType, StringBuilder.class)) {
            return new StringBuilder(obj.toString());
        } else if (Objects.equals(targetType, StringBuffer.class)) {
            return new StringBuffer(obj.toString());
        } else {
            return CONTINUE;
        }
    }
}
