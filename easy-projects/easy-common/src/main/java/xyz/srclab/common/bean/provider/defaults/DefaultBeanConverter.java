package xyz.srclab.common.bean.provider.defaults;

import xyz.srclab.common.bean.BeanConverter;
import xyz.srclab.common.bean.BeanConverterHandler;
import xyz.srclab.common.bean.BeanOperator;

import java.lang.reflect.Type;

final class DefaultBeanConverter implements BeanConverter {

    static DefaultBeanConverter INSTANCE = new DefaultBeanConverter();

    private final BeanConverterHandler beanConverterHandler = DefaultBeanSupport.getBeanConverterHandler();

    @Override
    public Object convert(Object from, Type to) {
        return beanConverterHandler.convert(from, to, BeanOperator.DEFAULT);
    }

    @Override
    public Object convert(Object from, Type to, BeanOperator beanOperator) {
        return beanConverterHandler.convert(from, to, beanOperator);
    }
}
