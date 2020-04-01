package xyz.srclab.common.bean;

import xyz.srclab.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class DefaultBeanOperator implements BeanOperator {

    private final BeanResolver beanResolver = BeanResolver.DEFAULT;
    private final BeanConverter beanConverter = BeanConverter.DEFAULT;

    @Override
    public BeanResolver getBeanResolver() {
        return beanResolver;
    }

    @Override
    public BeanConverter getBeanConverter() {
        return beanConverter;
    }
}
