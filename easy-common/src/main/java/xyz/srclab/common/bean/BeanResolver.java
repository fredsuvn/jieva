package xyz.srclab.common.bean;

import xyz.srclab.common.builder.ProcessByHandlersBuilder;

public interface BeanResolver {

    static Builder newBuilder() {
        return Builder.newBuilder();
    }

    BeanDescriptor resolve(Object bean);

    class Builder
            extends ProcessByHandlersBuilder<BeanResolver, BeanResolverHandler, Builder> {

        public static Builder newBuilder() {
            return new Builder();
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
}
