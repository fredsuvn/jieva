package xyz.fsgek.common.bean;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.Gek;
import xyz.fsgek.common.collect.GekArray;
import xyz.fsgek.common.convert.GekConverter;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

final class BeanCopierImpl implements GekBeanCopier {

    @Override
    public <T> T copyProperties(Object source, Type sourceType, T dest, Type destType, Option... options) {
        GekBeanResolver resolver = null;
        GekConverter converter = null;
        boolean throwIfConversionFailed = true;
        boolean putIfNotContained = true;
        Object ignoreProperties = null;
        boolean ignoreNull = false;
        if (GekArray.isNotEmpty(options)) {
            for (Option option : options) {
                if (Objects.equals(option.key(), CopyOptions.KEY_OF_BEAN_RESOLVER)) {
                    resolver = Gek.as(option.value());
                } else if (Objects.equals(option.key(), CopyOptions.KEY_OF_CONVERTER)) {
                    converter = Gek.as(option.value());
                } else if (Objects.equals(option.key(), CopyOptions.KEY_OF_THROW_IF_CONVERT_FAILED)) {
                    throwIfConversionFailed = Gek.as(option.value());
                } else if (Objects.equals(option.key(), CopyOptions.KEY_OF_PUT_IF_NOT_CONTAINED)) {
                    putIfNotContained = Gek.as(option.value());
                } else if (Objects.equals(option.key(), CopyOptions.KEY_OF_IGNORE_PROPERTIES)) {
                    ignoreProperties = option.value();
                } else if (Objects.equals(option.key(), CopyOptions.KEY_OF_IGNORE_NULL)) {
                    ignoreNull = Gek.as(option.value());
                }
            }
        }
        return copyProperties0(
            source, sourceType, dest, destType,
            Gek.notNull(resolver, GekBeanResolver.defaultResolver()),
            Gek.notNull(converter, GekConverter.defaultConverter()),
            throwIfConversionFailed,
            putIfNotContained,
            ignoreProperties,
            ignoreNull
        );
    }

