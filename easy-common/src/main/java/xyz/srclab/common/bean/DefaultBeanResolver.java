package xyz.srclab.common.bean;

public class DefaultBeanResolver implements BeanResolver {

    private final BeanResolverHandler beanResolverHandler = BeanResolverHandler.DEFAULT;

    @Override
    public BeanClass resolve(Object bean) {
        return beanResolverHandler.resolve(bean);
    }
}