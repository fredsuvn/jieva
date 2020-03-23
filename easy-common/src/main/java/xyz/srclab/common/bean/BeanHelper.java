package xyz.srclab.common.bean;

public class BeanHelper {

    public static BeanDescriptor resolve(Object bean) {
        return CommonBeanOperator.getInstance().resolve(bean);
    }

    public static Object getProperty(Object bean, String propertyName) {
        return CommonBeanOperator.getInstance().getProperty(bean, propertyName);
    }

    public static <T> T getProperty(Object bean, String propertyName, Class<T> type) {
        return CommonBeanOperator.getInstance().getProperty(bean, propertyName, type);
    }

    public static void setProperty(Object bean, String propertyName, Object value) {
        CommonBeanOperator.getInstance().setProperty(bean, propertyName, value);
    }

    public static void copyProperties(Object source, Object dest) {
        CommonBeanOperator.getInstance().copyProperties(source, dest);
    }

    public static void copyPropertiesIgnoreNull(Object source, Object dest) {
        CommonBeanOperatorIgnoreNull.getInstance().copyProperties(source, dest);
    }

    public static <T> T convert(Object from, Class<T> to) {
        return CommonBeanOperator.getInstance().convert(from, to);
    }

    public static <T> T copy(T from) {
        return CommonBeanOperator.getInstance().copy(from);
    }
}
