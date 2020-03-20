package xyz.srclab.common.bean;

import xyz.srclab.common.builder.ProcessByHandlersBuilder;
import xyz.srclab.common.format.FormatHelper;

public class BeanConverterBuilder
        extends ProcessByHandlersBuilder<BeanConverter, BeanConverterHandler, BeanConverterBuilder> {

    public static BeanConverterBuilder newBuilder() {
        return new BeanConverterBuilder();
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
