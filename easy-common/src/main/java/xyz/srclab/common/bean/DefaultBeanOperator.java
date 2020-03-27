package xyz.srclab.common.bean;

public class DefaultBeanOperator implements BeanOperator {

    public static DefaultBeanOperator getInstance() {
        return INSTANCE;
    }

    private static final DefaultBeanOperator INSTANCE = new DefaultBeanOperator();

    @Override
    public BeanResolver getBeanResolver() {
        return DefaultBeanResolver.getInstance();
    }

    @Override
    public BeanConverter getBeanConverter() {
        return DefaultBeanConverter.getInstance();
    }
}
