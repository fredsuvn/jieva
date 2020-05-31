package xyz.srclab.common.bean.provider.defaults;

import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.convert.ConvertHandler;
import xyz.srclab.common.bean.BeanOperator;

import java.lang.reflect.Type;

final class DefaultConverter implements Converter {

    static DefaultConverter INSTANCE = new DefaultConverter();

    private final ConvertHandler convertHandler = DefaultBeanSupport.getBeanConverterHandler();

    @Override
    public <T> T convert(Object from, Type to) {
        return convertHandler.convert(from, to, BeanOperator.DEFAULT);
    }

    @Override
    public <T> T convert(Object from, Type to, BeanOperator beanOperator) {
        return convertHandler.convert(from, to, beanOperator);
    }
}
