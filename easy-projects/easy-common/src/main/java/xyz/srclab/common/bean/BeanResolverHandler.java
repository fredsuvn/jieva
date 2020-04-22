package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;

@Immutable
public interface BeanResolverHandler {

    BeanResolverHandler DEFAULT = BeanSupport.getBeanResolverHandler();

    boolean supportBean(Class<?> beanClass);

    BeanStruct resolve(Class<?> beanClass);
}
