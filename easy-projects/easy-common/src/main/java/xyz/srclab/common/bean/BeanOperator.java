package xyz.srclab.common.bean;

import org.apache.commons.beanutils.BeanUtils;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

@Immutable
public interface BeanOperator {

    static BeanOperator getDefault() {
        return BeanOperator0.getDefault();
    }

    static BeanOperatorBuilder newBuilder() {
        return BeanOperator0.newOperatorBuilder();
    }

    BeanResolver resolver();

    Converter converter();

    boolean canResolve(Object object);

    default BeanClass resolveBean(Class<?> beanClass) {
        return resolver().resolve(beanClass);
    }

    @Nullable
    default Object getValue(Object bean, String propertyName)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        BeanClass beanClass = resolveBean(bean.getClass());
        return beanClass.getPropertyValue(bean, propertyName);
    }

    @Nullable
    default <T> T getValue(Object bean, String propertyName, Type type)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        BeanClass beanClass = resolveBean(bean.getClass());
        return beanClass.getPropertyValue(bean, propertyName, type, converter());
    }

    @Nullable
    default <T> T getValue(Object bean, String propertyName, Class<T> type)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        BeanClass beanClass = resolveBean(bean.getClass());
        return beanClass.getPropertyValue(bean, propertyName, type, converter());
    }

    @Nullable
    default <T> T getValue(Object bean, String propertyName, TypeRef<T> type)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        BeanClass beanClass = resolveBean(bean.getClass());
        return beanClass.getPropertyValue(bean, propertyName, type, converter());
    }

    default void setPropertyValue(Object bean, String propertyName, @Nullable Object value)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        BeanClass beanClass = resolveBean(bean.getClass());
        beanClass.setPropertyValue(bean, propertyName, value, converter());
    }

    @Immutable
    default Map<String, Object> getPropertiesValues(Object bean, String... propertyNames) {
        return resolveBean(bean.getClass()).getPropertiesValues(bean, propertyNames);
    }

    @Immutable
    default Map<String, Object> getPropertiesValues(Object bean, Iterable<String> propertyNames) {
        return resolveBean(bean.getClass()).getPropertiesValues(bean, propertyNames);
    }

    default void setPropertiesValues(Object bean, Map<String, Object> properties) {
        resolveBean(bean.getClass()).setPropertiesValues(bean, properties, converter());
    }

    default void setPropertiesValues(
            Object bean, Iterable<String> propertyNames, Function<String, Object> function) {
        resolveBean(bean.getClass()).setPropertiesValues(bean, propertyNames, function);
    }

    default void clearPropertiesValues(Object bean) {
        resolveBean(bean.getClass()).clearPropertiesValues(bean);
    }

    default Map<String, Object> asMap(Object bean) {
        return resolveBean(bean.getClass()).asMap(bean);
    }

    default Map<String, Object> asReadableMap(Object bean) {
        return resolveBean(bean.getClass()).asReadableMap(bean);
    }

    default Map<String, Object> asWriteableMap(Object bean) {
        return resolveBean(bean.getClass()).asWriteableMap(bean);
    }

    @Immutable
    default Map<String, Object> toMap(Object beanOrMap) {
        return resolveBean(beanOrMap.getClass()).toMap(beanOrMap);
    }

    default <T> T toBean(Class<T> type, Map<String, Object> properties) {
        return Cast.as(resolveBean(type).toBean(properties, converter()));
    }

    default <T> T toBean(Class<T> type, Iterable<String> propertyNames, Function<String, Object> function) {
        return Cast.as(resolveBean(type).toBean(propertyNames, function));
    }

    default <T> T duplicate(T from) {
        return Cast.as(resolveBean(from.getClass()).duplicate(from));
    }

    default <T> T convert(Object from, Type to) {
        return converter().convert(from, to);
    }

    default <T> T convert(Object from, Class<T> to) {
        return converter().convert(from, to);
    }

    default <T> T convert(Object from, TypeRef<T> to) {
        return converter().convert(from, to);
    }

    default void copyProperties(Object source, Object dest) {

    }

    default void copyPropertiesIgnoreNull(Object source, Object dest) {
        prepareCopyProperties(source, dest)
                .filter(CopyPreparation.ignoreNullFilter)
                .doCopy();
    }

    default CopyPreparation prepareCopyProperties(Object source, Object dest) {
        return new CopyPreparation(this, source, dest);
    }

    default void populateProperties(Object source, Map<?, ?> dest) {
        Map<?, ?> sourceMap = toRawMap(source);
        BeanUtils.pu
        dest.putAll(sourceMap);
    }

    default <K, V> void populatePropertiesIgnoreNull(Object source, Map<K, V> dest) {
        preparePopulateProperties(source, dest)
                .filter(PopulatePreparation.ignoreNullFilter)
                .doPopulate();
    }

    default <K, V> PopulatePreparation<K, V> preparePopulateProperties(Object source, Map<K, V> dest) {
        return new PopulatePreparation<>(this, source, dest);
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
                    BeanProperty destProperty = destClass.property(destPropertyName);
                    destProperty.setValue(dest, beanOperator.convert(value, destProperty.genericType()));
                });
            } else if (dest instanceof Map) {
                BeanClass sourceClass = beanOperator.resolveBean(source.getClass());
                Map destMap = (Map) dest;
                sourceClass.readableProperties().forEach((name, property) -> {
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
                sourceClass.readableProperties().forEach((name, sourceProperty) -> {
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
                    BeanProperty destProperty = destClass.property(destPropertyName);
                    destProperty.setValue(dest, beanOperator.convert(value, destProperty.genericType()));
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
                sourceClass.readableProperties().forEach((name, property) -> {
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


}
