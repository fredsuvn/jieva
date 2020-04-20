package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.pattern.builder.HandlersBuilder;
import xyz.srclab.common.reflect.type.TypeRef;
import xyz.srclab.common.string.format.fastformat.FastFormat;

import java.lang.reflect.Type;

@Immutable
public interface BeanConverter {

    BeanConverter DEFAULT = new DefaultBeanConverter();

    static Builder newBuilder() {
        return new Builder();
    }

    Object convert(Object from, Type to);

    Object convert(Object from, Type to, BeanOperator beanOperator);

    default <T> T convert(Object from, Class<T> to) {
        return (T) convert(from, (Type) to);
    }

    default <T> T convert(Object from, Class<T> to, BeanOperator beanOperator) {
        return (T) convert(from, (Type) to, beanOperator);
    }

    default <T> T convert(Object from, TypeRef<T> to) {
        return (T) convert(from, to.getType());
    }

    default <T> T convert(Object from, TypeRef<T> to, BeanOperator beanOperator) {
        return (T) convert(from, to.getType(), beanOperator);
    }

    class Builder extends HandlersBuilder<BeanConverter, BeanConverterHandler, Builder> {

        private @Nullable BeanOperator beanOperator;

        public Builder setBeanOperator(BeanOperator beanOperator) {
            this.commitChanges();
            this.beanOperator = beanOperator;
            return this;
        }

        @Override
        public BeanConverter build() {
            return super.build();
        }

        protected BeanConverter buildNew() {
            return new BeanConverterImpl(this);
        }

        private static final class BeanConverterImpl implements BeanConverter {

            private final BeanOperator beanOperator;
            private final BeanConverterHandler[] handlers;

            private BeanConverterImpl(Builder builder) {
                this.beanOperator = builder.beanOperator == null ? BeanOperator.DEFAULT : builder.beanOperator;
                this.handlers = builder.handlers.toArray(new BeanConverterHandler[0]);
            }

            @Override
            public Object convert(Object from, Type to) {
                return convert(from, to, beanOperator);
            }

            @Override
            public Object convert(Object from, Type to, BeanOperator beanOperator) {
                for (BeanConverterHandler handler : handlers) {
                    if (handler.supportConvert(from, to, beanOperator)) {
                        return handler.convert(from, to, beanOperator);
                    }
                }
                throw new UnsupportedOperationException(
                        FastFormat.format("Cannot convert object from {} to {}", from, to));
            }
        }
    }
}
