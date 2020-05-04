package xyz.srclab.common.bean.provider.defaults;

import xyz.srclab.common.bean.*;

/**
 * @author sunqian
 */
final class DefaultBeanSupport {

    private static final BeanProvider beanProvider = new DefaultBeanProvider();

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
