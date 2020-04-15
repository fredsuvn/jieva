package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;

import java.lang.reflect.Type;

@Immutable
public interface BeanConverterHandler {

    BeanConverterHandler DEFAULT = new DefaultBeanConverterHandler();

    boolean supportConvert(Object from, Type to, BeanOperator beanOperator);

    Object convert(Object from, Type to, BeanOperator beanOperator);
}
