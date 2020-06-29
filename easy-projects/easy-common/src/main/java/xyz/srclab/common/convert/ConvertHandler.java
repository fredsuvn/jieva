package xyz.srclab.common.convert;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;

import java.lang.reflect.Type;
import java.util.List;

@Immutable
public interface ConvertHandler {

    @Immutable
    static List<ConvertHandler> defaultHandlers() {
        return ConvertHandlerSupport.defaultHandlers();
    }

    @Nullable
    Object convert(Object from, Class<?> to, Converter converter);

    @Nullable
    Object convert(Object from, Type to, Converter converter);
}
