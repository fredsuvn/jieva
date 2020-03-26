package xyz.srclab.common.bean;

import org.jetbrains.annotations.Nullable;
import xyz.srclab.common.builder.CacheStateBuilder;
import xyz.srclab.common.lang.TypeRef;
import xyz.srclab.common.reflect.ReflectHelper;

import java.lang.reflect.Type;
import java.util.Map;

public interface BeanOperator {

    static Builder newBuilder() {
        return Builder.newBuilder();
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

    default Object getProperty(Object bean, String propertyName) {
        BeanDescriptor beanDescriptor = resolve(bean);
        if (!beanDescriptor.canReadProperty(propertyName)) {
            throw new UnsupportedOperationException("Property is not readable: " + propertyName);
        }
        BeanPropertyDescriptor propertyDescriptor = beanDescriptor.getPropertyDescriptor(propertyName);
        return propertyDescriptor.getValue(bean);
    }

    default <T> T getProperty(Object bean, String propertyName, Type type) {
        Object property = getProperty(bean, propertyName);
        return convert(property, type);
    }

    default <T> T getProperty(Object bean, String propertyName, Class<T> type) {
        return getProperty(bean, propertyName, (Type) type);
    }

    default <T> T getProperty(Object bean, String propertyName, TypeRef<T> type) {
        return getProperty(bean, propertyName, type.getType());
    }

    default void setProperty(Object bean, String propertyName, Object value) {
        BeanDescriptor beanDescriptor = resolve(bean);
        if (!beanDescriptor.canWriteProperty(propertyName)) {
            throw new UnsupportedOperationException("Property is not writeable: " + propertyName);
        }
        BeanPropertyDescriptor propertyDescriptor = beanDescriptor.getPropertyDescriptor(propertyName);
        Type type = propertyDescriptor.getGenericType();
        propertyDescriptor.setValue(bean, convert(value, type));
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
                Object sourceValue = descriptor.getValue(source);
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
                Object sourceValue = sourcePropertyDescriptor.getValue(source);
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
                Object sourceValue = descriptor.getValue(source);
                setPropertyAction.doSet(
                        name, sourceValue, Object.class, value -> dest.put(name, value), this);
            });
        }
    }

    default <T> T clone(T from) {
        T returned = (T) ReflectHelper.newInstance(from.getClass());
        copyProperties(from, returned);
        return returned;
    }

    default <T> T convert(Object from, Type to) {
        return getBeanConverter().convert(from, to, this);
    }

    default <T> T convert(Object from, Class<T> to) {
        return convert(from, (Type) to);
    }

    default <T> T convert(Object from, TypeRef<T> to) {
        return convert(from, to.getType());
    }

    TypeRef<Map<String, Object>> TO_MAP_TYPE_OF = new TypeRef<Map<String, Object>>() {
    };

    default Map<String, Object> toMap(Object bean) {
        return convert(bean, TO_MAP_TYPE_OF);
    }

    class Builder extends CacheStateBuilder<BeanOperator> {

        public static Builder newBuilder() {
            return new Builder();
        }

        private BeanResolver beanResolver;
        private BeanConverter beanConverter;

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

        @Override
        protected BeanOperator buildNew() {
            return new BeanOperatorImpl(this);
        }

        private static final class BeanOperatorImpl implements BeanOperator {

            private final BeanResolver beanResolver;
            private final BeanConverter beanConverter;

            private BeanOperatorImpl(Builder builder) {
                this.beanResolver = builder.beanResolver == null ?
                        CommonBeanResolver.getInstance() : builder.beanResolver;
                this.beanConverter = new BeanConverterProxy(builder.beanConverter == null ?
                        CommonBeanConverter.getInstance() : builder.beanConverter);
            }

            @Override
            public BeanResolver getBeanResolver() {
                return beanResolver;
            }

            @Override
            public BeanConverter getBeanConverter() {
                return beanConverter;
            }

            private class BeanConverterProxy implements BeanConverter {

                private final BeanConverter proxied;

                private BeanConverterProxy(BeanConverter proxied) {
                    this.proxied = proxied;
                }

                @Nullable
                @Override
                public <T> T convert(@Nullable Object from, Type to) {
                    return proxied.convert(from, to, BeanOperatorImpl.this);
                }

                @Nullable
                @Override
                public <T> T convert(@Nullable Object from, Type to, BeanOperator beanOperator) {
                    return proxied.convert(from, to, beanOperator);
                }
            }
        }
    }

    interface SetPropertyAction {

        SetPropertyAction COMMON =
                (sourcePropertyName, sourcePropertyValue, destPropertyType, destPropertySetter, beanOperator) -> {
                    Object destPropertyValue = beanOperator.convert(sourcePropertyValue, destPropertyType);
                    destPropertySetter.set(destPropertyValue);
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
                Object sourcePropertyName, Object sourcePropertyValue,
                Type destPropertyType, PropertySetter destPropertySetter,
                BeanOperator beanOperator
        );
    }

    interface PropertySetter {
        void set(Object value);
    }
}
