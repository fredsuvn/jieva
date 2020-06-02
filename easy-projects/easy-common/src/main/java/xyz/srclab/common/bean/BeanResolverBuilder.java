package xyz.srclab.common.bean;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.pattern.builder.HandlersBuilder;

import java.util.List;

public class BeanResolverBuilder extends HandlersBuilder<BeanResolver, BeanResolverHandler, BeanResolverBuilder> {

    @Override
    protected BeanResolver buildNew() {
        //return new BeanResolverImpl(this);
        return null;
    }

    private static final class BeanResolverImpl implements BeanResolver {

        private final BeanResolverHandler[] handlers;

        private BeanResolverImpl(List<BeanResolverHandler> handlers) {
            this.handlers = handlers.toArray(new BeanResolverHandler[0]);
        }

        @Override
        public BeanClass resolve(Class<?> beanClass) {
            for (BeanResolverHandler handler : handlers) {
                @Nullable BeanClass result = handler.resolve(beanClass);
                if (result != null) {
                    return result;
                }
            }
            throw new UnsupportedOperationException("Cannot resolve this class: " + beanClass);
        }
    }
}