    private <T> T copyProperties0(
        Object source, Type sourceType, T dest, Type destType,
        GekBeanResolver resolver,
        GekConverter converter,
        boolean throwIfConversionFailed,
        boolean putIfNotContained,
        @Nullable Object ignoreProperties,
        boolean ignoreNull
    ) {
        if (source instanceof Map) {
            if (dest instanceof Map) {
                Map<Object, Object> sourceMap = Gek.as(source);
                Map<Object, Object> destMap = Gek.as(dest);
                sourceMap.forEach((key, value) -> {
                    if (!(key instanceof String)) {
                        throw new IllegalArgumentException("Key of source map is not String.");
                    }
                    String sourcePropertyName = String.valueOf(key);
                    if (filterProperty(sourcePropertyName, ignoreProperties)) {
                        return;
                    }
                    if (!putIfNotContained && !destMap.containsKey(sourcePropertyName)) {
                        return;
                    }
                    Object newDestValue = tryConvert(value, sourceValueType, destValueType, converter);
                    if (newDestValue == null) {
                        return;
                    } else {
                        newDestValue = GekConverter.getResult(newDestValue);
                    }
                    if (!destFilter.test(newDestKey, newDestValue)) {
                        return;
                    }
                    destMap.put(newDestKey, newDestValue);
                });
            } else {
                GekBeanResolver resolver =
                    this.beanResolver == null ? GekBeanResolver.defaultResolver() : this.beanResolver;
                Map<Object, Object> sourceMap = Gek.as(source);
                GekBean destBean = resolver.resolve(destType);
                sourceMap.forEach((key, value) -> {
                    if (!sourceFilter.test(key, value)) {
                        return;
                    }
                    Object destNameObj = nameMapper.apply(key);
                    if (destNameObj == null) {
                        return;
                    }
                    Object newDestNameObj = tryConvert(destNameObj, sourceKeyType, String.class, converter);
                    if (newDestNameObj == null) {
                        return;
                    } else {
                        newDestNameObj = GekConverter.getResult(newDestNameObj);
                    }
                    String newDestName = (String) newDestNameObj;
                    GekProperty destProperty = destBean.getProperty(newDestName);
                    if (destProperty == null || !destProperty.isWriteable()) {
                        return;
                    }
                    Object newDestValue = tryConvert(value, sourceValueType, destProperty.getType(), converter);
                    if (newDestValue == null) {
                        return;
                    } else {
                        newDestValue = GekConverter.getResult(newDestValue);
                    }
                    if (!destFilter.test(newDestName, newDestValue)) {
                        return;
                    }
                    destProperty.set(dest, newDestValue);
                });
            }
        } else {
            GekBeanResolver resolver =
                this.beanResolver == null ? GekBeanResolver.defaultResolver() : this.beanResolver;
            GekBean sourceBean = resolver.resolve(sourceType);
            if (dest instanceof Map) {
                Map<Object, Object> destMap = Gek.as(dest);
                sourceBean.getProperties().forEach((sourceName, sourceProperty) -> {
                    if (!sourceProperty.isReadable()) {
                        return;
                    }
                    Object sourceValue = sourceProperty.get(source);
                    if (!sourceFilter.test(sourceName, sourceValue)) {
                        return;
                    }
                    Object destKey = nameMapper.apply(sourceName);
                    if (destKey == null) {
                        return;
                    }
                    Object newDestKey = tryConvert(destKey, String.class, destKeyType, converter);
                    if (newDestKey == null) {
                        return;
                    } else {
                        newDestKey = GekConverter.getResult(newDestKey);
                    }
                    if (!putIfNotContained && !destMap.containsKey(destKey)) {
                        return;
                    }
                    Object newDestValue = tryConvert(sourceValue, sourceProperty.getType(), destValueType, converter);
                    if (newDestValue == null) {
                        return;
                    } else {
                        newDestValue = GekConverter.getResult(newDestValue);
                    }
                    if (!destFilter.test(destKey, newDestValue)) {
                        return;
                    }
                    destMap.put(newDestKey, newDestValue);
                });
            } else {
                GekBean destBean = resolver.resolve(destType);
                sourceBean.getProperties().forEach((sourceName, sourceProperty) -> {
                    if (!sourceProperty.isReadable()) {
                        return;
                    }
                    Object sourceValue = sourceProperty.get(source);
                    if (!sourceFilter.test(sourceName, sourceValue)) {
                        return;
                    }
                    Object destNameObj = nameMapper.apply(sourceName);
                    if (destNameObj == null) {
                        return;
                    }
                    Object newDestNameObj = tryConvert(destNameObj, String.class, String.class, converter);
                    if (newDestNameObj == null) {
                        return;
                    } else {
                        newDestNameObj = GekConverter.getResult(newDestNameObj);
                    }
                    String newDestName = (String) newDestNameObj;
                    GekProperty destProperty = destBean.getProperty(newDestName);
                    if (destProperty == null || !destProperty.isWriteable()) {
                        return;
                    }
                    Object newDestValue = tryConvert(sourceValue, sourceProperty.getType(), destProperty.getType(), converter);
                    if (newDestValue == null) {
                        return;
                    } else {
                        newDestValue = GekConverter.getResult(newDestValue);
                    }
                    if (!destFilter.test(newDestName, newDestValue)) {
                        return;
                    }
                    destProperty.set(dest, newDestValue);
                });
            }
        }
    }

    private boolean filterProperty(String name, @Nullable Object ignoreProperties) {
        if (ignoreProperties == null) {
            return false;
        }
        if (ignoreProperties instanceof Object[]) {
            for (Object ignoreProperty : ((Object[]) ignoreProperties)) {
                if (Objects.equals(ignoreProperty, name)) {
                    return true;
                }
            }
            return false;
        }
        if (ignoreProperties instanceof Iterable) {
            for (Object o : (Iterable<?>) ignoreProperties) {
                if (Objects.equals(o, name)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public Builder toBuilder() {
        return null;
    }
}
