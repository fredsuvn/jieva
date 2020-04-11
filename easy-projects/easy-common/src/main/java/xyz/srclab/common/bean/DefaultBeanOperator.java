package xyz.srclab.common.bean;

public class DefaultBeanOperator implements BeanOperator {

    private final BeanResolver beanResolver = BeanResolver.DEFAULT;
    private final BeanConverter beanConverter = BeanConverter.DEFAULT;

    @Override
    public BeanResolver getBeanResolver() {
        return beanResolver;
    }

    @Override
    public BeanConverter getBeanConverter() {
        return beanConverter;
    }
}
