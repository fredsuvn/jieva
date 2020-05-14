package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.MapHelper;
import xyz.srclab.common.pattern.builder.CachedBuilder;
import xyz.srclab.common.reflect.MethodHelper;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

@Immutable
public interface BeanClass {

    static Builder newBuilder(Class<?> type) {
        return new Builder(type);
    }

    Class<?> getType();

    @Nullable
    BeanProperty getProperty(String propertyName);

    @Nullable
    default Object getPropertyValue(Object bean, String propertyName)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable BeanProperty beanProperty = getProperty(propertyName);
        if (beanProperty == null) {
            throw new BeanPropertyNotFoundException(propertyName);
        }
        if (!beanProperty.isReadable()) {
            throw new UnsupportedOperationException("Cannot read property: " + beanProperty.getName());
        }
        return beanProperty.getValue(bean);
    }

    @Nullable
    default <T> T getPropertyValue(Object bean, String propertyName, Type type, BeanConverter beanConverter)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable Object value = getPropertyValue(bean, propertyName);
        if (value == null) {
            return null;
        }
        return beanConverter.convert(value, type);
    }

    @Nullable
    default <T> T getPropertyValue(Object bean, String propertyName, Class<T> type, BeanConverter beanConverter)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        return getPropertyValue(bean, propertyName, (Type) type, beanConverter);
    }

    @Nullable
    default <T> T getPropertyValue(Object bean, String propertyName, TypeRef<T> type, BeanConverter beanConverter)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        return getPropertyValue(bean, propertyName, type.getType(), beanConverter);
    }

    default void setPropertyValue(Object bean, String propertyName, @Nullable Object value)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable BeanProperty beanProperty = getProperty(propertyName);
        if (beanProperty == null) {
            throw new BeanPropertyNotFoundException(propertyName);
        }
        if (!beanProperty.isWriteable()) {
            throw new UnsupportedOperationException("Cannot write property: " + beanProperty.getName());
        }
        beanProperty.setValue(bean, value);
    }

    default void setPropertyValue(
            Object bean, String propertyName, @Nullable Object value, BeanConverter beanConverter)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable BeanProperty beanProperty = getProperty(propertyName);
        if (beanProperty == null) {
            throw new BeanPropertyNotFoundException(propertyName);
        }
        if (!beanProperty.isWriteable()) {
            throw new UnsupportedOperationException("Cannot write property: " + beanProperty.getName());
        }
        @Nullable Object targetValue = value == null ? null :
                beanConverter.convert(value, beanProperty.getGenericType());
        beanProperty.setValue(bean, targetValue);
    }

    default boolean canReadProperty(String propertyName) {
        @Nullable BeanProperty property = getProperty(propertyName);
        return property != null && property.isReadable();
    }

    default boolean canWriteProperty(String propertyName) {
        @Nullable BeanProperty property = getProperty(propertyName);
        return property != null && property.isWriteable();
    }

    @Immutable
    Map<String, BeanProperty> getAllProperties();

    @Immutable
    Map<String, BeanProperty> getReadableProperties();

    @Immutable
    Map<String, BeanProperty> getWriteableProperties();

    @Immutable
    default Map<String, Object> toMap(Object bean) {
        return MapHelper.map(getReadableProperties(), name -> name, property -> property.getValue(bean));
    }

    @Nullable
    BeanMethod getMethod(String methodName, Class<?>... parameterTypes);

    @Nullable
    BeanMethod getMethod(Method method);

    @Immutable
    Map<Method, BeanMethod> getAllMethods();

    @Override
    boolean equals(Object other);

    @Override
    int hashCode();

    final class Builder extends CachedBuilder<BeanClass> {

        private final Class<?> type;
        private @Nullable Map<String, BeanProperty> propertyMap;
        private @Nullable Map<Method, BeanMethod> methodMap;

        public Builder(Class<?> type) {
            this.type = type;
        }

        public Builder setProperties(Map<String, BeanProperty> properties) {
            this.propertyMap = properties;
            this.updateState();
            return this;
        }

        public Builder setMethods(Map<Method, BeanMethod> methodMap) {
            this.methodMap = methodMap;
            this.updateState();
            return this;
        }

        @Override
        protected BeanClass buildNew() {
            return new BeanClassImpl(this);
        }

        private static final class BeanClassImpl implements BeanClass {

            private final Class<?> type;
            private final @Immutable Map<String, BeanProperty> propertyMap;
            private final @Immutable Map<String, BeanProperty> readablePropertyMap;
            private final @Immutable Map<String, BeanProperty> writeablePropertyMap;
            private final @Immutable Map<Method, BeanMethod> methodMap;

            private BeanClassImpl(Builder builder) {
                this.type = builder.type;
                this.propertyMap = builder.propertyMap == null ?
                        Collections.emptyMap() : MapHelper.immutable(builder.propertyMap);
                this.readablePropertyMap = MapHelper.filter(this.propertyMap, e -> e.getValue().isReadable());
                this.writeablePropertyMap = MapHelper.filter(this.propertyMap, e -> e.getValue().isWriteable());
                this.methodMap = builder.methodMap == null ?
                        Collections.emptyMap() : MapHelper.immutable(builder.methodMap);
            }

            @Override
            public Class<?> getType() {
                return type;
            }

            @Override
            public @Nullable BeanProperty getProperty(String propertyName) {
                return propertyMap.get(propertyName);
            }

            @Override
            public @Immutable Map<String, BeanProperty> getAllProperties() {
                return propertyMap;
            }

            @Override
            public @Immutable Map<String, BeanProperty> getReadableProperties() {
                return readablePropertyMap;
            }

            @Override
            public @Immutable Map<String, BeanProperty> getWriteableProperties() {
                return writeablePropertyMap;
            }

            @Override
            public @Nullable BeanMethod getMethod(String methodName, Class<?>... parameterTypes) {
                @Nullable Method method = MethodHelper.getMethod(type, methodName, parameterTypes);
                return method == null ? null : methodMap.get(method);
            }

            @Override
            public @Nullable BeanMethod getMethod(Method method) {
                return methodMap.get(method);
            }

            @Override
            public @Immutable Map<Method, BeanMethod> getAllMethods() {
                return methodMap;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o instanceof BeanClassImpl) {
                    return type.equals(((BeanClassImpl) o).type);
                }
                return false;
            }

            @Override
            public int hashCode() {
                return type.hashCode();
            }
        }
    }
}
