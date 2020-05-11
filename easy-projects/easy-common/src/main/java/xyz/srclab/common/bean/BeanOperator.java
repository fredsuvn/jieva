package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.MapHelper;
import xyz.srclab.common.pattern.builder.CachedBuilder;
import xyz.srclab.common.reflect.ConstructorHelper;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Method;
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
        copyProperties(source, dest, CopyPropertiesFunction.DEFAULT);
    }

    default void copyPropertiesIgnoreNull(Object source, Object dest) {
        copyProperties(source, dest, CopyPropertiesFunction.DEFAULT_IGNORE_NULL);
    }

    default void copyProperties(Object source, Object dest, CopyPropertiesFunction copyPropertiesFunction) {
        if (source instanceof Map && dest instanceof Map) {
            Map src = (Map) source;
            Map des = (Map) dest;
            src.forEach((k, v) -> {
                if (k == null) {
                    return;
                }
                BeanProperty kProperty = BeanSupport.newMapProperty(k);
                copyPropertiesFunction.apply(src, kProperty, des, kProperty, this);
            });
        } else if (source instanceof Map) {
            Map src = (Map) source;
            BeanStruct destBean = resolveBean(dest.getClass());
            src.forEach((k, v) -> {
                if (k == null) {
                    return;
                }
                String propertyName = String.valueOf(k);
                if (!destBean.canWriteProperty(propertyName)) {
                    return;
                }
                BeanProperty sourceProperty = BeanSupport.newMapProperty(k);
                BeanProperty destProperty = getProperty(dest, propertyName);
                copyPropertiesFunction.apply(src, sourceProperty, dest, destProperty, this);
            });
        } else if (dest instanceof Map) {
            BeanStruct sourceBean = resolveBean(source.getClass());
            sourceBean.getReadableProperties().forEach((name, sourceProperty) -> {
                BeanProperty destProperty = BeanSupport.newMapProperty(name);
                copyPropertiesFunction.apply(source, sourceProperty, dest, destProperty, this);
            });
        } else {
            BeanStruct sourceBean = resolveBean(source.getClass());
            BeanStruct destBean = resolveBean(dest.getClass());
            sourceBean.getReadableProperties().forEach((name, sourceProperty) -> {
                if (!destBean.canWriteProperty(name)) {
                    return;
                }
                BeanProperty destProperty = getProperty(dest, name);
                copyPropertiesFunction.apply(source, sourceProperty, dest, destProperty, this);
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

    default <K, V> void populateProperties(
            Object source, Map<K, V> dest, PopulatePropertiesFunction<K, V> populatePropertiesFunction) {
        if (source instanceof Map) {
            Map<Object, Object> src = (Map<Object, Object>) source;
            src.forEach((k, v) -> {
                if (k == null) {
                    return;
                }
                populatePropertiesFunction.apply(k, v, dest::put, this);
            });
        } else {
            BeanStruct sourceBean = resolveBean(source.getClass());
            sourceBean.getReadableProperties().forEach((name, property) -> {
                @Nullable Object sourceValue = property.getValue(source);
                populatePropertiesFunction.apply(name, sourceValue, dest::put, this);
            });
        }
    }

    default <T> T clone(T from) {
        T returned = ConstructorHelper.newInstance(from.getClass());
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
            throw new BeanMethodNotFoundException(methodName);
        }
        return beanMethod;
    }

    default BeanMethod getMethod(Object bean, Method method)
            throws BeanMethodNotFoundException {
        @Nullable BeanMethod beanMethod = resolveBean(bean.getClass()).getMethod(method);
        if (beanMethod == null) {
            throw new BeanMethodNotFoundException(method.toGenericString());
        }
        return beanMethod;
    }

    interface CopyPropertiesFunction {

        CopyPropertiesFunction DEFAULT =
                (source, sourceProperty, dest, destProperty, beanOperator) -> {
                    @Nullable Object sourceValue = sourceProperty.getValue(source);
                    @Nullable Object destValue = sourceValue == null ? null :
                            beanOperator.convert(sourceValue, destProperty.getGenericType());
                    destProperty.setValue(dest, destValue);
                };

        CopyPropertiesFunction DEFAULT_IGNORE_NULL =
                (source, sourceProperty, dest, destProperty, beanOperator) -> {
                    @Nullable Object sourceValue = sourceProperty.getValue(source);
                    if (sourceValue == null) {
                        return;
                    }
                    @Nullable Object destValue = beanOperator.convert(sourceValue, destProperty.getGenericType());
                    destProperty.setValue(dest, destValue);
                };

        void apply(Object source, BeanProperty sourceProperty,
                   Object dest, BeanProperty destProperty,
                   BeanOperator beanOperator);
    }

    interface PopulatePropertiesFunction<K, V> {

        void apply(
                Object sourcePropertyName,
                @Nullable Object sourcePropertyValue,
                MapPropertySetter<K, V> mapPropertySetter,
                BeanOperator beanOperator
        );
    }

    interface MapPropertySetter<K, V> {
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
