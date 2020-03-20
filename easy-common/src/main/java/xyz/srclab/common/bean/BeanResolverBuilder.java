package xyz.srclab.common.bean;

import xyz.srclab.common.builder.ProcessByHandlersBuilder;

public class BeanResolverBuilder
        extends ProcessByHandlersBuilder<BeanResolver, BeanResolverHandler, BeanResolverBuilder> {

    public static BeanResolverBuilder newBuilder() {
        return new BeanResolverBuilder();
    }

    public BeanResolver build() {
        return new BeanResolverImpl(this.handlers.toArray(new BeanResolverHandler[0]));
    }

    private static class BeanResolverImpl implements BeanResolver {

        private final BeanResolverHandler[] handlers;

        private BeanResolverImpl(BeanResolverHandler[] handlers) {
            this.handlers = handlers;
        }

        @Override
        public BeanDescriptor resolve(Object bean) {
            for (BeanResolverHandler handler : handlers) {
                if (handler.supportBean(bean)) {
                    return handler.resolve(bean);
                }
            }
            throw new UnsupportedOperationException("Cannot resolve this bean: " + bean);
        }
    }
}
