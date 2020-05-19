package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.pattern.builder.CachedBuilder;
import xyz.srclab.common.reflect.ClassHelper;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

@Immutable
public interface BeanOperator {

    BeanOperator DEFAULT = BeanSupport.getBeanOperator();

    static Builder newBuilder() {
        return new Builder();
    }

    BeanResolver getBeanResolver();

    BeanConverter getBeanConverter();

    default BeanClass resolveBean(Class<?> beanClass) {
        return getBeanResolver().resolve(beanClass);
    }

    @Nullable
    default BeanProperty getProperty(Object bean, String propertyName) {
        BeanClass beanClass = resolveBean(bean.getClass());
        return beanClass.getProperty(propertyName);
    }

    @Nullable
    default Object getPropertyValue(Object bean, String propertyName)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        BeanClass beanClass = resolveBean(bean.getClass());
        return beanClass.getPropertyValue(bean, propertyName);
    }

    @Nullable
    default <T> T getPropertyValue(Object bean, String propertyName, Type type)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        BeanClass beanClass = resolveBean(bean.getClass());
        return beanClass.getPropertyValue(bean, propertyName, type, getBeanConverter());
    }

    @Nullable
    default <T> T getPropertyValue(Object bean, String propertyName, Class<T> type)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        BeanClass beanClass = resolveBean(bean.getClass());
        return beanClass.getPropertyValue(bean, propertyName, type, getBeanConverter());
    }

    @Nullable
    default <T> T getPropertyValue(Object bean, String propertyName, TypeRef<T> type)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        BeanClass beanClass = resolveBean(bean.getClass());
        return beanClass.getPropertyValue(bean, propertyName, type, getBeanConverter());
    }

    default void setPropertyValue(Object bean, String propertyName, @Nullable Object value)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        BeanClass beanClass = resolveBean(bean.getClass());
        beanClass.setPropertyValue(bean, propertyName, value, getBeanConverter());
    }

    default void copyProperties(Object source, Object dest) {
        prepareCopyProperties(source, dest).doCopy();
    }

    default void copyPropertiesIgnoreNull(Object source, Object dest) {
        prepareCopyProperties(source, dest)
                .filter(CopyPreparation.ignoreNullFilter)
                .doCopy();
    }

    default CopyPreparation prepareCopyProperties(Object source, Object dest) {
        return new CopyPreparation(this, source, dest);
    }

    default <K, V> void populateProperties(Object source, Map<K, V> dest) {
        preparePopulateProperties(source, dest).doPopulate();
    }

    default <K, V> void populatePropertiesIgnoreNull(Object source, Map<K, V> dest) {
        preparePopulateProperties(source, dest)
                .filter(PopulatePreparation.ignoreNullFilter)
                .doPopulate();
    }

    default <K, V> PopulatePreparation<K, V> preparePopulateProperties(Object source, Map<K, V> dest) {
        return new PopulatePreparation<>(this, source, dest);
    }

    default <T> T clone(T from) {
        T returned = ClassHelper.newInstance(from.getClass());
        copyProperties(from, returned);
        return returned;
    }

    default <T> T convert(Object from, Type to) {
        return getBeanConverter().convert(from, to, this);
    }

    default <T> T convert(Object from, Class<T> to) {
        return getBeanConverter().convert(from, to, this);
    }

    default <T> T convert(Object from, TypeRef<T> to) {
        return getBeanConverter().convert(from, to, this);
    }

    @Immutable
    default Map<String, Object> toMap(Object bean) {
        return resolveBean(bean.getClass()).toMap(bean);
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

    final class CopyPreparation {

        private static final BiPredicate<Object, Object> nopFilter = (name, value) -> true;
        private static final BiPredicate<Object, Object> ignoreNullFilter = (name, value) -> value != null;

        private static final Function<Object, Object> nopNameMapper = name -> name;

        private final BeanOperator beanOperator;
        private final Object source;
        private final Object dest;

        private BiPredicate<Object, Object> filter = nopFilter;
        private Function<Object, Object> nameMapper = nopNameMapper;

        public CopyPreparation(BeanOperator beanOperator, Object source, Object dest) {
            this.beanOperator = beanOperator;
            this.source = source;
            this.dest = dest;
        }

        public CopyPreparation filter(BiPredicate<Object, Object> filter) {
            this.filter = filter;
            return this;
        }

        public CopyPreparation mapName(Function<Object, Object> nameMapper) {
            this.nameMapper = nameMapper;
            return this;
        }

        public void doCopy() {
            if (source instanceof Map && dest instanceof Map) {
                Map sourceMap = (Map) source;
                Map destMap = (Map) dest;
                sourceMap.forEach((key, value) -> {
                    if (key == null || !filter.test(key, value)) {
                        return;
                    }
                    Object newKey = nameMapper.apply(key);
                    if (!destMap.containsKey(newKey)) {
                        return;
                    }
                    destMap.put(newKey, value);
                });
            } else if (source instanceof Map) {
                Map sourceMap = (Map) source;
                BeanClass destClass = beanOperator.resolveBean(dest.getClass());
                sourceMap.forEach((key, value) -> {
                    if (key == null || !filter.test(key, value)) {
                        return;
                    }
                    String destPropertyName = nameMapper.apply(key).toString();
                    if (!destClass.canWriteProperty(destPropertyName)) {
                        return;
                    }
                    BeanProperty destProperty = destClass.getProperty(destPropertyName);
                    destProperty.setValue(dest, beanOperator.convert(value, destProperty.getGenericType()));
                });
            } else if (dest instanceof Map) {
                BeanClass sourceClass = beanOperator.resolveBean(source.getClass());
                Map destMap = (Map) dest;
                sourceClass.getReadableProperties().forEach((name, property) -> {
                    @Nullable Object value = property.getValue(source);
                    if (!filter.test(name, value)) {
                        return;
                    }
                    Object newKey = nameMapper.apply(name);
                    if (!destMap.containsKey(newKey)) {
                        return;
                    }
                    destMap.put(newKey, value);
                });
            } else {
                BeanClass sourceClass = beanOperator.resolveBean(source.getClass());
                BeanClass destClass = beanOperator.resolveBean(dest.getClass());
                sourceClass.getReadableProperties().forEach((name, sourceProperty) -> {
                    if (!destClass.canWriteProperty(name)) {
                        return;
                    }
                    @Nullable Object value = sourceProperty.getValue(source);
                    if (!filter.test(name, value)) {
                        return;
                    }
                    String destPropertyName = nameMapper.apply(name).toString();
                    if (!destClass.canWriteProperty(destPropertyName)) {
                        return;
                    }
                    BeanProperty destProperty = destClass.getProperty(destPropertyName);
                    destProperty.setValue(dest, beanOperator.convert(value, destProperty.getGenericType()));
                });
            }
        }
    }

    final class PopulatePreparation<K, V> {

        private static final BiPredicate<Object, Object> nopFilter = (name, value) -> true;
        private static final BiPredicate<Object, Object> ignoreNullFilter = (name, value) -> value != null;

        private static final Function<Object, Object> emptyMapper = o -> o;

        private final BeanOperator beanOperator;
        private final Object source;
        private final Map<K, V> dest;

        private BiPredicate<Object, Object> filter = nopFilter;
        private Function<Object, K> keyMapper = (Function<Object, K>) emptyMapper;
        private Function<Object, V> valueMapper = (Function<Object, V>) emptyMapper;

        public PopulatePreparation(BeanOperator beanOperator, Object source, Map<K, V> dest) {
            this.beanOperator = beanOperator;
            this.source = source;
            this.dest = dest;
        }

        public PopulatePreparation<K, V> filter(BiPredicate<Object, Object> filter) {
            this.filter = filter;
            return this;
        }

        public PopulatePreparation<K, V> mapKey(Function<Object, K> keyMapper) {
            this.keyMapper = keyMapper;
            return this;
        }

        public PopulatePreparation<K, V> mapValue(Function<Object, V> valueMapper) {
            this.valueMapper = valueMapper;
            return this;
        }

        public void doPopulate() {
            if (source instanceof Map) {
                Map sourceMap = (Map) source;
                sourceMap.forEach((key, value) -> {
                    if (key == null || !filter.test(key, value)) {
                        return;
                    }
                    K newKey = keyMapper.apply(key);
                    @Nullable V newValue = valueMapper.apply(value);
                    dest.put(newKey, newValue);
                });
            } else {
                BeanClass sourceClass = beanOperator.resolveBean(source.getClass());
                sourceClass.getReadableProperties().forEach((name, property) -> {
                    @Nullable Object sourceValue = property.getValue(source);
                    if (!filter.test(name, sourceValue)) {
                        return;
                    }
                    K newKey = keyMapper.apply(name);
                    @Nullable V newValue = valueMapper.apply(sourceValue);
                    dest.put(newKey, newValue);
                });
            }
        }
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
