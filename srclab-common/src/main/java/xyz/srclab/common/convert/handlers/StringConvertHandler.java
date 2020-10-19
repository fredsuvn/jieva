package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.convert.Converter;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
public class StringConvertHandler implements ConvertHandler {

    @Override
    public @Nullable Object convert(Object from, Class<?> to, Converter converter) {
        if (to.equals(String.class)
                || to.isAssignableFrom(String.class)
                || to.isAssignableFrom(CharSequence.class)) {
            return from.toString();
        }
        return null;
    }

    @Override
    public @Nullable Object convert(Object from, Type to, Converter converter) {
        if (to instanceof Class) {
            return convert(from, (Class<?>) to, converter);
        }
        return null;
    }
}
