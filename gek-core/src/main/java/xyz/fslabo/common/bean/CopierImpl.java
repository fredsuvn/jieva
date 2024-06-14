package xyz.fslabo.common.bean;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.base.Option;
import xyz.fslabo.common.collect.JieColl;
import xyz.fslabo.common.mapper.JieMapper;
import xyz.fslabo.common.reflect.GekParamType;
import xyz.fslabo.common.reflect.JieReflect;

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
        Option<?, ?>... options
    ) throws BeanCopyException {
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
        } catch (BeanCopyException e) {
            throw e;
        } catch (Exception e) {
            throw new BeanCopyException(sourceType, destType, e);
        }
    }

    public void mapToMap(
        @Nullable Object source, Type sourceType,
        @Nullable Object dest, Type destType,
        Option<?, ?>... options
    ) {
        GekParamType sourceParamType = JieReflect.getGenericSuperType(sourceType, Map.class);
        checkMapType(sourceParamType);
        Type sourceKeyType = sourceParamType.getActualTypeArgument(0);
        Type sourceValueType = sourceParamType.getActualTypeArgument(1);
        Map<Object, Object> sourceMap = Jie.as(source);
        GekParamType destParamType = JieReflect.getGenericSuperType(destType, Map.class);
        checkMapType(destParamType);
        Type destKeyType = destParamType.getActualTypeArgument(0);
        Type destValueType = destParamType.getActualTypeArgument(1);
        Map<Object, Object> destMap = Jie.as(dest);
        JieMapper converter = Option.find(BeanOption.Key.CONVERTER, JieMapper.defaultMapper(), options);
        Object ignoredProperties = Option.find(BeanOption.Key.IGNORED_PROPERTIES, options);
        Object ignoreNull = Option.find(BeanOption.Key.IGNORE_NULL, options);
        Object thrownIfConversionFails = Option.find(BeanOption.Key.THROWN_IF_ANY_FAILS, options);
        Object putIfNotContained = Option.find(BeanOption.Key.PUT_IF_NOT_CONTAINED, options);
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
                    throw new BeanCopyException(sourceKeyType, destKeyType);
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
                    throw new BeanCopyException(sourceValueType, destValueType);
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
        Option<?, ?>... options
    ) {
        GekParamType sourceParamType = JieReflect.getGenericSuperType(sourceType, Map.class);
        checkMapType(sourceParamType);
        Type sourceKeyType = sourceParamType.getActualTypeArgument(0);
        Type sourceValueType = sourceParamType.getActualTypeArgument(1);
        Map<Object, Object> sourceMap = Jie.as(source);
        BeanResolver resolver = Option.find(BeanOption.Key.PROVIDER, BeanResolver.defaultResolver(), options);
        BeanInfo destData = resolver.resolve(destType);
        Map<String, PropertyInfo> destProperties = destData.getProperties();
        JieMapper converter = Option.find(BeanOption.Key.CONVERTER, JieMapper.defaultMapper(), options);
        Object ignoredProperties = Option.find(BeanOption.Key.IGNORED_PROPERTIES, options);
        Object ignoreNull = Option.find(BeanOption.Key.IGNORE_NULL, options);
        Object thrownIfConversionFails = Option.find(BeanOption.Key.THROWN_IF_ANY_FAILS, options);
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
                    throw new BeanCopyException(sourceKeyType, String.class);
                } else {
                    return;
                }
            }
            String destName = destKey.toString();
            PropertyInfo destProperty = destProperties.get(destName);
            if (destProperty == null) {
                return;
            }
            Object destValue = converter.map(value, sourceValueType, destProperty.getType(), options);
            if (destValue == null) {
                if (thrownIfConversionFails != null && Objects.equals(false, ignoreNull)) {
                    throw new BeanCopyException(sourceValueType, destProperty.getType());
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
        Option<?, ?>... options
    ) {
        BeanResolver resolver = Option.find(BeanOption.Key.PROVIDER, BeanResolver.defaultResolver(), options);
        BeanInfo sourceData = resolver.resolve(sourceType);
        Map<String, PropertyInfo> sourceProperties = sourceData.getProperties();
        GekParamType destParamType = JieReflect.getGenericSuperType(destType, Map.class);
        checkMapType(destParamType);
        Type destKeyType = destParamType.getActualTypeArgument(0);
        Type destValueType = destParamType.getActualTypeArgument(1);
        Map<Object, Object> destMap = Jie.as(dest);
        JieMapper converter = Option.find(BeanOption.Key.CONVERTER, JieMapper.defaultMapper(), options);
        Object ignoredProperties = Option.find(BeanOption.Key.IGNORED_PROPERTIES, options);
        Object ignoreNull = Option.find(BeanOption.Key.IGNORE_NULL, options);
        Object thrownIfConversionFails = Option.find(BeanOption.Key.THROWN_IF_ANY_FAILS, options);
        Object putIfNotContained = Option.find(BeanOption.Key.PUT_IF_NOT_CONTAINED, options);
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
                    throw new BeanCopyException(String.class, destKeyType);
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
                    throw new BeanCopyException(property.getType(), destValueType);
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
        Option<?, ?>... options
    ) {
        BeanResolver resolver = Option.find(BeanOption.Key.PROVIDER, BeanResolver.defaultResolver(), options);
        BeanInfo sourceData = resolver.resolve(sourceType);
        Map<String, PropertyInfo> sourceProperties = sourceData.getProperties();
        BeanInfo destData = resolver.resolve(destType);
        Map<String, PropertyInfo> destProperties = destData.getProperties();
        JieMapper converter = Option.find(BeanOption.Key.CONVERTER, JieMapper.defaultMapper(), options);
        Object ignoredProperties = Option.find(BeanOption.Key.IGNORED_PROPERTIES, options);
        Object ignoreNull = Option.find(BeanOption.Key.IGNORE_NULL, options);
        Object thrownIfConversionFails = Option.find(BeanOption.Key.THROWN_IF_ANY_FAILS, options);
        sourceProperties.forEach((name, property) -> {
            if (isIgnored(name, ignoredProperties)) {
                return;
            }
            Object value = property.getValue(source);
            if (value == null && ignoreNull != null && Objects.equals(true, ignoreNull)) {
                return;
            }
            PropertyInfo destProperty = destProperties.get(name);
            if (destProperty == null) {
                return;
            }
            Object destValue = converter.map(value, property.getType(), destProperty.getType(), options);
            if (destValue == null) {
                if (thrownIfConversionFails != null && Objects.equals(false, ignoreNull)) {
                    throw new BeanCopyException(property.getType(), destProperty.getType());
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
            throw new BeanCopyException("Not a Map type: null.");
        }
        List<Type> types = paramType.getActualTypeArgumentList();
        if (JieColl.isEmpty(types) || types.size() != 2) {
            throw new BeanCopyException("Not a Map type: " + paramType + ".");
        }
    }
}
