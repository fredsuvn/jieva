package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.lang.TypeRef;

import java.lang.reflect.Type;

@Immutable
public interface BeanConverterHandler {

    BeanConverterHandler DEFAULT = new DefaultBeanConverterHandler();

    boolean supportConvert(Object from, Type to, BeanOperator beanOperator);

    default boolean supportConvert(Object from, Class<?> to, BeanOperator beanOperator) {
        return supportConvert(from, (Type) to, beanOperator);
    }

    default boolean supportConvert(Object from, TypeRef<?> to, BeanOperator beanOperator) {
        return supportConvert(from, to.getType(), beanOperator);
    }

    Object convert(Object from, Type to, BeanOperator beanOperator);

    default Object convert(Object from, Class<?> to, BeanOperator beanOperator) {
        return convert(from, (Type) to, beanOperator);
    }

    default Object convert(Object from, TypeRef<?> to, BeanOperator beanOperator) {
        return convert(from, to.getType(), beanOperator);
    }
}
