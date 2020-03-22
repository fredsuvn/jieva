package xyz.srclab.common.bean;

public class CommonBeanOperatorStrategyIgnoreNull implements BeanOperatorStrategy {

    public static CommonBeanOperatorStrategyIgnoreNull getInstance() {
        return INSTANCE;
    }

    private static final CommonBeanOperatorStrategyIgnoreNull INSTANCE = new CommonBeanOperatorStrategyIgnoreNull();

    @Override
    public CopyProperty getCopyPropertyStrategy() {
        return CommonCopyPropertyStrategyIgnoreNull.getInstance();
    }
}
