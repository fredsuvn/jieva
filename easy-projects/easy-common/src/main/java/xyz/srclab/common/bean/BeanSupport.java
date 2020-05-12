package xyz.srclab.common.bean;

import xyz.srclab.common.ToovaBoot;

/**
 * @author sunqian
 */
final class BeanSupport {

    private static final BeanProvider beanProvider = ToovaBoot.getProvider(BeanProvider.class);

    static BeanOperator getBeanOperator() {
        return beanProvider.getBeanOperator();
    }

    static BeanConverter getBeanConverter() {
        return beanProvider.getBeanConverter();
    }

    static BeanConverterHandler getBeanConverterHandler() {
        return beanProvider.getBeanConverterHandler();
    }

    static BeanResolver getBeanResolver() {
        return beanProvider.getBeanResolver();
    }

    static BeanResolverHandler getBeanResolverHandler() {
        return beanProvider.getBeanResolverHandler();
    }
}
