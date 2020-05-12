package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;

import java.lang.reflect.Type;

@Immutable
public interface BeanConverterHandler {

    BeanConverterHandler DEFAULT = BeanSupport.getBeanConverterHandler();

    boolean supportConvert(Object from, Type to, BeanOperator beanOperator);

    <T> T convert(Object from, Type to, BeanOperator beanOperator);
}
