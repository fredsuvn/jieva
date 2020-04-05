package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.builder.ProcessByHandlersBuilder;

@Immutable
public interface BeanResolver {

    BeanResolver DEFAULT = new DefaultBeanResolver();

    static Builder newBuilder() {
        return new Builder();
    }

    BeanClass resolve(Object bean);

    class Builder extends ProcessByHandlersBuilder<BeanResolver, BeanResolverHandler, Builder> {

        public static Builder newBuilder() {
            return new Builder();
        }

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
            public BeanClass resolve(Object bean) {
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
