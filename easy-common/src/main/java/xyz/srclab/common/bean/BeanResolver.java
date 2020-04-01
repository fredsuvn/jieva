package xyz.srclab.common.bean;

import xyz.srclab.annotation.concurrent.ReturnThreadSafeDependOn;
import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.annotation.concurrent.ThreadSafeDependOn;
import xyz.srclab.common.builder.ProcessByHandlersBuilder;

public interface BeanResolver {

    @ThreadSafe
    BeanResolver DEFAULT = new DefaultBeanResolver();

    static Builder newBuilder() {
        return new Builder();
    }

    BeanDescriptor resolve(Object bean);

    class Builder extends ProcessByHandlersBuilder<BeanResolver, BeanResolverHandler, Builder> {

        public static Builder newBuilder() {
            return new Builder();
        }

        @ReturnThreadSafeDependOn
        @Override
        public BeanResolver build() {
            return super.build();
        }

        @ReturnThreadSafeDependOn
        @Override
        protected BeanResolver buildNew() {
            return new BeanResolverImpl(this);
        }

        @ThreadSafeDependOn
        private static final class BeanResolverImpl implements BeanResolver {

            private final BeanResolverHandler[] handlers;

            private BeanResolverImpl(Builder builder) {
                this.handlers = builder.handlers.toArray(new BeanResolverHandler[0]);
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
