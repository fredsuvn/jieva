package xyz.srclab.common.bean;

import xyz.srclab.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class DefaultBeanResolver implements BeanResolver {

    public static DefaultBeanResolver getInstance() {
        return INSTANCE;
    }

    private static final DefaultBeanResolver INSTANCE = new DefaultBeanResolver();

    private final BeanResolverHandler beanResolverHandler = DefaultBeanResolverHandler.getInstance();

    @Override
    public BeanDescriptor resolve(Object bean) {
        return beanResolverHandler.resolve(bean);
    }
}