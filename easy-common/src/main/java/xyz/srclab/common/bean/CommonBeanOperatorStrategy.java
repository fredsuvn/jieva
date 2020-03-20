package xyz.srclab.common.bean;

public class CommonBeanOperatorStrategy implements BeanOperatorStrategy {

    public static class CopyProperty implements BeanOperatorStrategy.CopyProperty {

        public static CopyProperty getInstance() {
            return INSTANCE;
        }

        private static final CopyProperty INSTANCE = new CopyProperty();

        public void copyProperty(
                BeanPropertyDescriptor sourceProperty, Object sourceBean,
                BeanPropertyDescriptor destProperty, Object destBean,
                BeanConverter beanConverter) {
            if (!sourceProperty.isReadable() || !destProperty.isWriteable()) {
                return;
            }
            Object sourceValue = sourceProperty.getValue(sourceBean);
            Class<?> type = destProperty.getType();
            Object destValue = beanConverter.convert(sourceValue, type);
            destProperty.setValue(destBean, destValue);
        }
    }

    public static class CopyPropertyIgnoreNull implements BeanOperatorStrategy.CopyProperty {

        public static CopyPropertyIgnoreNull getInstance() {
            return INSTANCE;
        }

        private static final CopyPropertyIgnoreNull INSTANCE = new CopyPropertyIgnoreNull();

        public void copyProperty(
                BeanPropertyDescriptor sourceProperty, Object sourceBean,
                BeanPropertyDescriptor destProperty, Object destBean,
                BeanConverter beanConverter) {
            if (!sourceProperty.isReadable() || !destProperty.isWriteable()) {
                return;
            }
            Object sourceValue = sourceProperty.getValue(sourceBean);
            if (sourceValue == null) {
                return;
            }
            Class<?> type = destProperty.getType();
            Object destValue = beanConverter.convert(sourceValue, type);
            destProperty.setValue(destBean, destValue);
        }
    }
}
