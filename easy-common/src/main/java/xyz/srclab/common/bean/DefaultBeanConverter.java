package xyz.srclab.common.bean;

import java.lang.reflect.Type;

public class DefaultBeanConverter implements BeanConverter {

    private final BeanConverterHandler beanConverterHandler = BeanConverterHandler.DEFAULT;

    @Override
    public <T> T convert(Object from, Type to) {
        return beanConverterHandler.convert(from, to, BeanOperator.DEFAULT);
    }

    @Override
    public <T> T convert(Object from, Type to, BeanOperator beanOperator) {
        return beanConverterHandler.convert(from, to, beanOperator);
    }

    @Override
    public <T> T convert(Object from, Class<T> to) {
        return beanConverterHandler.convert(from, to, BeanOperator.DEFAULT);
    }

    @Override
    public <T> T convert(Object from, Class<T> to, BeanOperator beanOperator) {
        return beanConverterHandler.convert(from, to, beanOperator);
    }
}
