package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.convert.FsConvertHandler;
import xyz.srclab.common.convert.FsConverter;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Convert handler implementation supports converting any object to string with {@link String#valueOf(Object)}.
 * It supports target type in:
 * <ul>
 *     <li>{@link String};</li>
 *     <li>{@link CharSequence};</li>
 *     <li>Char array (primitive or wrapper);</li>
 *     <li>{@link StringBuilder};</li>
 *     <li>{@link StringBuffer};</li>
 * </ul>
 * Note if the {@code obj} is null, return {@link #NOT_SUPPORTED}.
 *
 * @author fredsuvn
 */
public class StringConvertHandler implements FsConvertHandler {

    @Override
    public @Nullable Object convert(@Nullable Object obj, Type fromType, Type targetType, FsConverter converter) {
        if (obj == null) {
            return NOT_SUPPORTED;
        }
        if (Objects.equals(targetType, String.class)) {
            return String.valueOf(obj);
        } else if (Objects.equals(targetType, CharSequence.class)) {
            return String.valueOf(obj);
        } else if (Objects.equals(targetType, char[].class)) {
            return String.valueOf(obj).toCharArray();
        } else if (Objects.equals(targetType, Character[].class)) {
            String str = String.valueOf(obj);
            Character[] result = new Character[str.length()];
            for (int i = 0; i < result.length; i++) {
                result[i] = str.charAt(i);
            }
            return result;
        } else if (Objects.equals(targetType, StringBuilder.class)) {
            return new StringBuilder(String.valueOf(obj));
        } else if (Objects.equals(targetType, StringBuffer.class)) {
            return new StringBuffer(String.valueOf(obj));
        } else {
            return NOT_SUPPORTED;
        }
    }
}
