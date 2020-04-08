package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.builder.ProcessByHandlersBuilder;

@Immutable
public interface BeanResolver {

    BeanResolver DEFAULT = new DefaultBeanResolver();

    static Builder newBuilder() {
        return new Builder();
    }

    BeanClass resolve(Class<?> beanClass);

    class Builder extends ProcessByHandlersBuilder<BeanResolver, BeanResolverHandler, Builder> {

        @Override
        public BeanResolver build() {
            return super.build();
        }

        @Override
        protected BeanResolver buildNew() {
            return new BeanResolverImpl(this);
        }

        private static final class BeanResolverImpl implements BeanResolver {

            private final BeanResolverHandler[] handlers;

            private BeanResolverImpl(Builder builder) {
                this.handlers = builder.handlers.toArray(new BeanResolverHandler[0]);
            }

            @Override
            public BeanClass resolve(Class<?> beanClass) {
                for (BeanResolverHandler handler : handlers) {
                    if (handler.supportBean(beanClass)) {
                        return handler.resolve(beanClass);
                    }
                }
                throw new UnsupportedOperationException("Cannot resolve this class: " + beanClass);
            }
        }
    }
}
