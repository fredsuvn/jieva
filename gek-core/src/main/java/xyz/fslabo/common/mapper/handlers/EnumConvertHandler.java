package xyz.fslabo.common.mapper.handlers;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.mapper.JieMapper;

import java.lang.reflect.Type;

/**
 * Convert handler implementation which is used to support conversion from any object to enum types.
 * Note if source object is null, return {@code null}.
 *
 * @author fredsuvn
 */
public class EnumConvertHandler implements JieMapper.Handler {

    /**
     * An instance.
     */
    public static final EnumConvertHandler INSTANCE = new EnumConvertHandler();

    @Override
    public @Nullable Object map(@Nullable Object source, Type sourceType, Type targetType, JieMapper mapper) {
        if (source == null) {
            return null;
        }
        if (!(targetType instanceof Class<?>)) {
            return null;
        }
        Class<?> enumType = (Class<?>) targetType;
        if (!enumType.isEnum()) {
            return null;
        }
        String name = source.toString();
        return Jie.findEnum(enumType, name);
    }
}
