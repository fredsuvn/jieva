package xyz.srclab.common.bean;

import com.sun.javafx.fxml.PropertyNotFoundException;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.annotation.ReturnImmutable;
import xyz.srclab.annotation.concurrent.ReturnThreadSafeDependOn;
import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.annotation.concurrent.ThreadSafeDependOn;
import xyz.srclab.common.builder.CacheStateBuilder;
import xyz.srclab.common.collection.map.MapHelper;
import xyz.srclab.common.lang.TypeRef;
import xyz.srclab.common.reflect.instance.InstanceHelper;

import java.lang.reflect.Type;
import java.util.Map;

public interface BeanOperator {

    @ThreadSafe
    BeanOperator DEFAULT = new DefaultBeanOperator();

    static Builder newBuilder() {
        return new Builder();
    }

    BeanResolver getBeanResolver();

    BeanConverter getBeanConverter();

    default BeanDescriptor resolve(Object bean) {
        return getBeanResolver().resolve(bean);
    }

    default boolean containsProperty(Object bean, String propertyName) {
        BeanDescriptor beanDescriptor = resolve(bean);
        return beanDescriptor.containsProperty(propertyName);
    }

    @Nullable
    default Object getProperty(Object bean, String propertyName)
            throws PropertyNotFoundException, UnsupportedOperationException {
        BeanDescriptor beanDescriptor = resolve(bean);
        BeanPropertyDescriptor propertyDescriptor = beanDescriptor.getPropertyDescriptor(propertyName);
        return propertyDescriptor.getValue(bean);
    }

    @Nullable
    default <T> T getProperty(Object bean, String propertyName, Type type)
            throws PropertyNotFoundException, UnsupportedOperationException {
        @Nullable Object value = getProperty(bean, propertyName);
        if (value == null) {
            return null;
        }
        return convert(value, type);
    }

    @Nullable
    default <T> T getProperty(Object bean, String propertyName, Class<T> type)
            throws PropertyNotFoundException, UnsupportedOperationException {
        return getProperty(bean, propertyName, (Type) type);
    }

    @Nullable
    default <T> T getProperty(Object bean, String propertyName, TypeRef<T> type)
            throws PropertyNotFoundException, UnsupportedOperationException {
        return getProperty(bean, propertyName, type.getType());
    }

    default void setProperty(Object bean, String propertyName, @Nullable Object value)
            throws PropertyNotFoundException, UnsupportedOperationException {
        BeanDescriptor beanDescriptor = resolve(bean);
        BeanPropertyDescriptor propertyDescriptor = beanDescriptor.getPropertyDescriptor(propertyName);
        propertyDescriptor.setValue(bean, value == null ?
                null : convert(value, propertyDescriptor.getGenericType()));
    }

    default void copyProperties(Object source, Object dest) {
        copyProperties(source, dest, SetPropertyAction.COMMON);
    }

    default void copyPropertiesIgnoreNull(Object source, Object dest) {
        copyProperties(source, dest, SetPropertyAction.COMMON_IGNORE_NULL);
    }

    default void copyProperties(Object source, Object dest, SetPropertyAction setPropertyAction) {
        if (source instanceof Map && dest instanceof Map) {
            Map src = (Map) source;
            Map des = (Map) dest;
            src.forEach((k, v) -> {
                setPropertyAction.doSet(k, v, Object.class, value -> des.put(k, value), this);
            });
        } else if (source instanceof Map) {
            Map src = (Map) source;
            BeanDescriptor destDescriptor = resolve(dest);
            src.forEach((k, v) -> {
                String propertyName = String.valueOf(k);
                if (!destDescriptor.canWriteProperty(propertyName)) {
                    return;
                }
                BeanPropertyDescriptor destPropertyDescriptor = destDescriptor.getPropertyDescriptor(propertyName);
                setPropertyAction.doSet(
                        k,
                        v,
                        destPropertyDescriptor.getGenericType(),
                        value -> destPropertyDescriptor.setValue(dest, value),
                        this
                );
            });
        } else if (dest instanceof Map) {
            BeanDescriptor sourceDescriptor = resolve(source);
            Map des = (Map) dest;
            sourceDescriptor.getPropertyDescriptors().forEach((name, descriptor) -> {
                if (!descriptor.isReadable()) {
                    return;
                }
                @Nullable Object sourceValue = descriptor.getValue(source);
                setPropertyAction.doSet(
                        name, sourceValue, Object.class, value -> des.put(name, value), this);
            });
        } else {
            BeanDescriptor sourceDescriptor = resolve(source);
            BeanDescriptor destDescriptor = resolve(dest);
            sourceDescriptor.getPropertyDescriptors().forEach((name, sourcePropertyDescriptor) -> {
                if (!sourcePropertyDescriptor.isReadable()
                        || !destDescriptor.canWriteProperty(name)) {
                    return;
                }
                BeanPropertyDescriptor destPropertyDescriptor = destDescriptor.getPropertyDescriptor(name);
                @Nullable Object sourceValue = sourcePropertyDescriptor.getValue(source);
                setPropertyAction.doSet(
                        name,
                        sourceValue,
                        destPropertyDescriptor.getGenericType(),
                        value -> destPropertyDescriptor.setValue(dest, value),
                        this
                );
            });
        }
    }

