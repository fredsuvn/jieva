package xyz.srclab.common.bean;

public class CommonBeanOperatorIgnoreNull implements BeanOperator {

    public static CommonBeanOperatorIgnoreNull getInstance() {
        return INSTANCE;
    }

    private static final CommonBeanOperatorIgnoreNull INSTANCE = new CommonBeanOperatorIgnoreNull();

    @Override
    public BeanResolver getBeanResolver() {
        return CommonBeanResolver.getInstance();
    }

    @Override
    public BeanConverter getBeanConverter() {
        return CommonBeanConverter.getInstance();
    }

    @Override
    public BeanOperatorStrategy.CopyProperty getCopyPropertyStrategy() {
        return CommonBeanOperatorStrategy.CopyPropertyIgnoreNull.getInstance();
    }
}
