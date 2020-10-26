package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.reflect.TypeKit;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
public class TypeBoundsConvertHandler implements ConvertHandler {

    @Override
    public @Nullable Object convert(Object from, Class<?> toType, Converter converter) {
        return null;
    }

    @Override
    public @Nullable Object convert(Object from, Type toType, Converter converter) {
        Type upperBound = TypeKit.getUpperBound(toType);
        return converter.convert(from, upperBound);
    }
}
