package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.map.MapHelper;
import xyz.srclab.common.pattern.builder.CachedBuilder;
import xyz.srclab.common.reflect.SignatureHelper;
import xyz.srclab.common.reflect.instance.InstanceHelper;
import xyz.srclab.common.reflect.type.TypeRef;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

@Immutable
public interface BeanOperator {

    BeanOperator DEFAULT = BeanSupport.getBeanOperator();

    static Builder newBuilder() {
        return new Builder();
    }

    BeanResolver getBeanResolver();

    BeanConverter getBeanConverter();

    default BeanStruct resolveBean(Class<?> beanClass) {
        return getBeanResolver().resolve(beanClass);
    }

    default BeanProperty getProperty(Object bean, String propertyName) throws BeanPropertyNotFoundException {
        BeanStruct beanStruct = resolveBean(bean.getClass());
        @Nullable BeanProperty beanProperty = beanStruct.getProperty(propertyName);
        if (beanProperty == null) {
            throw new BeanPropertyNotFoundException(propertyName);
        }
        return beanProperty;
    }

    @Nullable
    default Object getPropertyValue(Object bean, String propertyName)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        return getProperty(bean, propertyName).getValue(bean);
    }

    @Nullable
    default <T> T getPropertyValue(Object bean, String propertyName, Type type)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        @Nullable Object value = getPropertyValue(bean, propertyName);
        if (value == null) {
            return null;
        }
        return convert(value, type);
    }

    @Nullable
    default <T> T getPropertyValue(Object bean, String propertyName, Class<T> type)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        return getPropertyValue(bean, propertyName, (Type) type);
    }

    @Nullable
    default <T> T getPropertyValue(Object bean, String propertyName, TypeRef<T> type)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        return getPropertyValue(bean, propertyName, type.getType());
    }

    default void setPropertyValue(Object bean, String propertyName, @Nullable Object value)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        getProperty(bean, propertyName).setValue(bean, value);
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
            BeanStruct destBean = resolveBean(dest.getClass());
            src.forEach((k, v) -> {
                String propertyName = String.valueOf(k);
                if (!destBean.canWriteProperty(propertyName)) {
                    return;
                }
                BeanProperty destProperty = getProperty(dest, propertyName);
                eachProperty.apply(
                        k,
                        v,
                        destProperty.getGenericType(),
                        value -> destProperty.setValue(dest, value),
                        this
                );
            });
        } else if (dest instanceof Map) {
            BeanStruct sourceBean = resolveBean(source.getClass());
            Map des = (Map) dest;
            sourceBean.getReadableProperties().forEach((name, property) -> {
                @Nullable Object sourceValue = property.getValue(source);
                eachProperty.apply(
                        name, sourceValue, Object.class, value -> des.put(name, value), this);
            });
        } else {
            BeanStruct sourceBean = resolveBean(source.getClass());
            BeanStruct destBean = resolveBean(dest.getClass());
            sourceBean.getReadableProperties().forEach((name, sourceProperty) -> {
                if (!destBean.canWriteProperty(name)) {
                    return;
                }
                BeanProperty destProperty = getProperty(dest, name);
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
            BeanStruct sourceBean = resolveBean(source.getClass());
            sourceBean.getReadableProperties().forEach((name, property) -> {
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
        return (T) getBeanConverter().convert(from, to, this);
    }

    default <T> T convert(Object from, Class<T> to) {
        return (T) getBeanConverter().convert(from, (Type) to, this);
    }

    default <T> T convert(Object from, TypeRef<T> to) {
        return (T) getBeanConverter().convert(from, to.getType(), this);
    }

    @Immutable
    default Map<String, Object> toMap(Object bean) {
        Map<String, Object> result = new LinkedHashMap<>();
        resolveBean(bean.getClass()).getReadableProperties().forEach((name, property) -> {
            @Nullable Object value = property.getValue(bean);
            result.put(name, value);
        });
        return MapHelper.immutable(result);
    }

    default BeanMethod getMethod(Object bean, String methodName, Class<?>... parameterTypes)
            throws BeanMethodNotFoundException {
        @Nullable BeanMethod beanMethod = resolveBean(bean.getClass()).getMethod(methodName, parameterTypes);
        if (beanMethod == null) {
            throw new BeanMethodNotFoundException(SignatureHelper.signMethod(methodName, parameterTypes));
        }
        return beanMethod;
    }

    default BeanMethod getMethodBySignature(Object bean, String methodSignature)
            throws BeanMethodNotFoundException {
        @Nullable BeanMethod beanMethod = resolveBean(bean.getClass()).getMethodBySignature(methodSignature);
        if (beanMethod == null) {
            throw new BeanMethodNotFoundException(methodSignature);
        }
        return beanMethod;
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

    final class Builder extends CachedBuilder<BeanOperator> {

        private @Nullable BeanResolver beanResolver;
        private @Nullable BeanConverter beanConverter;

        public Builder setBeanResolver(BeanResolver beanResolver) {
            this.beanResolver = beanResolver;
            this.updateState();
            return this;
        }

        public Builder setBeanConverter(BeanConverter beanConverter) {
            this.beanConverter = beanConverter;
            this.updateState();
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
                        BeanResolver.DEFAULT : builder.beanResolver;
                this.beanConverter = builder.beanConverter == null ?
                        BeanConverter.DEFAULT : builder.beanConverter;
            }

            @Override
            public BeanResolver getBeanResolver() {
                return beanResolver;
            }

            @Override
            public BeanConverter getBeanConverter() {
                return beanConverter;
            }
        }
    }
}
