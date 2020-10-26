package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.convert.Converter;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
public class StringConvertHandler implements ConvertHandler {

    @Override
    public @Nullable Object convert(Object from, Class<?> toType, Converter converter) {
        if (toType.equals(String.class)
                || toType.isAssignableFrom(String.class)
                || toType.isAssignableFrom(CharSequence.class)) {
            return from.toString();
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
