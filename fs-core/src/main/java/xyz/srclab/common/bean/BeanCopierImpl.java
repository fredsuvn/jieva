package xyz.srclab.common.bean;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.convert.FsConvertException;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.reflect.FsType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

final class BeanCopierImpl implements FsBeanCopier {

    static final BeanCopierImpl INSTANCE = new BeanCopierImpl();

    @Override
    public <T> T copyProperties(Object source, Type sourceType, T dest, Type destType, Options options) {
        copyProperties0(source, sourceType, dest, destType, options);
        return dest;
    }

    private void copyProperties0(Object source, Type sourceType, Object dest, Type destType, Options options) {
        if (source instanceof Map) {
            ParameterizedType sourceMapType = FsType.getGenericSuperType(sourceType, Map.class);
            if (sourceMapType == null) {
                throw new IllegalArgumentException("Not a map type: " + sourceType + ".");
            }
            Type[] sourceActualTypes = sourceMapType.getActualTypeArguments();
            Type sourceKeyType = sourceActualTypes[0];
            Type sourceValueType = sourceActualTypes[1];
            if (dest instanceof Map) {
                ParameterizedType destMapType = FsType.getGenericSuperType(destType, Map.class);
                if (destMapType == null) {
                    throw new IllegalArgumentException("Not a map type: " + destType + ".");
                }
                Type[] destActualTypes = destMapType.getActualTypeArguments();
                Type destKeyType = destActualTypes[0];
                Type destValueType = destActualTypes[1];
                copyProperties00(source, sourceType, sourceKeyType, sourceValueType, dest, destType, destKeyType, destValueType, options);
            } else {
                copyProperties00(source, sourceType, sourceKeyType, sourceValueType, dest, destType, null, null, options);
            }
        } else {
            if (dest instanceof Map) {
                ParameterizedType destMapType = FsType.getGenericSuperType(destType, Map.class);
                if (destMapType == null) {
                    throw new IllegalArgumentException("Not a map type: " + destType + ".");
                }
                Type[] destActualTypes = destMapType.getActualTypeArguments();
                Type destKeyType = destActualTypes[0];
                Type destValueType = destActualTypes[1];
                copyProperties00(source, sourceType, null, null, dest, destType, destKeyType, destValueType, options);
            } else {
                copyProperties00(source, sourceType, null, null, dest, destType, null, null, options);
            }
        }
    }

