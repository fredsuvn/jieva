package xyz.srclab.common.bean;

import xyz.srclab.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class DefaultBeanOperator implements BeanOperator {

    public static DefaultBeanOperator getInstance() {
        return INSTANCE;
    }

    private static final DefaultBeanOperator INSTANCE = new DefaultBeanOperator();

    private final BeanResolver beanResolver = DefaultBeanResolver.getInstance();
    private final BeanConverter beanConverter = DefaultBeanConverter.getInstance();

    @Override
    public BeanResolver getBeanResolver() {
        return beanResolver;
    }

    @Override
    public BeanConverter getBeanConverter() {
        return beanConverter;
    }
}
