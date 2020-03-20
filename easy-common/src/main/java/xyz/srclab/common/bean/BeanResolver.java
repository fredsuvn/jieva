package xyz.srclab.common.bean;

public interface BeanResolver {

    static BeanResolverBuilder newBuilder() {
        return BeanResolverBuilder.newBuilder();
    }

    BeanDescriptor resolve(Object bean);
}
