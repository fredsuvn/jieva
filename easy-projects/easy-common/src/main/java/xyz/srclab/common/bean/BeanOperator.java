package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.reflect.ClassKit;
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

    default BeanClass resolveBean(Class<?> beanClass) {
        return resolver().resolve(beanClass);
    }

    @Nullable
    default BeanProperty getProperty(Object bean, String propertyName) {
        BeanClass beanClass = resolveBean(bean.getClass());
        return beanClass.property(propertyName);
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
        return beanClass.getPropertyValue(bean, propertyName, type, converter());
    }

    @Nullable
    default <T> T getPropertyValue(Object bean, String propertyName, Class<T> type)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        BeanClass beanClass = resolveBean(bean.getClass());
        return beanClass.getPropertyValue(bean, propertyName, type, converter());
    }

    @Nullable
    default <T> T getPropertyValue(Object bean, String propertyName, TypeRef<T> type)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        BeanClass beanClass = resolveBean(bean.getClass());
        return beanClass.getPropertyValue(bean, propertyName, type, converter());
    }

    default void setPropertyValue(Object bean, String propertyName, @Nullable Object value)
            throws BeanPropertyNotFoundException, UnsupportedOperationException {
        BeanClass beanClass = resolveBean(bean.getClass());
        beanClass.setPropertyValue(bean, propertyName, value, converter());
    }

    default void copyProperties(Object source, Object dest) {
        Map<Object, Object> sourceMap = toRawMap(source);
        toBean(sourceMap, dest);
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
        T returned = ClassKit.newInstance(from.getClass());
        copyProperties(from, returned);
        return returned;
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

    @Immutable
    default Map<String, Object> toMap(Object bean) {
        return resolveBean(bean.getClass()).toMap(bean);
    }

    @Immutable
    default Map<String, Object> deepToMap(Object bean) {
        return resolveBean(bean.getClass()).deepToMap(bean);
    }

    @Immutable
    default Map<String, Object> deepToMap(Object bean, Function<Object, @Nullable Object> resolver) {
        return resolveBean(bean.getClass()).deepToMap(bean, resolver);
    }

    default <T, K, V> T toBean(Map<K, V> map, Class<T> beanType) {
        return resolveBean(beanType).toBean(map, converter());
    }

    default <T, K, V> void toBean(Map<K, V> map, T bean) {
        resolveBean(bean.getClass()).toBean(map, bean, converter());
    }

    default Map<Object, Object> toRawMap(Object beanOrMap) {
        if (beanOrMap instanceof Map) {
            return Cast.as(beanOrMap);
        }
        return Cast.as(toMap(beanOrMap));
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
