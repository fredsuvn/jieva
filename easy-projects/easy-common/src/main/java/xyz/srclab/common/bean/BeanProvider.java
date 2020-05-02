package xyz.srclab.common.bean;

/**
 * @author sunqian
 */
public interface BeanProvider {

    BeanConverter getBeanConverter();

    BeanConverterHandler getBeanConverterHandler();

    BeanResolver getBeanResolver();

    BeanResolverHandler getBeanResolverHandler();

    BeanOperator getBeanOperator();
}
