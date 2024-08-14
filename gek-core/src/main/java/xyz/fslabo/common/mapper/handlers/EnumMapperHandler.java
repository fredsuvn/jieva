package xyz.fslabo.common.mapper.handlers;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.bean.PropertyInfo;
import xyz.fslabo.common.mapper.Mapper;
import xyz.fslabo.common.mapper.MappingOptions;

import java.lang.reflect.Type;

/**
 * Enum mapper handler implementation which is used to support mapping from any object to enum types, has a singleton
 * instance {@link #SINGLETON}.
 * <p>
 * If source object is null or target type is not an enum type, return {@link Flag#CONTINUE}.
 *
 * @author fredsuvn
 */
public class EnumMapperHandler implements Mapper.Handler {

    /**
     * Singleton instance.
     */
    public static final EnumMapperHandler SINGLETON = new EnumMapperHandler();

    protected EnumMapperHandler() {
    }

    @Override
    public Object map(@Nullable Object source, Type sourceType, Type targetType, Mapper mapper, MappingOptions options) {
        if (source == null) {
            return Flag.CONTINUE;
        }
        if (!(targetType instanceof Class<?>)) {
            return Flag.CONTINUE;
        }
        Class<?> enumType = (Class<?>) targetType;
        if (!enumType.isEnum()) {
            return Flag.CONTINUE;
        }
        String name = source.toString();
        return Jie.findEnum(enumType, name);
    }

    @Override
    public Object mapProperty(@Nullable Object source, Type sourceType, Type targetType, PropertyInfo targetProperty, Mapper mapper, MappingOptions options) {
        return map(source, sourceType, targetType, mapper, options);
    }
}
