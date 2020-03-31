package xyz.srclab.common.bean;

import xyz.srclab.annotation.concurrent.ReturnThreadSafeDependOn;
import xyz.srclab.annotation.concurrent.ThreadSafeDependOn;
import xyz.srclab.common.builder.ProcessByHandlersBuilder;
import xyz.srclab.common.format.FormatHelper;
import xyz.srclab.common.lang.TypeRef;

import java.lang.reflect.Type;

public interface BeanConverter {

    static Builder newBuilder() {
        return new Builder();
    }

    <T> T convert(Object from, Type to);

    <T> T convert(Object from, Type to, BeanOperator beanOperator);

    default <T> T convert(Object from, Class<T> to) {
        return convert(from, (Type) to);
    }

    default <T> T convert(Object from, Class<T> to, BeanOperator beanOperator) {
        return convert(from, (Type) to, beanOperator);
    }

    default <T> T convert(Object from, TypeRef<T> to) {
        return convert(from, to.getType());
    }

    default <T> T convert(Object from, TypeRef<T> to, BeanOperator beanOperator) {
        return convert(from, to.getType(), beanOperator);
    }

    class Builder extends ProcessByHandlersBuilder<BeanConverter, BeanConverterHandler, Builder> {

        @ReturnThreadSafeDependOn
        @Override
        public BeanConverter build() {
            return super.build();
        }

        @ReturnThreadSafeDependOn
        protected BeanConverter buildNew() {
            return new BeanConverterImpl(this);
        }

        @ThreadSafeDependOn
        private static final class BeanConverterImpl implements BeanConverter {

            private final BeanConverterHandler[] handlers;

            private BeanConverterImpl(Builder builder) {
                this.handlers = builder.handlers.toArray(new BeanConverterHandler[0]);
            }

            @Override
            public <T> T convert(Object from, Type to) {
                return convert(from, to, DefaultBeanOperator.getInstance());
            }

            @Override
            public <T> T convert(Object from, Type to, BeanOperator beanOperator) {
                for (BeanConverterHandler handler : handlers) {
                    if (handler.supportConvert(from, to, beanOperator)) {
                        return handler.convert(from, to, beanOperator);
                    }
                }
                throw new UnsupportedOperationException(
                        FormatHelper.fastFormat("Cannot convert object from {} to {}", from, to));
            }
        }
    }
}
