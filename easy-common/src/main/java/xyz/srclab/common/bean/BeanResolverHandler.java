package xyz.srclab.common.bean;

public interface BeanResolverHandler {

    boolean supportBean(Object bean);

    BeanDescriptor resolve(Object bean);
}
