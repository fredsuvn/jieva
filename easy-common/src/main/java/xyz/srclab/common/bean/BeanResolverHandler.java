package xyz.srclab.common.bean;

public interface BeanResolverHandler {

    boolean supportBean(Object bean, BeanOperator beanOperator);

    BeanDescriptor resolve(Object bean, BeanOperator beanOperator);
}
