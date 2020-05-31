package xyz.srclab.common.convert;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.pattern.builder.HandlersBuilder;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.Arrays;

@Immutable
public interface Converter {

    static Builder newBuilder() {
        return new Builder();
    }

    static Builder from(Converter converter) {
        return new Builder(converter);
    }

    <T> T convert(Object from, Type to) throws UnsupportedOperationException;

    default <T> T convert(Object from, Class<T> to) throws UnsupportedOperationException {
        return convert(from, (Type) to);
    }

    default <T> T convert(Object from, TypeRef<T> to) throws UnsupportedOperationException {
        return convert(from, to.getType());
    }

    final class Builder extends HandlersBuilder<Converter, ConvertHandler, Builder> {

        private final @Nullable Converter baseConverter;

        public Builder() {
            this.baseConverter = null;
        }

        public Builder(Converter baseConverter) {
            this.baseConverter = baseConverter;
        }

        protected Converter buildNew() {
            return new ConverterImpl(this);
        }

        private static final class ConverterImpl implements Converter {

            private static final ConvertHandler[] TO_ARRAY = new ConvertHandler[0];

            private final ConvertHandler[] handlers;

            private @Nullable ConverterAdaptor converterAdaptor;

            private ConverterImpl(Builder builder) {
                ConvertHandler[] handlers = builder.handlers.toArray(TO_ARRAY);
                if (builder.baseConverter == null) {
                    this.handlers = handlers;
                    return;
                }
                this.handlers = Arrays.copyOf(handlers, handlers.length + 1, ConvertHandler[].class);
                Converter baseConverter = builder.baseConverter;
                ConvertHandler baseHandler = baseConverter instanceof ConverterImpl ?
                        ((ConverterImpl) baseConverter).getConverterAdaptor()
                        :
                        new ConverterAdaptor(builder.baseConverter);
                this.handlers[this.handlers.length - 1] = baseHandler;
            }

            @Override
            public <T> T convert(Object from, Type to) {
                for (ConvertHandler handler : handlers) {
                    @Nullable Object result = handler.convert(from, to, this);
                    if (result != null) {
                        return Cast.as(result);
                    }
                }
                throw new UnsupportedOperationException("Cannot convert to " + to + ": " + from);
            }

            private ConverterAdaptor getConverterAdaptor() {
                if (converterAdaptor == null) {
                    converterAdaptor = new ConverterAdaptor(this);
                }
                return converterAdaptor;
            }
        }

        private static final class ConverterAdaptor implements ConvertHandler {

            private final Converter converter;

            private ConverterAdaptor(Converter converter) {
                this.converter = converter;
            }

            @Override
            public @Nullable Object convert(Object from, Type to, Converter converter) {
                try {
                    return this.converter.convert(from, to);
                } catch (UnsupportedOperationException e) {
                    return null;
                }
            }
        }
    }
}