    default void populateProperties(Object source, Map dest) {
        populateProperties(source, dest, SetPropertyAction.COMMON);
    }

    default void populatePropertiesIgnoreNull(Object source, Map dest) {
        populateProperties(source, dest, SetPropertyAction.COMMON_IGNORE_NULL);
    }

    default void populateProperties(Object source, Map dest, SetPropertyAction setPropertyAction) {
        if (source instanceof Map) {
            Map src = (Map) source;
            src.forEach((k, v) -> setPropertyAction.doSet(k, v, Object.class, value -> dest.put(k, value), this));
        } else {
            BeanDescriptor sourceDescriptor = resolve(source);
            sourceDescriptor.getPropertyDescriptors().forEach((name, descriptor) -> {
                if (!descriptor.isReadable()) {
                    return;
                }
                @Nullable Object sourceValue = descriptor.getValue(source);
                setPropertyAction.doSet(
                        name, sourceValue, Object.class, value -> dest.put(name, value), this);
            });
        }
    }

    default <T> T clone(T from) {
        T returned = InstanceHelper.newInstance(from.getClass());
        copyProperties(from, returned);
        return returned;
    }

    default <T> T convert(Object from, Type to) {
        return getBeanConverter().convert(from, to, this);
    }

    default <T> T convert(Object from, Class<T> to) {
        return getBeanConverter().convert(from, (Type) to, this);
    }

    default <T> T convert(Object from, TypeRef<T> to) {
        return getBeanConverter().convert(from, to.getType(), this);
    }

    TypeRef<Map<String, Object>> TO_MAP_TYPE_OF = new TypeRef<Map<String, Object>>() {
    };

    @ReturnImmutable
    default Map<String, Object> toMap(Object bean) {
        return MapHelper.immutableMap(getBeanConverter().convert(bean, TO_MAP_TYPE_OF, this));
    }

    class Builder extends CacheStateBuilder<BeanOperator> {

        private @Nullable BeanResolver beanResolver;
        private @Nullable BeanConverter beanConverter;

        public Builder setBeanResolver(BeanResolver beanResolver) {
            this.changeState();
            this.beanResolver = beanResolver;
            return this;
        }

        public Builder setBeanConverter(BeanConverter beanConverter) {
            this.changeState();
            this.beanConverter = beanConverter;
            return this;
        }

        @ReturnThreadSafeDependOn
        @Override
        public BeanOperator build() {
            return super.build();
        }

        @ReturnThreadSafeDependOn
        @Override
        protected BeanOperator buildNew() {
            return new BeanOperatorImpl(this);
        }

        @ThreadSafeDependOn
        private static final class BeanOperatorImpl implements BeanOperator {

            private final BeanResolver beanResolver;
            private final BeanConverter beanConverter;

            private BeanOperatorImpl(Builder builder) {
                this.beanResolver = builder.beanResolver == null ?
                        BeanResolver.DEFAULT : builder.beanResolver;
                this.beanConverter = new BeanConverterProxy(builder.beanConverter == null ?
                        BeanConverter.DEFAULT : builder.beanConverter);
            }

            @Override
            public BeanResolver getBeanResolver() {
                return beanResolver;
            }

            @Override
            public BeanConverter getBeanConverter() {
                return beanConverter;
            }

            @ThreadSafeDependOn
            private class BeanConverterProxy implements BeanConverter {

                private final BeanConverter proxied;

                private BeanConverterProxy(BeanConverter proxied) {
                    this.proxied = proxied;
                }

                @Override
                public <T> T convert(Object from, Type to) {
                    return proxied.convert(from, to, BeanOperatorImpl.this);
                }

                @Override
                public <T> T convert(Object from, Type to, BeanOperator beanOperator) {
                    return proxied.convert(from, to, beanOperator);
                }
            }
        }
    }

    interface SetPropertyAction {

        SetPropertyAction COMMON =
                (sourcePropertyName, sourcePropertyValue, destPropertyType, destPropertySetter, beanOperator) -> {
                    destPropertySetter.set(sourcePropertyValue == null ?
                            null : beanOperator.convert(sourcePropertyValue, destPropertyType));
                };

        SetPropertyAction COMMON_IGNORE_NULL =
                (sourcePropertyName, sourcePropertyValue, destPropertyType, destPropertySetter, beanOperator) -> {
                    if (sourcePropertyValue == null) {
                        return;
                    }
                    Object destPropertyValue = beanOperator.convert(sourcePropertyValue, destPropertyType);
                    destPropertySetter.set(destPropertyValue);
                };

        void doSet(
                Object sourcePropertyName, @Nullable Object sourcePropertyValue,
                Type destPropertyType, PropertySetter destPropertySetter,
                BeanOperator beanOperator
        );
    }

    interface PropertySetter {
        void set(@Nullable Object value);
    }
}
