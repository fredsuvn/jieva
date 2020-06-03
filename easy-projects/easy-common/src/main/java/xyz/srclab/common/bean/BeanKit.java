package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Function;

public class BeanKit {

    private static final BeanOperator beanOperator = BeanOperator.getDefault();

    public static BeanClass resolveBean(Class<?> beanClass) {
        return beanOperator.resolveBean(beanClass);
    }

    @Nullable
    public static BeanProperty getProperty(Object bean, String propertyName) {
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

    public static BeanOperator.CopyPreparation prepareCopyProperties(Object source, Object dest) {
        return beanOperator.prepareCopyProperties(source, dest);
    }

    public static <K, V> void populateProperties(Object source, Map<K, V> dest) {
        beanOperator.populateProperties(source, dest);
    }

    public static <K, V> void populatePropertiesIgnoreNull(Object source, Map<K, V> dest) {
        beanOperator.populatePropertiesIgnoreNull(source, dest);
    }

    public static <K, V> BeanOperator.PopulatePreparation<K, V> preparePopulateProperties(
            Object source, Map<K, V> dest) {
        return beanOperator.preparePopulateProperties(source, dest);
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

    @Immutable
    public static Map<String, Object> deepToMap(Object bean) {
        return beanOperator.deepToMap(bean);
    }

    @Immutable
    public static Map<String, Object> deepToMap(Object bean, Function<Object, @Nullable Object> resolver) {
        return beanOperator.deepToMap(bean, resolver);
    }

    public static <T, K, V> T toBean(Map<K, V> map, Class<T> beanType) {
        return beanOperator.toBean(map, beanType);
    }

    public static <T, K, V> void toBean(Map<K, V> map, T bean) {
        beanOperator.toBean(map, bean);
    }
}
