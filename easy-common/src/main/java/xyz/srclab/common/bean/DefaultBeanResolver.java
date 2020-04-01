package xyz.srclab.common.bean;

import xyz.srclab.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class DefaultBeanResolver implements BeanResolver {

    private final BeanResolverHandler beanResolverHandler = new DefaultBeanResolverHandler();

    @Override
    public BeanDescriptor resolve(Object bean) {
        return beanResolverHandler.resolve(bean);
    }
}