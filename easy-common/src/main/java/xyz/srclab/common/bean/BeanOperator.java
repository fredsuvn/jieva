package xyz.srclab.common.bean;

import com.sun.javafx.fxml.PropertyNotFoundException;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.builder.CacheStateBuilder;
import xyz.srclab.common.collection.map.MapHelper;
import xyz.srclab.common.lang.TypeRef;
import xyz.srclab.common.reflect.instance.InstanceHelper;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

@Immutable
public interface BeanOperator {

    BeanOperator DEFAULT = new DefaultBeanOperator();

    static Builder newBuilder() {
        return new Builder();
    }

    BeanResolver getBeanResolver();

    BeanConverter getBeanConverter();

    default BeanClass resolve(Class<?> beanClass) {
        return getBeanResolver().resolve(beanClass);
    }

    default boolean containsProperty(Object bean, String propertyName) {
        BeanClass beanClass = resolve(bean.getClass());
        return beanClass.containsProperty(propertyName);
    }

    @Nullable
    default Object getProperty(Object bean, String propertyName)
            throws PropertyNotFoundException, UnsupportedOperationException {
        BeanClass beanClass = resolve(bean.getClass());
        BeanProperty beanProperty = beanClass.getProperty(propertyName);
        return beanProperty.getValue(bean);
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
        BeanClass beanClass = resolve(bean.getClass());
        BeanProperty beanProperty = beanClass.getProperty(propertyName);
        beanProperty.setValue(bean, value == null ?
                null : convert(value, beanProperty.getGenericType()));
    }

    default void copyProperties(Object source, Object dest) {
        copyProperties(source, dest, EachProperty.DEFAULT);
    }

    default void copyPropertiesIgnoreNull(Object source, Object dest) {
        copyProperties(source, dest, EachProperty.DEFAULT_IGNORE_NULL);
    }

    default void copyProperties(Object source, Object dest, EachProperty eachProperty) {
        if (source instanceof Map && dest instanceof Map) {
            Map src = (Map) source;
            Map des = (Map) dest;
            src.forEach((k, v) -> {
                if (k == null) {
                    return;
                }
                eachProperty.apply(k, v, Object.class, value -> des.put(k, value), this);
            });
        } else if (source instanceof Map) {
            Map src = (Map) source;
            BeanClass destBean = resolve(dest.getClass());
            src.forEach((k, v) -> {
                String propertyName = String.valueOf(k);
                if (!destBean.canWriteProperty(propertyName)) {
                    return;
                }
                BeanProperty destProperty = destBean.getProperty(propertyName);
                eachProperty.apply(
                        k,
                        v,
                        destProperty.getGenericType(),
                        value -> destProperty.setValue(dest, value),
                        this
                );
            });
        } else if (dest instanceof Map) {
            BeanClass sourceBean = resolve(source.getClass());
            Map des = (Map) dest;
            sourceBean.getAllProperties().forEach((name, property) -> {
                if (!property.isReadable()) {
                    return;
                }
                @Nullable Object sourceValue = property.getValue(source);
                eachProperty.apply(
                        name, sourceValue, Object.class, value -> des.put(name, value), this);
            });
        } else {
            BeanClass sourceBean = resolve(source.getClass());
            BeanClass destBean = resolve(dest.getClass());
            sourceBean.getAllProperties().forEach((name, sourceProperty) -> {
                if (!sourceProperty.isReadable()
                        || !destBean.canWriteProperty(name)) {
                    return;
                }
                BeanProperty destProperty = destBean.getProperty(name);
                @Nullable Object sourceValue = sourceProperty.getValue(source);
                eachProperty.apply(
                        name,
                        sourceValue,
                        destProperty.getGenericType(),
                        value -> destProperty.setValue(dest, value),
                        this
                );
            });
        }
    }

    default <K, V> void populateProperties(Object source, Map<K, V> dest) {
        populateProperties(
                source, dest,
                (name, value, setter, beanOperator) -> setter.set((K) name, (V) value));
    }

    default <K, V> void populatePropertiesIgnoreNull(Object source, Map<K, V> dest) {
        populateProperties(
                source, dest,
                (name, value, setter, beanOperator) -> {
                    if (value != null) {
                        setter.set((K) name, (V) value);
                    }
                });
    }

    default <K, V> void populateProperties(Object source, Map<K, V> dest, EachEntry<K, V> eachEntry) {
        if (source instanceof Map) {
            Map<Object, Object> src = (Map<Object, Object>) source;
            src.forEach((k, v) -> {
                if (k == null) {
                    return;
                }
                eachEntry.apply(k, v, dest::put, this);
            });
        } else {
            BeanClass sourceBean = resolve(source.getClass());
            sourceBean.getAllProperties().forEach((name, property) -> {
                if (!property.isReadable()) {
                    return;
                }
                @Nullable Object sourceValue = property.getValue(source);
                eachEntry.apply(name, sourceValue, dest::put, this);
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

    @Immutable
    default Map<String, Object> toMap(Object bean) {
        Map<String, Object> result = new LinkedHashMap<>();
        resolve(bean.getClass()).getAllProperties().forEach((name, property) -> {
            if (!property.isReadable()) {
                return;
            }
            @Nullable Object value = property.getValue(bean);
            result.put(name, value);
        });
        return MapHelper.immutable(result);
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

        @Override
        public BeanOperator build() {
            return super.build();
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

    interface EachProperty {

        EachProperty DEFAULT =
                (sourcePropertyName, sourcePropertyValue, destPropertyType, destPropertySetter, beanOperator) ->
                        destPropertySetter.set(sourcePropertyValue == null ?
                                null : beanOperator.convert(sourcePropertyValue, destPropertyType));

        EachProperty DEFAULT_IGNORE_NULL =
                (sourcePropertyName, sourcePropertyValue, destPropertyType, destPropertySetter, beanOperator) -> {
                    if (sourcePropertyValue == null) {
                        return;
                    }
                    Object destPropertyValue = beanOperator.convert(sourcePropertyValue, destPropertyType);
                    destPropertySetter.set(destPropertyValue);
                };

        void apply(
                Object sourcePropertyName,
                @Nullable Object sourcePropertyValue,
                Type destPropertyType,
                PropertySetter destPropertySetter,
                BeanOperator beanOperator
        );
    }

    interface PropertySetter {
        void set(@Nullable Object value);
    }

    interface EachEntry<K, V> {

        void apply(
                Object sourcePropertyName,
                @Nullable Object sourcePropertyValue,
                KeyValueSetter<K, V> keyValueSetter,
                BeanOperator beanOperator
        );
    }

    interface KeyValueSetter<K, V> {
        void set(K key, @Nullable V value);
    }
}
