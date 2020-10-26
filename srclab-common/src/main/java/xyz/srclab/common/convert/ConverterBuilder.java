package xyz.srclab.common.convert;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.ArrayKit;
import xyz.srclab.common.base.As;
import xyz.srclab.common.base.Check;
import xyz.srclab.common.design.builder.HandlersProductBuilder;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author sunqian
 */
public class ConverterBuilder extends HandlersProductBuilder<Converter, ConvertHandler, ConverterBuilder> {

    static ConverterBuilder newBuilder() {
        return new ConverterBuilder();
    }

    @Override
    protected Converter buildNew() {
        Check.checkState(!handlersResult().isEmpty(), "There is no handler");
        return new ConverterImpl(handlersResult());
    }

    public Converter build() {
        return buildCaching();
    }

    private static final class ConverterImpl implements Converter {

        private final ConvertHandler[] handlers;

        private ConverterImpl(List<ConvertHandler> handlers) {
            this.handlers = ArrayKit.toArray(handlers, ConvertHandler.class);
        }

        @Override
        public <T> T convert(Object from, Class<T> toType) throws UnsupportedOperationException {
            for (ConvertHandler handler : handlers) {
                @Nullable T value = As.nullable(handler.convert(from, toType, this));
                if (value != null) {
                    return value;
                }
            }
            throw new UnsupportedOperationException(toType.getTypeName());
        }

        @Override
        public <T> T convert(Object from, Type toType) throws UnsupportedOperationException {
            for (ConvertHandler handler : handlers) {
                @Nullable T value = As.nullable(handler.convert(from, toType, this));
                if (value != null) {
                    return value;
                }
            }
            throw new UnsupportedOperationException(toType.getTypeName());
        }

        @Override
        public <T> T convert(Object from, TypeRef<T> toTypeRef) throws UnsupportedOperationException {
            for (ConvertHandler handler : handlers) {
                @Nullable T value = As.nullable(handler.convert(from, toTypeRef.getType(), this));
                if (value != null) {
                    return value;
                }
            }
            throw new UnsupportedOperationException(toTypeRef.getType().getTypeName());
        }
    }
}
