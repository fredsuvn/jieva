package xyz.fsgek.common.bean;

import xyz.fslabo.annotations.Nullable;
import xyz.fsgek.common.base.Gek;
import xyz.fsgek.common.base.GekOption;
import xyz.fsgek.common.collect.GekColl;
import xyz.fsgek.common.mapper.JieMapper;
import xyz.fsgek.common.reflect.GekParamType;
import xyz.fsgek.common.reflect.GekReflect;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final class CopierImpl implements GekBeanCopier {

    static CopierImpl DEFAULT_COPIER = new CopierImpl();

    @Override
    public void copyProperties(
        @Nullable Object source, Type sourceType,
        @Nullable Object dest, Type destType,
        GekOption<?, ?>... options
    ) throws GekBeanCopyException {
        try {
            if (source instanceof Map) {
                if (dest instanceof Map) {
                    mapToMap(source, sourceType, dest, destType, options);
                } else {
                    mapToData(source, sourceType, dest, destType, options);
                }
            } else {
                if (dest instanceof Map) {
                    dataToMap(source, sourceType, dest, destType, options);
                } else {
                    dataToData(source, sourceType, dest, destType, options);
                }
            }
        } catch (GekBeanCopyException e) {
            throw e;
        } catch (Exception e) {
            throw new GekBeanCopyException(sourceType, destType, e);
        }
    }

    public void mapToMap(
        @Nullable Object source, Type sourceType,
        @Nullable Object dest, Type destType,
        GekOption<?, ?>... options
    ) {
        GekParamType sourceParamType = GekReflect.getGenericSuperType(sourceType, Map.class);
        checkMapType(sourceParamType);
        Type sourceKeyType = sourceParamType.getActualTypeArgument(0);
        Type sourceValueType = sourceParamType.getActualTypeArgument(1);
        Map<Object, Object> sourceMap = Gek.as(source);
        GekParamType destParamType = GekReflect.getGenericSuperType(destType, Map.class);
        checkMapType(destParamType);
        Type destKeyType = destParamType.getActualTypeArgument(0);
        Type destValueType = destParamType.getActualTypeArgument(1);
        Map<Object, Object> destMap = Gek.as(dest);
        JieMapper converter = GekOption.get(GekBeanOption.Key.CONVERTER, JieMapper.defaultMapper(), options);
        Object ignoredProperties = GekOption.get(GekBeanOption.Key.IGNORED_PROPERTIES, options);
        Object ignoreNull = GekOption.get(GekBeanOption.Key.IGNORE_NULL, options);
        Object thrownIfConversionFails = GekOption.get(GekBeanOption.Key.THROWN_IF_ANY_FAILS, options);
        Object putIfNotContained = GekOption.get(GekBeanOption.Key.PUT_IF_NOT_CONTAINED, options);
        sourceMap.forEach((key, value) -> {
            if (isIgnored(key, ignoredProperties)) {
                return;
            }
            if (value == null && ignoreNull != null && Objects.equals(true, ignoreNull)) {
                return;
            }
            Object destKey = converter.map(key, sourceKeyType, destKeyType, options);
            if (destKey == null) {
                if (thrownIfConversionFails != null && Objects.equals(false, ignoreNull)) {
                    throw new GekBeanCopyException(sourceKeyType, destKeyType);
                } else {
                    return;
                }
            }
            destKey = JieMapper.resolveResult(destKey);
            if (!destMap.containsKey(destKey) && putIfNotContained != null && Objects.equals(false, putIfNotContained)) {
                return;
            }
            Object destValue = converter.map(value, sourceValueType, destValueType, options);
            if (destValue == null) {
                if (thrownIfConversionFails != null && Objects.equals(false, ignoreNull)) {
                    throw new GekBeanCopyException(sourceValueType, destValueType);
                } else {
                    return;
                }
            }
            destValue = JieMapper.resolveResult(destValue);
            destMap.put(destKey, destValue);
        });
    }

    public void mapToData(
        @Nullable Object source, Type sourceType,
        @Nullable Object dest, Type destType,
        GekOption<?, ?>... options
    ) {
        GekParamType sourceParamType = GekReflect.getGenericSuperType(sourceType, Map.class);
        checkMapType(sourceParamType);
        Type sourceKeyType = sourceParamType.getActualTypeArgument(0);
        Type sourceValueType = sourceParamType.getActualTypeArgument(1);
        Map<Object, Object> sourceMap = Gek.as(source);
        GekBeanResolver resolver = GekOption.get(GekBeanOption.Key.PROVIDER, GekBeanResolver.defaultResolver(), options);
        GekBeanInfo destData = resolver.resolve(destType);
        Map<String, GekPropertyInfo> destProperties = destData.getProperties();
        JieMapper converter = GekOption.get(GekBeanOption.Key.CONVERTER, JieMapper.defaultMapper(), options);
        Object ignoredProperties = GekOption.get(GekBeanOption.Key.IGNORED_PROPERTIES, options);
        Object ignoreNull = GekOption.get(GekBeanOption.Key.IGNORE_NULL, options);
        Object thrownIfConversionFails = GekOption.get(GekBeanOption.Key.THROWN_IF_ANY_FAILS, options);
        sourceMap.forEach((key, value) -> {
            if (isIgnored(key, ignoredProperties)) {
                return;
            }
            if (value == null && ignoreNull != null && Objects.equals(true, ignoreNull)) {
                return;
            }
            Object destKey = converter.map(key, sourceKeyType, String.class, options);
            if (destKey == null) {
                if (thrownIfConversionFails != null && Objects.equals(false, ignoreNull)) {
                    throw new GekBeanCopyException(sourceKeyType, String.class);
                } else {
                    return;
                }
            }
            String destName = destKey.toString();
            GekPropertyInfo destProperty = destProperties.get(destName);
            if (destProperty == null) {
                return;
            }
            Object destValue = converter.map(value, sourceValueType, destProperty.getType(), options);
            if (destValue == null) {
                if (thrownIfConversionFails != null && Objects.equals(false, ignoreNull)) {
                    throw new GekBeanCopyException(sourceValueType, destProperty.getType());
                } else {
                    return;
                }
            }
            destValue = JieMapper.resolveResult(destValue);
            destProperty.setValue(dest, destValue);
        });
    }

    public void dataToMap(
        @Nullable Object source, Type sourceType,
        @Nullable Object dest, Type destType,
        GekOption<?, ?>... options
    ) {
        GekBeanResolver resolver = GekOption.get(GekBeanOption.Key.PROVIDER, GekBeanResolver.defaultResolver(), options);
        GekBeanInfo sourceData = resolver.resolve(sourceType);
        Map<String, GekPropertyInfo> sourceProperties = sourceData.getProperties();
        GekParamType destParamType = GekReflect.getGenericSuperType(destType, Map.class);
        checkMapType(destParamType);
        Type destKeyType = destParamType.getActualTypeArgument(0);
        Type destValueType = destParamType.getActualTypeArgument(1);
        Map<Object, Object> destMap = Gek.as(dest);
        JieMapper converter = GekOption.get(GekBeanOption.Key.CONVERTER, JieMapper.defaultMapper(), options);
        Object ignoredProperties = GekOption.get(GekBeanOption.Key.IGNORED_PROPERTIES, options);
        Object ignoreNull = GekOption.get(GekBeanOption.Key.IGNORE_NULL, options);
        Object thrownIfConversionFails = GekOption.get(GekBeanOption.Key.THROWN_IF_ANY_FAILS, options);
        Object putIfNotContained = GekOption.get(GekBeanOption.Key.PUT_IF_NOT_CONTAINED, options);
        sourceProperties.forEach((name, property) -> {
            if (isIgnored(name, ignoredProperties)) {
                return;
            }
            Object value = property.getValue(source);
            if (value == null && ignoreNull != null && Objects.equals(true, ignoreNull)) {
                return;
            }
            Object destKey = converter.map(name, String.class, destKeyType, options);
            if (destKey == null) {
                if (thrownIfConversionFails != null && Objects.equals(false, ignoreNull)) {
                    throw new GekBeanCopyException(String.class, destKeyType);
                } else {
                    return;
                }
            }
            destKey = JieMapper.resolveResult(destKey);
            if (!destMap.containsKey(destKey) && putIfNotContained != null && Objects.equals(false, putIfNotContained)) {
                return;
            }
            Object destValue = converter.map(value, property.getType(), destValueType, options);
            if (destValue == null) {
                if (thrownIfConversionFails != null && Objects.equals(false, ignoreNull)) {
                    throw new GekBeanCopyException(property.getType(), destValueType);
                } else {
                    return;
                }
            }
            destValue = JieMapper.resolveResult(destValue);
            destMap.put(destKey, destValue);
        });
    }

    public void dataToData(
        @Nullable Object source, Type sourceType,
        @Nullable Object dest, Type destType,
        GekOption<?, ?>... options
    ) {
        GekBeanResolver resolver = GekOption.get(GekBeanOption.Key.PROVIDER, GekBeanResolver.defaultResolver(), options);
        GekBeanInfo sourceData = resolver.resolve(sourceType);
        Map<String, GekPropertyInfo> sourceProperties = sourceData.getProperties();
        GekBeanInfo destData = resolver.resolve(destType);
        Map<String, GekPropertyInfo> destProperties = destData.getProperties();
        JieMapper converter = GekOption.get(GekBeanOption.Key.CONVERTER, JieMapper.defaultMapper(), options);
        Object ignoredProperties = GekOption.get(GekBeanOption.Key.IGNORED_PROPERTIES, options);
        Object ignoreNull = GekOption.get(GekBeanOption.Key.IGNORE_NULL, options);
        Object thrownIfConversionFails = GekOption.get(GekBeanOption.Key.THROWN_IF_ANY_FAILS, options);
        sourceProperties.forEach((name, property) -> {
            if (isIgnored(name, ignoredProperties)) {
                return;
            }
            Object value = property.getValue(source);
            if (value == null && ignoreNull != null && Objects.equals(true, ignoreNull)) {
                return;
            }
            GekPropertyInfo destProperty = destProperties.get(name);
            if (destProperty == null) {
                return;
            }
            Object destValue = converter.map(value, property.getType(), destProperty.getType(), options);
            if (destValue == null) {
                if (thrownIfConversionFails != null && Objects.equals(false, ignoreNull)) {
                    throw new GekBeanCopyException(property.getType(), destProperty.getType());
                } else {
                    return;
                }
            }
            destValue = JieMapper.resolveResult(destValue);
            destProperty.setValue(dest, destValue);
        });
    }

    private boolean isIgnored(Object name, @Nullable Object ignoredProperties) {
        if (ignoredProperties == null) {
            return false;
        }
        if (ignoredProperties instanceof Iterable<?>) {
            for (Object ignoredProperty : ((Iterable<?>) ignoredProperties)) {
                if (Objects.equals(name, ignoredProperty)) {
                    return true;
                }
            }
            return false;
        }
        if (ignoredProperties instanceof Object[]) {
            for (Object ignoredProperty : ((Object[]) ignoredProperties)) {
                if (Objects.equals(name, ignoredProperty)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private void checkMapType(@Nullable GekParamType paramType) {
        if (paramType == null) {
            throw new GekBeanCopyException("Not a Map type: null.");
        }
        List<Type> types = paramType.getActualTypeArgumentList();
        if (GekColl.isEmpty(types) || types.size() != 2) {
            throw new GekBeanCopyException("Not a Map type: " + paramType + ".");
        }
    }
}
