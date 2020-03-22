package xyz.srclab.common.bean;

public class CommonBeanOperatorStrategy implements BeanOperatorStrategy {

    public static CommonBeanOperatorStrategy getInstance() {
        return INSTANCE;
    }

    private static final CommonBeanOperatorStrategy INSTANCE = new CommonBeanOperatorStrategy();

    @Override
    public BeanOperatorStrategy.CopyProperty getCopyPropertyStrategy() {
        return CommonCopyPropertyStrategy.getInstance();
    }
}
