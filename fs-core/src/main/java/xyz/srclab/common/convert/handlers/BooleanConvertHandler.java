package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.convert.FsConverter;

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
 * Note if source object is null, return {@link Fs#CONTINUE}.
 *
 * @author fredsuvn
 */
public class BooleanConvertHandler implements FsConverter.Handler {

    /**
     * An instance.
     */
    public static final BooleanConvertHandler INSTANCE = new BooleanConvertHandler();

    @Override
    public @Nullable Object convert(
        @Nullable Object source, Type sourceType, Type targetType, FsConverter.Options options, FsConverter converter) {
        if (source == null) {
            return Fs.CONTINUE;
        }
        if (!Objects.equals(targetType, boolean.class) && !Objects.equals(targetType, Boolean.class)) {
            return Fs.CONTINUE;
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
