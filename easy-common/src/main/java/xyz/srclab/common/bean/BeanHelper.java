package xyz.srclab.common.bean;

import xyz.srclab.common.lang.TypeRef;

import java.lang.reflect.Type;
import java.util.Map;

public class BeanHelper {

    public static BeanResolver getBeanResolver() {
        return CommonBeanOperator.getInstance().getBeanResolver();
    }

    public static BeanConverter getBeanConverter() {
        return CommonBeanOperator.getInstance().getBeanConverter();
    }

    public static BeanDescriptor resolve(Object bean) {
        return CommonBeanOperator.getInstance().resolve(bean);
    }

    public static boolean containsProperty(Object bean, String propertyName) {
        return CommonBeanOperator.getInstance().containsProperty(bean, propertyName);
    }

    public static Object getProperty(Object bean, String propertyName) {
        return CommonBeanOperator.getInstance().getProperty(bean, propertyName);
    }

    public static <T> T getProperty(Object bean, String propertyName, Type type) {
        return CommonBeanOperator.getInstance().getProperty(bean, propertyName, type);
    }

    public static <T> T getProperty(Object bean, String propertyName, Class<T> type) {
        return CommonBeanOperator.getInstance().getProperty(bean, propertyName, type);
    }

    public static <T> T getProperty(Object bean, String propertyName, TypeRef<T> type) {
        return CommonBeanOperator.getInstance().getProperty(bean, propertyName, type);
    }

    public static void setProperty(Object bean, String propertyName, Object value) {
        CommonBeanOperator.getInstance().setProperty(bean, propertyName, value);
    }

    public static void copyProperties(Object source, Object dest) {
        CommonBeanOperator.getInstance().copyProperties(source, dest);
    }

    public static void copyPropertiesIgnoreNull(Object source, Object dest) {
        CommonBeanOperator.getInstance().copyPropertiesIgnoreNull(source, dest);
    }

    public static void copyProperties(Object source, Object dest, BeanOperator.SetPropertyAction setPropertyAction) {
        CommonBeanOperator.getInstance().copyProperties(source, dest, setPropertyAction);
    }

    public static void populateProperties(Object source, Map dest) {
        CommonBeanOperator.getInstance().populateProperties(source, dest);
    }

    public static void populatePropertiesIgnoreNull(Object source, Map dest) {
        CommonBeanOperator.getInstance().populatePropertiesIgnoreNull(source, dest);
    }

    public static void populateProperties(Object source, Map dest, BeanOperator.SetPropertyAction setPropertyAction) {
        CommonBeanOperator.getInstance().populateProperties(source, dest, setPropertyAction);
    }

    public static <T> T clone(T from) {
        return CommonBeanOperator.getInstance().clone(from);
    }

    public static <T> T convert(Object from, Type to) {
        return CommonBeanOperator.getInstance().convert(from, to);
    }

    public static <T> T convert(Object from, Class<T> to) {
        return CommonBeanOperator.getInstance().convert(from, to);
    }

    public static <T> T convert(Object from, TypeRef<T> to) {
        return CommonBeanOperator.getInstance().convert(from, to);
    }

    public static Map<String, Object> toMap(Object bean) {
        return CommonBeanOperator.getInstance().toMap(bean);
    }
}
