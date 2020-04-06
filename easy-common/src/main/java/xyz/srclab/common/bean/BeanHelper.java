package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.lang.TypeRef;

import java.lang.reflect.Type;
import java.util.Map;

public class BeanHelper {

    private static final BeanOperator beanOperator = BeanOperator.DEFAULT;

    public static BeanResolver getBeanResolver() {
        return beanOperator.getBeanResolver();
    }

    public static BeanConverter getBeanConverter() {
        return beanOperator.getBeanConverter();
    }

    public static BeanClass resolve(Class<?> beanClass) {
        return beanOperator.resolve(beanClass);
    }

    public static boolean containsProperty(Object bean, String propertyName) {
        return beanOperator.containsProperty(bean, propertyName);
    }

    @Nullable
    public static Object getProperty(Object bean, String propertyName) {
        return beanOperator.getProperty(bean, propertyName);
    }

    @Nullable
    public static <T> T getProperty(Object bean, String propertyName, Type type) {
        return beanOperator.getProperty(bean, propertyName, type);
    }

    @Nullable
    public static <T> T getProperty(Object bean, String propertyName, Class<T> type) {
        return beanOperator.getProperty(bean, propertyName, type);
    }

    @Nullable
    public static <T> T getProperty(Object bean, String propertyName, TypeRef<T> type) {
        return beanOperator.getProperty(bean, propertyName, type);
    }

    public static void setProperty(Object bean, String propertyName, @Nullable Object value) {
        beanOperator.setProperty(bean, propertyName, value);
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
}
