package xyz.srclab.common.convert;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;

import java.lang.reflect.Type;

@Immutable
public interface ConvertHandler {

    static ConvertHandler defaultHandler() {
        return new ConvertHandler() {
            @Override
            public @Nullable Object convert(Object from, Type to, Converter converter) {
                return null;
            }
        };
    }

    @Nullable
    Object convert(Object from, Type to, Converter converter);
}
