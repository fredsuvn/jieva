package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.pattern.builder.HandlersBuilder;

@Immutable
public interface BeanResolver {

    BeanResolver DEFAULT = BeanSupport.getBeanResolver();

    static Builder newBuilder() {
        return new Builder();
    }

    BeanStruct resolve(Class<?> beanClass);

    final class Builder extends HandlersBuilder<BeanResolver, BeanResolverHandler, Builder> {

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
            public BeanStruct resolve(Class<?> beanClass) {
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
