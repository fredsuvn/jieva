package xyz.srclab.common.bean.provider.defaults;

import xyz.srclab.common.bean.*;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.convert.ConvertHandler;

/**
 * @author sunqian
 */
final class DefaultBeanSupport {

    private static final BeanProvider beanProvider = new DefaultBeanProvider();

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
