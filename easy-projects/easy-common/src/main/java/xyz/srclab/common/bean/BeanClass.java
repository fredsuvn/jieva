package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Function;

@Immutable
public interface BeanClass {

    Class<?> type();

    @Nullable
    BeanProperty property(String propertyName);

    @Nullable
    default Object getPropertyValue(Object bean, String propertyName)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable BeanProperty beanProperty = property(propertyName);
        if (beanProperty == null) {
            throw new BeanPropertyNotFoundException(propertyName);
        }
        if (!beanProperty.readable()) {
            throw new UnsupportedOperationException("Cannot read property: " + beanProperty.name());
        }
        return beanProperty.getValue(bean);
    }

    @Nullable
    default <T> T getPropertyValue(Object bean, String propertyName, Class<T> type, Converter converter)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable Object value = getPropertyValue(bean, propertyName);
        return value == null ? null : converter.convert(value, type);
    }

    @Nullable
    default <T> T getPropertyValue(Object bean, String propertyName, Type type, Converter converter)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable Object value = getPropertyValue(bean, propertyName);
        return value == null ? null : converter.convert(value, type);
    }

    @Nullable
    default <T> T getPropertyValue(Object bean, String propertyName, TypeRef<T> type, Converter converter)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable Object value = getPropertyValue(bean, propertyName);
        return value == null ? null : converter.convert(value, type);
    }

    default void setPropertyValue(Object bean, String propertyName, @Nullable Object value)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable BeanProperty beanProperty = property(propertyName);
        if (beanProperty == null) {
            throw new BeanPropertyNotFoundException(propertyName);
        }
        if (!beanProperty.writeable()) {
            throw new UnsupportedOperationException("Cannot write property: " + beanProperty.name());
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
        if (!beanProperty.writeable()) {
            throw new UnsupportedOperationException("Cannot write property: " + beanProperty.name());
        }
        @Nullable Object targetValue = value == null ? null :
                converter.convert(value, beanProperty.genericType());
        beanProperty.setValue(bean, targetValue);
    }

    default boolean canReadProperty(String propertyName) {
        @Nullable BeanProperty property = property(propertyName);
        return property != null && property.readable();
    }

    default boolean canWriteProperty(String propertyName) {
        @Nullable BeanProperty property = property(propertyName);
        return property != null && property.writeable();
    }

    @Immutable
    Map<String, BeanProperty> properties();

    @Immutable
    Map<String, BeanProperty> readableProperties();

    @Immutable
    Map<String, BeanProperty> writeableProperties();

    @Immutable
    default Map<String, Object> toMap(Object bean) {
        return MapKit.map(readableProperties(), name -> name, property -> property.getValue(bean));
    }

    @Immutable
    default Map<String, Object> deepToMap(Object bean) {
        return deepToMap(bean, BeanClass0.newDeepToMapFunction(this));
    }

    @Immutable
    default Map<String, Object> deepToMap(Object bean, Function<Object, @Nullable Object> resolver) {
        return MapKit.map(readableProperties(), name -> name, property -> {
            @Nullable Object value = property.getValue(bean);
            return value == null ? null : resolver.apply(value);
        });
    }

    @Override
    int hashCode();

    @Override
    boolean equals(Object other);

    @Override
    String toString();
}
