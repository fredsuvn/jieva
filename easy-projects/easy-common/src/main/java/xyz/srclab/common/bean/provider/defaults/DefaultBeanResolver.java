package xyz.srclab.common.bean.provider.defaults;

import xyz.srclab.common.bean.BeanResolver;
import xyz.srclab.common.bean.BeanResolverHandler;
import xyz.srclab.common.bean.BeanClass;

final class DefaultBeanResolver implements BeanResolver {

    static DefaultBeanResolver INSTANCE = new DefaultBeanResolver();

    private final BeanResolverHandler beanResolverHandler = DefaultBeanSupport.getBeanResolverHandler();

    @Override
    public BeanClass resolve(Class<?> beanClass) {
        return beanResolverHandler.resolve(beanClass);
    }
}