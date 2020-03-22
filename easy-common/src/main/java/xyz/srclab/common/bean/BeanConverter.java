package xyz.srclab.common.bean;

import org.jetbrains.annotations.Nullable;
import xyz.srclab.common.builder.ProcessByHandlersBuilder;
import xyz.srclab.common.format.FormatHelper;

public interface BeanConverter {

    static Builder newBuilder() {
        return Builder.newBuilder();
    }

    @Nullable
    <T> T convert(@Nullable Object from, Class<T> to);

    @Nullable
    <T> T convert(@Nullable Object from, Class<T> to, BeanOperator beanOperator);

    class Builder extends ProcessByHandlersBuilder<BeanConverter, BeanConverterHandler, Builder> {

        public static Builder newBuilder() {
            return new Builder();
        }

        protected BeanConverter buildNew() {
            return new BeanConverterImpl(this);
        }

        private static class BeanConverterImpl implements BeanConverter {

            private final BeanConverterHandler[] handlers;

            private BeanConverterImpl(Builder builder) {
                this.handlers = builder.handlers.toArray(new BeanConverterHandler[0]);
            }

            @Nullable
            @Override
            public <T> T convert(@Nullable Object from, Class<T> to) {
                return convert(from, to, CommonBeanOperator.getInstance());
            }

            @Override
            @Nullable
            public <T> T convert(@Nullable Object from, Class<T> to, BeanOperator beanOperator) {
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
