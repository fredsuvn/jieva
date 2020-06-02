package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;

import java.util.Map;

@Immutable
public interface BeanResolverHandler {

    void resolve(Class<?> beanClass, Map<String, BeanProperty> context);
}
