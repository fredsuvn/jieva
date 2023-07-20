package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.convert.FsConverter;

import java.lang.reflect.Type;
import java.util.Objects;

import static xyz.srclab.common.convert.FsConverter.CONTINUE;

/**
 * Convert handler implementation which is used to support conversion from any object to boolean types with
 * {@link Boolean#parseBoolean(String)}.
 * <p>
 * Supports target types:
 * <ul>
 *     <li>boolean and {@link Boolean};</li>
 * </ul>
 * Note if the {@code obj} is null, return {@link FsConverter#CONTINUE}.
 *
 * @author fredsuvn
 */
public class BooleanConvertHandler implements FsConverter.Handler {

    @Override
    public @Nullable Object convert(@Nullable Object obj, Type fromType, Type targetType, FsConverter converter) {
        if (obj == null) {
            return CONTINUE;
        }
        if (Objects.equals(targetType, boolean.class) || Objects.equals(targetType, Boolean.class)) {
            return Boolean.parseBoolean(obj.toString());
        } else {
            return CONTINUE;
        }
    }
}
