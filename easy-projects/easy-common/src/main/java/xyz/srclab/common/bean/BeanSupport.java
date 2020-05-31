package xyz.srclab.common.bean;

import xyz.srclab.common.ToovaBoot;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.convert.ConvertHandler;

/**
 * @author sunqian
 */
final class BeanSupport {

    private static final BeanProvider beanProvider = ToovaBoot.getProvider(BeanProvider.class);

    static BeanOperator getBeanOperator() {
        return beanProvider.getBeanOperator();
    }

    static Converter getBeanConverter() {
        return beanProvider.getBeanConverter();
    }

    static ConvertHandler getBeanConverterHandler() {
        return beanProvider.getBeanConverterHandler();
    }

    static BeanResolver getBeanResolver() {
        return beanProvider.getBeanResolver();
    }

    static BeanResolverHandler getBeanResolverHandler() {
        return beanProvider.getBeanResolverHandler();
    }
}
