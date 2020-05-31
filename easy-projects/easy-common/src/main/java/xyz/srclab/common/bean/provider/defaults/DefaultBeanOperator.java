package xyz.srclab.common.bean.provider.defaults;

import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.bean.BeanOperator;
import xyz.srclab.common.bean.BeanResolver;

final class DefaultBeanOperator implements BeanOperator {

    static DefaultBeanOperator INSTANCE = new DefaultBeanOperator();

    private final BeanResolver beanResolver = DefaultBeanSupport.getBeanResolver();
    private final Converter converter = DefaultBeanSupport.getBeanConverter();

    @Override
    public BeanResolver getBeanResolver() {
        return beanResolver;
    }

    @Override
    public Converter getConverter() {
        return converter;
    }
}
