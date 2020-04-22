package xyz.srclab.common.bean.provider.defaults;

import xyz.srclab.common.bean.BeanConverter;
import xyz.srclab.common.bean.BeanOperator;
import xyz.srclab.common.bean.BeanResolver;

final class DefaultBeanOperator implements BeanOperator {

    static DefaultBeanOperator INSTANCE = new DefaultBeanOperator();

    private final BeanResolver beanResolver = DefaultBeanSupport.getBeanResolver();
    private final BeanConverter beanConverter = DefaultBeanSupport.getBeanConverter();

    @Override
    public BeanResolver getBeanResolver() {
        return beanResolver;
    }

    @Override
    public BeanConverter getBeanConverter() {
        return beanConverter;
    }
}
