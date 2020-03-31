package xyz.srclab.common.bean;

import xyz.srclab.annotation.concurrent.ThreadSafe;

import java.lang.reflect.Type;

@ThreadSafe
public class DefaultBeanConverter implements BeanConverter {

    public static DefaultBeanConverter getInstance() {
        return INSTANCE;
    }

    private static final DefaultBeanConverter INSTANCE = new DefaultBeanConverter();

    @Override
    public <T> T convert(Object from, Type to) {
        return DefaultBeanConverterHandler.getInstance().convert(from, to, DefaultBeanOperator.getInstance());
    }

    @Override
    public <T> T convert(Object from, Type to, BeanOperator beanOperator) {
        return DefaultBeanConverterHandler.getInstance().convert(from, to, beanOperator);
    }

    @Override
    public <T> T convert(Object from, Class<T> to) {
        return DefaultBeanConverterHandler.getInstance().convert(from, to, DefaultBeanOperator.getInstance());
    }

    @Override
    public <T> T convert(Object from, Class<T> to, BeanOperator beanOperator) {
        return DefaultBeanConverterHandler.getInstance().convert(from, to, beanOperator);
    }
}
