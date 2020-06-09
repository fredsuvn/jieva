package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.IterableKit;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.reflect.ClassKit;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Immutable
public interface BeanClass {

    static BeanClass resolve(Object bean) {
        return BeanResolver.getDefault().resolve(bean.getClass());
    }

    static BeanClass newBeanClass(Class<?> type, Map<String, BeanProperty> propertyMap) {
        return BeanClass0.newBeanClass(type, propertyMap);
    }

    static Map<String, @Nullable Object> newBeanViewMap(Object bean, Map<String, BeanProperty> properties) {
        return BeanClass0.newBeanViewMap(bean, properties);
    }

    Class<?> type();

    @Immutable
    Map<String, BeanProperty> properties();

    default int propertiesSize() {
        return properties().size();
    }

    @Immutable
    default Map<String, BeanProperty> properties(String... propertyNames) {
        return properties(Arrays.asList(propertyNames));
    }

    @Immutable
    default Map<String, BeanProperty> properties(Iterable<String> propertyNames) {
        Set<String> set = IterableKit.toSet(propertyNames);
        return MapKit.filter(
                properties(),
                (name, property) -> set.contains(name)
        );
    }

    @Immutable
    Map<String, BeanProperty> readableProperties();

    @Immutable
    default Map<String, BeanProperty> readableProperties(String... propertyNames) {
        return readableProperties(Arrays.asList(propertyNames));
    }

    @Immutable
    default Map<String, BeanProperty> readableProperties(Iterable<String> propertyNames) {
        Set<String> set = IterableKit.toSet(propertyNames);
        return MapKit.filter(
                readableProperties(),
                (name, property) -> set.contains(name)
        );
    }

    @Immutable
    Map<String, BeanProperty> writeableProperties();

    @Immutable
    default Map<String, BeanProperty> writeableProperties(String... propertyNames) {
        return writeableProperties(Arrays.asList(propertyNames));
    }

    @Immutable
    default Map<String, BeanProperty> writeableProperties(Iterable<String> propertyNames) {
        Set<String> set = IterableKit.toSet(propertyNames);
        return MapKit.filter(
                writeableProperties(),
                (name, property) -> set.contains(name)
        );
    }

    @Nullable
    default BeanProperty property(String propertyName) {
        return properties().get(propertyName);
    }

    default boolean canReadProperty(String propertyName) {
        @Nullable BeanProperty property = property(propertyName);
        return property != null && property.readable();
    }

    default boolean canWriteProperty(String propertyName) {
        @Nullable BeanProperty property = property(propertyName);
        return property != null && property.writeable();
    }

    @Nullable
    default Object getPropertyValue(Object bean, String propertyName)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable BeanProperty beanProperty = property(propertyName);
        if (beanProperty == null) {
            throw new BeanPropertyNotFoundException(propertyName);
        }
        return beanProperty.getValue(bean);
    }

    @Nullable
    default <V> V getPropertyValue(Object bean, String propertyName, Class<V> type, Converter converter)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable BeanProperty beanProperty = property(propertyName);
        if (beanProperty == null) {
            throw new BeanPropertyNotFoundException(propertyName);
        }
        return beanProperty.getValue(bean, type, converter);
    }

    @Nullable
    default <V> V getPropertyValue(Object bean, String propertyName, Type type, Converter converter)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable BeanProperty beanProperty = property(propertyName);
        if (beanProperty == null) {
            throw new BeanPropertyNotFoundException(propertyName);
        }
        return beanProperty.getValue(bean, type, converter);
    }

    @Nullable
    default <V> V getPropertyValue(Object bean, String propertyName, TypeRef<V> type, Converter converter)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable BeanProperty beanProperty = property(propertyName);
        if (beanProperty == null) {
            throw new BeanPropertyNotFoundException(propertyName);
        }
        return beanProperty.getValue(bean, type, converter);
    }

    default void setPropertyValue(Object bean, String propertyName, @Nullable Object value)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable BeanProperty beanProperty = property(propertyName);
        if (beanProperty == null) {
            throw new BeanPropertyNotFoundException(propertyName);
        }
        beanProperty.setValue(bean, value);
    }

    default void setPropertyValue(
            Object bean, String propertyName, @Nullable Object value, Converter converter)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable BeanProperty beanProperty = property(propertyName);
        if (beanProperty == null) {
            throw new BeanPropertyNotFoundException(propertyName);
        }
        beanProperty.setValue(bean, value, converter);
    }

    @Immutable
    default Map<String, @Nullable Object> getPropertiesValues(Object bean, String... propertyNames) {
        return getPropertiesValues(bean, Arrays.asList(propertyNames));
    }

    @Immutable
    default Map<String, @Nullable Object> getPropertiesValues(Object bean, Iterable<String> propertyNames) {
        return MapKit.map(
                readableProperties(propertyNames),
                name -> name,
                property -> property.getValue(bean)
        );
    }

    default void setPropertiesValues(Object bean, Map<String, @Nullable Object> properties) {
        writeableProperties(properties.keySet())
                .forEach((name, property) -> property.setValue(bean, properties.get(name)));
    }

    default void setPropertiesValues(Object bean, Map<String, @Nullable Object> properties, Converter converter) {
        writeableProperties(properties.keySet())
                .forEach((name, property) -> property.setValue(bean, properties.get(name), converter));
    }

    default void setPropertiesValues(
            Object bean, Iterable<String> propertyNames, Function<String, @Nullable Object> function) {
        writeableProperties(propertyNames)
                .forEach((name, property) -> property.setValue(bean, function.apply(name)));
    }

    default void clearPropertiesValues(Object bean) {
        writeableProperties()
                .forEach((name, property) -> property.setValue(bean, null));
    }

    default Map<String, Object> asMap(Object bean) {
        return BeanClass.newBeanViewMap(bean, properties());
    }

    default Map<String, Object> asReadableMap(Object bean) {
        return BeanClass.newBeanViewMap(bean, readableProperties());
    }

    default Map<String, Object> asWriteableMap(Object bean) {
        return BeanClass.newBeanViewMap(bean, writeableProperties());
    }

    @Immutable
    default Map<String, @Nullable Object> toMap(Object bean) {
        return MapKit.map(readableProperties(), name -> name, property -> property.getValue(bean));
    }

    default Object toBean(Map<String, @Nullable Object> properties) {
        Object bean = ClassKit.newInstance(type());
        setPropertiesValues(bean, properties);
        return bean;
    }

    default Object toBean(Map<String, @Nullable Object> properties, Converter converter) {
        Object bean = ClassKit.newInstance(type());
        setPropertiesValues(bean, properties, converter);
        return bean;
    }

    default Object toBean(Iterable<String> propertyNames, Function<String, @Nullable Object> function) {
        Object bean = ClassKit.newInstance(type());
        setPropertiesValues(bean, propertyNames, function);
        return bean;
    }

    default Object duplicate(Object from) {
        return toBean(toMap(from));
    }

    @Override
    int hashCode();

    @Override
    boolean equals(Object other);

    @Override
    String toString();
}
