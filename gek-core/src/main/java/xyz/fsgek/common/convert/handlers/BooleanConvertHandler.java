package xyz.fsgek.common.convert.handlers;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.convert.GekConverter;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Convert handler implementation which is used to support conversion from any object to boolean types:
 * <ul>
 *     <li>
 *         If source is instance of {@link Boolean}, return itself;
 *     </li>
 *     <li>
 *         If source object is instance of {@link Number}, return true if {@link Number#intValue()} is 1, else false;
 *     </li>
 *     <li>
 *         If source object is not instance of {@link Number}, return {@link Boolean#parseBoolean(String)}.
 *     </li>
 * </ul>
 * Note if source object is null, return {@code null}.
 *
 * @author fredsuvn
 */
public class BooleanConvertHandler implements GekConverter.Handler {

    /**
     * An instance.
     */
    public static final BooleanConvertHandler INSTANCE = new BooleanConvertHandler();

    @Override
    public @Nullable Object convert(@Nullable Object source, Type sourceType, Type targetType, GekConverter converter) {
        if (source == null) {
            return null;
        }
        if (!Objects.equals(targetType, boolean.class) && !Objects.equals(targetType, Boolean.class)) {
            return null;
        }
        if (source instanceof Boolean) {
            return source;
        }
        if (source instanceof Number) {
            return ((Number) source).intValue() == 1;
        }
        return Boolean.parseBoolean(source.toString());
    }
}
