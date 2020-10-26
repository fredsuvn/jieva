package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.convert.Converter;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
public class CastConvertHandler implements ConvertHandler {

    @Override
    public @Nullable Object convert(Object from, Class<?> toType, Converter converter) {
        if (toType.equals(Object.class) || toType.equals(from.getClass())) {
            return from;
        }
        if (toType.isAssignableFrom(from.getClass())) {
            return from;
        }
        return null;
    }

    @Override
    public @Nullable Object convert(Object from, Type toType, Converter converter) {
        if (toType instanceof Class) {
            return convert(from, (Class<?>) toType, converter);
        }
        return null;
    }
}
