package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.convert.FsConverter;

import java.lang.reflect.Type;

/**
 * Convert handler implementation which is used to support conversion from any object to enum types.
 * Note if source object is null, return {@link Fs#CONTINUE}.
 *
 * @author fredsuvn
 */
public class EnumConvertHandler implements FsConverter.Handler {

    /**
     * An instance.
     */
    public static final EnumConvertHandler INSTANCE = new EnumConvertHandler();

    @Override
    public @Nullable Object convert(@Nullable Object source, Type sourceType, Type targetType, FsConverter converter) {
        if (source == null) {
            return Fs.CONTINUE;
        }
        if (!(targetType instanceof Class<?>)) {
            return Fs.CONTINUE;
        }
        Class<?> enumType = (Class<?>) targetType;
        if (!enumType.isEnum()) {
            return Fs.CONTINUE;
        }
        String name = source.toString();
        Object enumObj = Fs.findEnum(enumType, name);
        return enumObj == null ? Fs.CONTINUE : enumObj;
    }
}
