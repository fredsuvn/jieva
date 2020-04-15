package xyz.srclab.common.bean;

import java.lang.reflect.Type;

public class DefaultBeanConverter implements BeanConverter {

    private final BeanConverterHandler beanConverterHandler = BeanConverterHandler.DEFAULT;

    @Override
    public Object convert(Object from, Type to) {
        return beanConverterHandler.convert(from, to, BeanOperator.DEFAULT);
    }

    @Override
    public Object convert(Object from, Type to, BeanOperator beanOperator) {
        return beanConverterHandler.convert(from, to, beanOperator);
    }
}
