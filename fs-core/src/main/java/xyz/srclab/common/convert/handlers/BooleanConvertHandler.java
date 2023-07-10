package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.convert.FsConvertHandler;
import xyz.srclab.common.convert.FsConverter;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Convert handler implementation supports converting any object to boolean with {@link Boolean#parseBoolean(String)}.
 * It supports target type in:
 * <ul>
 *     <li>boolean and {@link Boolean};</li>
 * </ul>
 * Note if the {@code obj} is null, return {@link #NOT_SUPPORTED}.
 *
 * @author fredsuvn
 */
public class BooleanConvertHandler implements FsConvertHandler {

    @Override
    public @Nullable Object convert(@Nullable Object obj, Type fromType, Type targetType, FsConverter converter) {
        if (obj == null) {
            return NOT_SUPPORTED;
        }
        if (Objects.equals(targetType, boolean.class) || Objects.equals(targetType, Boolean.class)) {
            return Boolean.parseBoolean(obj.toString());
        } else {
            return NOT_SUPPORTED;
        }
    }
}
