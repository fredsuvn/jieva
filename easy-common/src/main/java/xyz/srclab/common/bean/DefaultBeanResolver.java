package xyz.srclab.common.bean;

public class DefaultBeanResolver implements BeanResolver {

    private final BeanResolverHandler beanResolverHandler = BeanResolverHandler.DEFAULT;

    @Override
    public BeanStruct resolve(Class<?> beanClass) {
        return beanResolverHandler.resolve(beanClass);
    }
}