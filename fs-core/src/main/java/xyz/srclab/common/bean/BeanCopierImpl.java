package xyz.srclab.common.bean;

import xyz.srclab.common.base.Fs;
import xyz.srclab.common.convert.FsConvertException;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.reflect.FsType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

final class BeanCopierImpl implements FsBeanCopier {

    static BeanCopierImpl INSTANCE = new BeanCopierImpl();

    @Override
    public <T> T copyProperties(Object source, Type sourceType, T dest, Type destType, CopyOptions options) {
        if (source instanceof Map) {
            ParameterizedType sourceMapType = FsType.getGenericSuperType(sourceType, Map.class);
            if (sourceMapType == null) {
                throw new IllegalArgumentException("Not a map type: " + sourceType + ".");
            }
            Type[] sourceActualTypes = sourceMapType.getActualTypeArguments();
            Type sourceValueType = sourceActualTypes[1];
            if (dest instanceof Map) {
                FsConverter converter = Fs.notNull(options.getConverter(), FsConverter.defaultConverter());
                ParameterizedType destMapType = FsType.getGenericSuperType(destType, Map.class);
                if (destMapType == null) {
                    throw new IllegalArgumentException("Not a map type: " + destType + ".");
                }
                Type[] destActualTypes = destMapType.getActualTypeArguments();
                Type destValueType = destActualTypes[1];
                Map<Object, Object> sourceMap = (Map<Object, Object>) source;
                Map<Object, Object> destMap = (Map<Object, Object>) dest;
                sourceMap.forEach((key, value) -> {
                    if (options.getSourcePropertyFilter() != null
                        && !options.getSourcePropertyFilter().test(key, value)) {
                        return;
                    }
                    Object destKey = options.getPropertyNameMapper() == null ?
                        key : options.getPropertyNameMapper().apply(key);
                    if (destKey == null) {
                        return;
                    }
                    if (!options.isPutIfNotContained() && !destMap.containsKey(destKey)) {
                        return;
                    }
                    Object newDestValue = converter.convertObject(value, sourceValueType, destValueType);
                    if (newDestValue == Fs.RETURN) {
                        if (options.isThrowIfConvertFailed()) {
                            throw new FsConvertException(sourceValueType, destValueType);
                        } else {
                            return;
                        }
                    }
                    if (options.getDestPropertyFilter() != null
                        && !options.getDestPropertyFilter().test(destKey, newDestValue)) {
                        return;
                    }
                    destMap.put(destKey, newDestValue);
                });
            } else {
                FsBeanResolver resolver = Fs.notNull(options.getBeanResolver(), FsBeanResolver.defaultResolver());
                FsConverter converter = Fs.notNull(options.getConverter(), FsConverter.defaultConverter());
                FsBean destBean = resolver.resolve(destType);
                Map<Object, Object> sourceMap = (Map<Object, Object>) source;
                sourceMap.forEach((key, value) -> {
                    if (options.getSourcePropertyFilter() != null
                        && !options.getSourcePropertyFilter().test(key, value)) {
                        return;
                    }
                    Object destNameObj = options.getPropertyNameMapper() == null ?
                        key : options.getPropertyNameMapper().apply(key);
                    if (destNameObj == null) {
                        return;
                    }
                    String destName = destNameObj.toString();
                    FsProperty destProperty = destBean.getProperty(destName);
                    if (destProperty == null || !destProperty.isWriteable()) {
                        return;
                    }
                    Object newDestValue = converter.convertObject(value, sourceValueType, destProperty.getType());
                    if (newDestValue == Fs.RETURN) {
                        if (options.isThrowIfConvertFailed()) {
                            throw new FsConvertException(sourceValueType, destProperty.getType());
                        } else {
                            return;
                        }
                    }
                    if (options.getDestPropertyFilter() != null
                        && !options.getDestPropertyFilter().test(destName, newDestValue)) {
                        return;
                    }
                    destProperty.set(dest, newDestValue);
                });
            }
        } else {
            FsBeanResolver resolver = Fs.notNull(options.getBeanResolver(), FsBeanResolver.defaultResolver());
            FsConverter converter = Fs.notNull(options.getConverter(), FsConverter.defaultConverter());
            FsBean sourceBean = resolver.resolve(sourceType);
            if (dest instanceof Map) {
                ParameterizedType destMapType = FsType.getGenericSuperType(destType, Map.class);
                if (destMapType == null) {
                    throw new IllegalArgumentException("Not a map type: " + destType + ".");
                }
                Type[] destActualTypes = destMapType.getActualTypeArguments();
                Type destValueType = destActualTypes[1];
                Map<Object, Object> destMap = (Map<Object, Object>) dest;
                sourceBean.getProperties().forEach((sourceName, sourceProperty) -> {
                    if (!sourceProperty.isReadable()) {
                        return;
                    }
                    Object sourceValue = sourceProperty.get(source);
                    if (options.getSourcePropertyFilter() != null
                        && !options.getSourcePropertyFilter().test(sourceName, sourceValue)) {
                        return;
                    }
                    Object destKey = options.getPropertyNameMapper() == null ?
                        sourceName : options.getPropertyNameMapper().apply(sourceName);
                    if (destKey == null) {
                        return;
                    }
                    if (!options.isPutIfNotContained() && !destMap.containsKey(destKey)) {
                        return;
                    }
                    Object newDestValue = converter.convertObject(sourceValue, sourceProperty.getType(), destValueType);
                    if (newDestValue == Fs.RETURN) {
                        if (options.isThrowIfConvertFailed()) {
                            throw new FsConvertException(sourceProperty.getType(), destValueType);
                        } else {
                            return;
                        }
                    }
                    if (options.getDestPropertyFilter() != null
                        && !options.getDestPropertyFilter().test(destKey, newDestValue)) {
                        return;
                    }
                    destMap.put(destKey, newDestValue);
                });
            } else {
                FsBean destBean = resolver.resolve(destType);
                sourceBean.getProperties().forEach((sourceName, sourceProperty) -> {
                    if (!sourceProperty.isReadable()) {
                        return;
                    }
                    Object sourceValue = sourceProperty.get(source);
                    if (options.getSourcePropertyFilter() != null
                        && !options.getSourcePropertyFilter().test(sourceName, sourceValue)) {
                        return;
                    }
                    Object destNameObj = options.getPropertyNameMapper() == null ?
                        sourceName : options.getPropertyNameMapper().apply(sourceName);
                    if (destNameObj == null) {
                        return;
                    }
                    String destName = destNameObj.toString();
                    FsProperty destProperty = destBean.getProperty(destName);
                    if (destProperty == null || !destProperty.isWriteable()) {
                        return;
                    }
                    Object newDestValue = converter.convertObject(
                        sourceValue, sourceProperty.getType(), destProperty.getType());
                    if (newDestValue == Fs.RETURN) {
                        if (options.isThrowIfConvertFailed()) {
                            throw new FsConvertException(sourceProperty.getType(), destProperty.getType());
                        } else {
                            return;
                        }
                    }
                    if (options.getDestPropertyFilter() != null
                        && !options.getDestPropertyFilter().test(destName, newDestValue)) {
                        return;
                    }
                    destProperty.set(dest, newDestValue);
                });
            }
        }
        return dest;
    }
}
