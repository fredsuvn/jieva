package xyz.srclab.common.bean;

public class CommonCopyPropertyStrategyIgnoreNull implements BeanOperatorStrategy.CopyProperty {

    public static CommonCopyPropertyStrategyIgnoreNull getInstance() {
        return INSTANCE;
    }

    private static final CommonCopyPropertyStrategyIgnoreNull INSTANCE = new CommonCopyPropertyStrategyIgnoreNull();

    public void copyProperty(
            BeanPropertyDescriptor sourceProperty, Object sourceBean,
            BeanPropertyDescriptor destProperty, Object destBean,
            BeanOperator beanOperator) {
        if (!sourceProperty.isReadable() || !destProperty.isWriteable()) {
            return;
        }
        Object sourceValue = sourceProperty.getValue(sourceBean);
        if (sourceValue == null) {
            return;
        }
        Class<?> type = destProperty.getType();
        Object destValue = beanOperator.convert(sourceValue, type);
        destProperty.setValue(destBean, destValue);
    }
}
