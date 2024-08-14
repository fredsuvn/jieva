package xyz.fslabo.common.mapper.handlers;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.bean.PropertyInfo;
import xyz.fslabo.common.mapper.Mapper;
import xyz.fslabo.common.mapper.MappingOptions;

import java.lang.reflect.Type;
import java.util.Map;

public class TypedMapperHandler implements Mapper.Handler {

    private final Map<Type, Converter<?>> converters;

    public TypedMapperHandler(Map<Type, Converter<?>> converters) {
        this.converters = converters;
    }

    @Override
    public Object map(@Nullable Object source, Type sourceType, Type targetType, Mapper mapper, MappingOptions options) {
        return mapProperty(source, sourceType, targetType, null, mapper, options);
    }

    @Override
    public Object mapProperty(@Nullable Object source, Type sourceType, Type targetType, @Nullable PropertyInfo targetProperty, Mapper mapper, MappingOptions options) {
        if (source == null) {
            return Flag.CONTINUE;
        }
        Converter<?> converter = converters.get(targetType);
        if (converter == null) {
            return Flag.CONTINUE;
        }
        Object targetObject = converter.convert(source, sourceType, targetProperty, options);
        if (targetObject == null) {
            return Flag.CONTINUE;
        }
        return wrapResult(targetObject);
    }

    /**
     * Converter to convert source object from source type to target type.
     */
    @FunctionalInterface
    public interface Converter<T> {

        /**
         * Converts and returns a new object from source type to target type, or returns {@code null} if unsupported.
         *
         * @param source     source object
         * @param sourceType source type
         * @param options    mapping options
         * @return a new object from source type to target type or null if unsupported
         */
        @Nullable
        T convert(Object source, Type sourceType, @Nullable PropertyInfo targetProperty, MappingOptions options);
    }
}
