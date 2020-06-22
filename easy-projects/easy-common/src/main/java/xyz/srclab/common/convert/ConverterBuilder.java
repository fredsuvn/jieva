package xyz.srclab.common.convert;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.design.builder.HandlersBuilder;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author sunqian
 */
public class ConverterBuilder extends HandlersBuilder<Converter, ConvertHandler, ConverterBuilder> {

    static ConverterBuilder newBuilder() {
        return new ConverterBuilder();
    }

    private ConverterBuilder() {
    }

    @Override
    protected Converter buildNew() {
        Checker.checkState(!handlers.isEmpty(), "There is no handler");
        return new ConverterImpl(handlers);
    }

    private static final class ConverterImpl implements Converter {

        private final ConvertHandler[] handlers;

        private ConverterImpl(List<ConvertHandler> handlers) {
            this.handlers = ArrayKit.toArray(handlers, ConvertHandler.class);
        }

        @Override
        public <T> T convert(Object from, Class<T> to) throws UnsupportedOperationException {
            for (ConvertHandler handler : handlers) {
                @Nullable T value = Cast.nullable(handler.convert(from, to, this));
                if (value != null) {
                    return value;
                }
            }
            throw new UnsupportedOperationException(to.getTypeName());
        }

        @Override
        public <T> T convert(Object from, Type to) throws UnsupportedOperationException {
            for (ConvertHandler handler : handlers) {
                @Nullable T value = Cast.nullable(handler.convert(from, to, this));
                if (value != null) {
                    return value;
                }
            }
            throw new UnsupportedOperationException(to.getTypeName());
        }

        @Override
        public <T> T convert(Object from, TypeRef<T> to) throws UnsupportedOperationException {
            for (ConvertHandler handler : handlers) {
                @Nullable T value = Cast.nullable(handler.convert(from, to.getType(), this));
                if (value != null) {
                    return value;
                }
            }
            throw new UnsupportedOperationException(to.getType().getTypeName());
        }
    }
}
