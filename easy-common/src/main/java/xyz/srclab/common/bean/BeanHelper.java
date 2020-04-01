package xyz.srclab.common.bean;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.annotation.ReturnImmutable;
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

    public static BeanDescriptor resolve(Object bean) {
        return beanOperator.resolve(bean);
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

    public static void copyProperties(Object source, Object dest, BeanOperator.SetPropertyAction setPropertyAction) {
        beanOperator.copyProperties(source, dest, setPropertyAction);
    }

    public static void populateProperties(Object source, Map dest) {
        beanOperator.populateProperties(source, dest);
    }

    public static void populatePropertiesIgnoreNull(Object source, Map dest) {
        beanOperator.populatePropertiesIgnoreNull(source, dest);
    }

    public static void populateProperties(Object source, Map dest, BeanOperator.SetPropertyAction setPropertyAction) {
        beanOperator.populateProperties(source, dest, setPropertyAction);
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

    @ReturnImmutable
    public static Map<String, Object> toMap(Object bean) {
        return beanOperator.toMap(bean);
    }
}
