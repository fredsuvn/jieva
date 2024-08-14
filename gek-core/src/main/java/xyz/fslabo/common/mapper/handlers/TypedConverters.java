package xyz.fslabo.common.mapper.handlers;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.bean.PropertyInfo;
import xyz.fslabo.common.mapper.MappingOptions;

import java.lang.reflect.Type;

public class TypedConverters {

    public static class StringConverter implements TypedMapperHandler.Converter<String> {

        @Override
        public @Nullable String convert(
            Object source, Type sourceType, @Nullable PropertyInfo targetProperty, MappingOptions options) {

            return "";
        }
    }
}
