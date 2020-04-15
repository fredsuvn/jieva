package xyz.srclab.common.bean;

import xyz.srclab.annotations.Immutable;

@Immutable
public interface BeanResolverHandler {

    BeanResolverHandler DEFAULT = new DefaultBeanResolverHandler();

    boolean supportBean(Class<?> beanClass);

    BeanStruct resolve(Class<?> beanClass);
}
