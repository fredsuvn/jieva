package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;

@Immutable
public interface BeanResolver {

    static BeanResolver getDefault() {
        return BeanResolver0.getDefault();
    }

    static BeanResolverBuilder newBuilder() {
        return BeanResolver0.newResolverBuilder();
    }

    BeanClass resolve(Class<?> beanClass);
}
