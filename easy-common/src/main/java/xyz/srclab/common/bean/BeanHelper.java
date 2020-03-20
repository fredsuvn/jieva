package xyz.srclab.common.bean;

public class BeanHelper {

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
