package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.reflect.type.TypeRef;

import java.lang.reflect.Type;
import java.util.Map;

public class BeanHelper {

    private static final BeanOperator beanOperator = BeanOperator.DEFAULT;

    public static BeanStruct resolveBean(Class<?> beanClass) {
        return beanOperator.resolveBean(beanClass);
    }

    public static BeanProperty getProperty(Object bean, String propertyName) throws BeanPropertyNotFoundException {
        return beanOperator.getProperty(bean, propertyName);
    }

    @Nullable
    public static Object getPropertyValue(Object bean, String propertyName)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        return beanOperator.getPropertyValue(bean, propertyName);
    }

    @Nullable
    public static <T> T getPropertyValue(Object bean, String propertyName, Type type)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        return beanOperator.getPropertyValue(bean, propertyName, type);
    }

    @Nullable
    public static <T> T getPropertyValue(Object bean, String propertyName, Class<T> type)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        return beanOperator.getPropertyValue(bean, propertyName, type);
    }

    @Nullable
    public static <T> T getPropertyValue(Object bean, String propertyName, TypeRef<T> type)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        return beanOperator.getPropertyValue(bean, propertyName, type);
    }

    public static void setPropertyValue(Object bean, String propertyName, @Nullable Object value)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        beanOperator.setPropertyValue(bean, propertyName, value);
    }

    public static void copyProperties(Object source, Object dest) {
        beanOperator.copyProperties(source, dest);
    }

    public static void copyPropertiesIgnoreNull(Object source, Object dest) {
        beanOperator.copyPropertiesIgnoreNull(source, dest);
    }

    public static void copyProperties(Object source, Object dest, BeanOperator.EachProperty eachProperty) {
        beanOperator.copyProperties(source, dest, eachProperty);
    }

    public static <K, V> void populateProperties(Object source, Map<K, V> dest) {
        beanOperator.populateProperties(source, dest);
    }

    public static <K, V> void populatePropertiesIgnoreNull(Object source, Map<K, V> dest) {
        beanOperator.populatePropertiesIgnoreNull(source, dest);
    }

    public static <K, V> void populateProperties(Object source, Map<K, V> dest, BeanOperator.EachEntry<K, V> eachEntry) {
        beanOperator.populateProperties(source, dest, eachEntry);
    }

    public static <T> T clone(T from) {
        return beanOperator.clone(from);
    }

    public static <T> T convert(Object from, Type to) {
        return beanOperator.convert(from, to);
    }

    public static <T> T convert(Object from, Class<T> to) {
        return beanOperator.convert(from, to);
    }

    public static <T> T convert(Object from, TypeRef<T> to) {
        return beanOperator.convert(from, to);
    }

    @Immutable
    public static Map<String, Object> toMap(Object bean) {
        return beanOperator.toMap(bean);
    }

    public static BeanMethod getMethod(Object bean, String methodName, Class<?>... parameterTypes)
            throws BeanMethodNotFoundException {
        return beanOperator.getMethod(bean, methodName, parameterTypes);
    }

    public static BeanMethod getMethodBySignature(Object bean, String methodSignature)
            throws BeanMethodNotFoundException {
        return beanOperator.getMethodBySignature(bean, methodSignature);
    }
}
