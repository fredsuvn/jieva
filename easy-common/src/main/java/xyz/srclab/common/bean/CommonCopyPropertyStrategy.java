package xyz.srclab.common.bean;

public class CommonCopyPropertyStrategy implements BeanOperatorStrategy.CopyProperty {

    public static CommonCopyPropertyStrategy getInstance() {
        return INSTANCE;
    }

    private static final CommonCopyPropertyStrategy INSTANCE = new CommonCopyPropertyStrategy();

    public void copyProperty(
            BeanPropertyDescriptor sourceProperty, Object sourceBean,
            BeanPropertyDescriptor destProperty, Object destBean,
            BeanOperator beanOperator) {
        if (!sourceProperty.isReadable() || !destProperty.isWriteable()) {
            return;
        }
        Object sourceValue = sourceProperty.getValue(sourceBean);
        Class<?> type = destProperty.getType();
        Object destValue = beanOperator.convert(sourceValue, type);
        destProperty.setValue(destBean, destValue);
    }
}
