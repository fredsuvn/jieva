package xyz.srclab.common.bean;

import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.convert.ConvertHandler;

/**
 * @author sunqian
 */
public interface BeanProvider {

    Converter getBeanConverter();

    ConvertHandler getBeanConverterHandler();

    BeanResolver getBeanResolver();

    BeanResolverHandler getBeanResolverHandler();

    BeanOperator getBeanOperator();
}
