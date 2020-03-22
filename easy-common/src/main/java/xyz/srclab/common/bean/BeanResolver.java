package xyz.srclab.common.bean;

import xyz.srclab.common.builder.ProcessByHandlersBuilder;

public interface BeanResolver {

    static Builder newBuilder() {
        return Builder.newBuilder();
    }

    BeanDescriptor resolve(Object bean);

    BeanDescriptor resolve(Object bean, BeanOperator beanOperator);

    class Builder extends ProcessByHandlersBuilder<BeanResolver, BeanResolverHandler, Builder> {

        public static Builder newBuilder() {
            return new Builder();
        }

        @Override
        protected BeanResolver buildNew() {
            return new BeanResolverImpl(this);
        }

        private static class BeanResolverImpl implements BeanResolver {

            private final BeanResolverHandler[] handlers;

            private BeanResolverImpl(Builder builder) {
                this.handlers = builder.handlers.toArray(new BeanResolverHandler[0]);
            }

            @Override
            public BeanDescriptor resolve(Object bean) {
                return resolve(bean, CommonBeanOperator.getInstance());
            }

            @Override
            public BeanDescriptor resolve(Object bean, BeanOperator beanOperator) {
                for (BeanResolverHandler handler : handlers) {
                    if (handler.supportBean(bean, beanOperator)) {
                        return handler.resolve(bean, beanOperator);
                    }
                }
                throw new UnsupportedOperationException("Cannot resolve this bean: " + bean);
            }
        }
    }
}