    private void copyProperties00(
        Object source, Type sourceType, Type sourceKeyType, Type sourceValueType,
        Object dest, Type destType, Type destKeyType, Type destValueType,
        Options options
    ) {
        Function<Object, Object> propertyNameMapper = options.propertyNameMapper() != null ?
            options.propertyNameMapper()
            :
            n -> n;
        BiPredicate<Object, @Nullable Object> sourcePropertyFilter = options.sourcePropertyFilter() != null ?
            options.sourcePropertyFilter()
            :
            (k, v) -> true;
        BiPredicate<Object, @Nullable Object> destPropertyFilter = options.destPropertyFilter() != null ?
            options.destPropertyFilter()
            :
            (k, v) -> true;
        if (source instanceof Map) {
            if (dest instanceof Map) {
                FsConverter converter = Fs.notNull(options.converter(), FsConverter.defaultConverter());
                Map<Object, Object> sourceMap = Fs.as(source);
                Map<Object, Object> destMap = Fs.as(dest);
                sourceMap.forEach((key, value) -> {
                    if (!sourcePropertyFilter.test(key, value)) {
                        return;
                    }
                    Object destKey = propertyNameMapper.apply(key);
                    if (destKey == null) {
                        return;
                    }
                    Object newDestKey = tryConvert(destKey, sourceKeyType, destKeyType, converter, options);
                    if (newDestKey == Fs.RETURN) {
                        return;
                    }
                    if (!options.putIfNotContained() && !destMap.containsKey(newDestKey)) {
                        return;
                    }
                    Object newDestValue = tryConvert(value, sourceValueType, destValueType, converter, options);
                    if (newDestValue == Fs.RETURN) {
                        return;
                    }
                    if (!destPropertyFilter.test(newDestKey, newDestValue)) {
                        return;
                    }
                    destMap.put(newDestKey, newDestValue);
                });
            } else {
                FsBeanResolver resolver = Fs.notNull(options.beanResolver(), FsBeanResolver.defaultResolver());
                FsConverter converter = Fs.notNull(options.converter(), FsConverter.defaultConverter());
                Map<Object, Object> sourceMap = Fs.as(source);
                FsBean destBean = resolver.resolve(destType);
                sourceMap.forEach((key, value) -> {
                    if (!sourcePropertyFilter.test(key, value)) {
                        return;
                    }
                    Object destNameObj = propertyNameMapper.apply(key);
                    if (destNameObj == null) {
                        return;
                    }
                    Object newDestNameObj = tryConvert(destNameObj, sourceKeyType, String.class, converter, options);
                    if (newDestNameObj == Fs.RETURN) {
                        return;
                    }
                    String newDestName = (String) newDestNameObj;
                    FsBeanProperty destProperty = destBean.getProperty(newDestName);
                    if (destProperty == null || !destProperty.isWriteable()) {
                        return;
                    }
                    Object newDestValue = tryConvert(value, sourceValueType, destProperty.getType(), converter, options);
                    if (newDestValue == Fs.RETURN) {
                        return;
                    }
                    if (!destPropertyFilter.test(newDestName, newDestValue)) {
                        return;
                    }
                    destProperty.set(dest, newDestValue);
                });
            }
        } else {
            FsBeanResolver resolver = Fs.notNull(options.beanResolver(), FsBeanResolver.defaultResolver());
            FsConverter converter = Fs.notNull(options.converter(), FsConverter.defaultConverter());
            FsBean sourceBean = resolver.resolve(sourceType);
            if (dest instanceof Map) {
                Map<Object, Object> destMap = Fs.as(dest);
                sourceBean.getProperties().forEach((sourceName, sourceProperty) -> {
                    if (!sourceProperty.isReadable()) {
                        return;
                    }
                    Object sourceValue = sourceProperty.get(source);
                    if (!sourcePropertyFilter.test(sourceName, sourceValue)) {
                        return;
                    }
                    Object destKey = propertyNameMapper.apply(sourceName);
                    if (destKey == null) {
                        return;
                    }
                    Object newDestKey = tryConvert(destKey, String.class, destKeyType, converter, options);
                    if (newDestKey == Fs.RETURN) {
                        return;
                    }
                    if (!options.putIfNotContained() && !destMap.containsKey(destKey)) {
                        return;
                    }
                    Object newDestValue = tryConvert(sourceValue, sourceProperty.getType(), destValueType, converter, options);
                    if (newDestValue == Fs.RETURN) {
                        return;
                    }
                    if (!destPropertyFilter.test(destKey, newDestValue)) {
                        return;
                    }
                    destMap.put(newDestKey, newDestValue);
                });
            } else {
                FsBean destBean = resolver.resolve(destType);
                sourceBean.getProperties().forEach((sourceName, sourceProperty) -> {
                    if (!sourceProperty.isReadable()) {
                        return;
                    }
                    Object sourceValue = sourceProperty.get(source);
                    if (!sourcePropertyFilter.test(sourceName, sourceValue)) {
                        return;
                    }
                    Object destNameObj = propertyNameMapper.apply(sourceName);
                    if (destNameObj == null) {
                        return;
                    }
                    Object newDestNameObj = tryConvert(destNameObj, String.class, String.class, converter, options);
                    if (newDestNameObj == Fs.RETURN) {
                        return;
                    }
                    String newDestName = (String) newDestNameObj;
                    FsBeanProperty destProperty = destBean.getProperty(newDestName);
                    if (destProperty == null || !destProperty.isWriteable()) {
                        return;
                    }
                    Object newDestValue = tryConvert(sourceValue, sourceProperty.getType(), destProperty.getType(), converter, options);
                    if (newDestValue == Fs.RETURN) {
                        return;
                    }
                    if (!destPropertyFilter.test(newDestName, newDestValue)) {
                        return;
                    }
                    destProperty.set(dest, newDestValue);
                });
            }
        }
    }

    private Object tryConvert(
        Object value, Type fromType, Type destType,
        FsConverter converter, Options options
    ) {
        Object newValue = converter.convertObject(value, fromType, destType);
        if (newValue == Fs.RETURN) {
            if (options.throwIfConvertFailed()) {
                throw new FsConvertException(fromType, destType);
            } else {
                return Fs.RETURN;
            }
        }
        return newValue;
    }
}
