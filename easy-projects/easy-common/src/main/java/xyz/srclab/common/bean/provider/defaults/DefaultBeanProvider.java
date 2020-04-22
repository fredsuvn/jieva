package xyz.srclab.common.bean.provider.defaults;

import xyz.srclab.common.bean.*;
import xyz.srclab.common.bean.provider.BeanProvider;

/**
 * @author sunqian
 */
public class DefaultBeanProvider implements BeanProvider {

    @Override
    public BeanConverter getBeanConverter() {
        return DefaultBeanConverter.INSTANCE;
    }

    @Override
    public BeanConverterHandler getBeanConverterHandler() {
        return DefaultBeanConverterHandler.INSTANCE;
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
