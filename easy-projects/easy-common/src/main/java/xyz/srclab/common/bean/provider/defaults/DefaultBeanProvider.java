package xyz.srclab.common.bean.provider.defaults;

import xyz.srclab.common.bean.*;
import xyz.srclab.common.bean.BeanProvider;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.convert.ConvertHandler;

/**
 * @author sunqian
 */
public class DefaultBeanProvider implements BeanProvider {

    @Override
    public Converter getBeanConverter() {
        return DefaultConverter.INSTANCE;
    }

    @Override
    public ConvertHandler getBeanConverterHandler() {
        return DefaultConvertHandler.INSTANCE;
    }

    @Override
    public BeanResolver getBeanResolver() {
        return DefaultBeanResolver.INSTANCE;
    }

    @Override
    public BeanResolverHandler getBeanResolverHandler() {
        return DefaultBeanResolverHandler.INSTANCE;
    }

    @Override
    public BeanOperator getBeanOperator() {
        return DefaultBeanOperator.INSTANCE;
    }
}
