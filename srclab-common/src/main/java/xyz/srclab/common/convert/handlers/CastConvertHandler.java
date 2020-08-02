package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.convert.ConvertHandler;
import xyz.srclab.common.convert.Converter;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
public class CastConvertHandler implements ConvertHandler {

    @Override
    public @Nullable Object convert(Object from, Class<?> to, Converter converter) {
        if (to.equals(Object.class) || to.equals(from.getClass())) {
            return from;
        }
        if (to.isAssignableFrom(from.getClass())) {
            return from;
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
