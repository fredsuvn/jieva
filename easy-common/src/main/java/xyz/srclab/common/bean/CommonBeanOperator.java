package xyz.srclab.common.bean;

public class CommonBeanOperator implements BeanOperator {

    public static CommonBeanOperator getInstance() {
        return INSTANCE;
    }

    private static final CommonBeanOperator INSTANCE = new CommonBeanOperator();

    @Override
    public BeanResolver getBeanResolver() {
        return CommonBeanResolver.getInstance();
    }

    @Override
    public BeanConverter getBeanConverter() {
        return CommonBeanConverter.getInstance();
    }
}
