package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.reflect.ClassKit;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;

@Immutable
public interface BeanClass {

    Class<?> type();

    @Nullable
    BeanProperty getProperty(String propertyName);

    @Immutable
    default Map<String, BeanProperty> getProperties(Object bean, String... propertyNames) {
        return getProperties(bean, Arrays.asList(propertyNames));
    }

    @Immutable
    Map<String, BeanProperty> getProperties(Object bean, Iterable<? extends String> propertyNames);

    @Nullable
    default Object getPropertyValue(Object bean, String propertyName)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable BeanProperty beanProperty = getProperty(propertyName);
        if (beanProperty == null) {
            throw new BeanPropertyNotFoundException(propertyName);
        }
        return beanProperty.getValue(bean);
    }

    @Nullable
    default <V> V getPropertyValue(Object bean, String propertyName, Class<V> type, Converter converter)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable BeanProperty beanProperty = getProperty(propertyName);
        if (beanProperty == null) {
            throw new BeanPropertyNotFoundException(propertyName);
        }
        return beanProperty.getValue(bean, type, converter);
    }

    @Nullable
    default <V> V getPropertyValue(Object bean, String propertyName, Type type, Converter converter)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable BeanProperty beanProperty = getProperty(propertyName);
        if (beanProperty == null) {
            throw new BeanPropertyNotFoundException(propertyName);
        }
        return beanProperty.getValue(bean, type, converter);
    }

    @Nullable
    default <V> V getPropertyValue(Object bean, String propertyName, TypeRef<V> type, Converter converter)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable BeanProperty beanProperty = getProperty(propertyName);
        if (beanProperty == null) {
            throw new BeanPropertyNotFoundException(propertyName);
        }
        return beanProperty.getValue(bean, type, converter);
    }

    @Immutable
    default Map<String, Object> getPropertyValues(Object bean, String... propertyNames) {
        return getPropertyValues(bean, Arrays.asList(propertyNames));
    }

    @Immutable
    default Map<String, Object> getPropertyValues(Object bean, Iterable<? extends String> propertyNames) {
        return MapKit.map(
                MapKit.filter(getProperties(bean, propertyNames), e -> e.getValue().readable()),
                name -> name,
                property -> property.getValue(bean)
        );
    }

    default void setPropertyValue(Object bean, String propertyName, @Nullable Object value)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable BeanProperty beanProperty = getProperty(propertyName);
        if (beanProperty == null) {
            throw new BeanPropertyNotFoundException(propertyName);
        }
        beanProperty.setValue(bean, value);
    }

    default void setPropertyValue(
            Object bean, String propertyName, @Nullable Object value, Converter converter)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable BeanProperty beanProperty = getProperty(propertyName);
        if (beanProperty == null) {
            throw new BeanPropertyNotFoundException(propertyName);
        }
        beanProperty.setValue(bean, value, converter);
    }

    default void setPropertyValues(Object bean, Map<String, Object> properties) {
        MapKit.filter(
                getProperties(bean, properties.keySet()),
                e -> e.getValue().writeable()
        ).forEach((name, property) -> property.setValue(bean, properties.get(name)));
    }

    default void setPropertyValues(Object bean, Map<String, Object> properties, Converter converter) {
        MapKit.filter(
                getProperties(bean, properties.keySet()),
                e -> e.getValue().writeable()
        ).forEach((name, property) -> property.setValue(bean, properties.get(name), converter));
    }

    default boolean canReadProperty(String propertyName) {
        @Nullable BeanProperty property = getProperty(propertyName);
        return property != null && property.readable();
    }

    default boolean canWriteProperty(String propertyName) {
        @Nullable BeanProperty property = getProperty(propertyName);
        return property != null && property.writeable();
    }

    @Immutable
    Map<String, BeanProperty> getAllProperties();

    @Immutable
    Map<String, BeanProperty> getReadableProperties();

    @Immutable
    Map<String, BeanProperty> getWriteableProperties();

    default Map<String, Object> asMap(Object bean) {

    }

    @Immutable
    default Map<String, Object> toMap(Object bean) {
        return MapKit.map(getReadableProperties(), name -> name, property -> property.getValue(bean));
    }

    @Immutable
    default Map<String, Object> deepToMap(Object bean) {
        return deepToMap(bean, BeanOperator.getDefault());
    }

    @Immutable
    default Map<String, Object> deepToMap(Object bean, BeanOperator operator) {
        return MapKit.map(getReadableProperties(), name -> name, property -> {
            @Nullable Object value = property.getValue(bean);
            if (value == null) {
                return null;
            }
            return BeanClass0.getDeepToMapFunction(operator).apply(value);
        });
    }

    default <T, K, V> T toBean(Map<K, V> map) {
        T bean = ClassKit.newInstance(type());
        fill(map, bean);
        return bean;
    }

    default <T, K, V> T toBean(Map<K, V> map, Converter converter) {
        T bean = ClassKit.newInstance(type());
        fill(map, bean, converter);
        return bean;
    }

    default <K, V> void fill(Map<K, V> map, Object bean) {
        map.forEach((k, v) -> {
            String propertyName = String.valueOf(k);
            @Nullable BeanProperty property = getProperty(propertyName);
            if (property == null || !property.writeable()) {
                return;
            }
            property.setValue(bean, v);
        });
    }

    default <K, V> void fill(Map<K, V> map, Object bean, Converter converter) {
        map.forEach((k, v) -> {
            String propertyName = String.valueOf(k);
            @Nullable BeanProperty property = getProperty(propertyName);
            if (property == null || !property.writeable()) {
                return;
            }
            if (v == null) {
                property.setValue(bean, null);
                return;
            }
            property.setValue(bean, converter.convert(v, property.genericType()));
        });
    }

    @Override
    int hashCode();

    @Override
    boolean equals(Object other);

    @Override
    String toString();
}
