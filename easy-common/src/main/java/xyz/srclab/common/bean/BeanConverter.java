package xyz.srclab.common.bean;

import xyz.srclab.common.builder.ProcessByHandlersBuilder;
import xyz.srclab.common.format.FormatHelper;

public interface BeanConverter {

    static Builder newBuilder() {
        return Builder.newBuilder();
    }

    <T> T convert(Object from, Class<T> to);

    class Builder extends ProcessByHandlersBuilder<BeanConverter, BeanConverterHandler, Builder> {

        public static Builder newBuilder() {
            return new Builder();
        }

        public BeanConverter build() {
            return new BeanConverterImpl(this.handlers.toArray(new BeanConverterHandler[0]));
        }

        private static class BeanConverterImpl implements BeanConverter {

            private final BeanConverterHandler[] handlers;

            private BeanConverterImpl(BeanConverterHandler[] handlers) {
                this.handlers = handlers;
            }

            @Override
            public <T> T convert(Object from, Class<T> to) {
                for (BeanConverterHandler handler : handlers) {
                    if (handler.supportConvert(from, to)) {
                        return handler.convert(from, to);
                    }
                }
                throw new UnsupportedOperationException(
                        FormatHelper.fastFormat("Cannot convert object from {} to {}", from, to));
            }
        }
    }
}
