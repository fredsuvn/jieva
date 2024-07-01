package xyz.fslabo.common.mapper;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.base.GekObject;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.base.Option;
import xyz.fslabo.common.bean.BeanCopyException;
import xyz.fslabo.common.bean.BeanInfo;
import xyz.fslabo.common.bean.BeanResolver;
import xyz.fslabo.common.collect.JieColl;
import xyz.fslabo.common.ref.Val;
import xyz.fslabo.common.reflect.GekParamType;
import xyz.fslabo.common.reflect.JieReflect;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

final class BeanMapperImpl implements BeanMapper {

    static BeanMapperImpl DEFAULT_MAPPER = new BeanMapperImpl();

    @Override
    public void copyProperties(Object source, Object dest, MapperOptions options) throws MapperException {
        try {
            if (source instanceof Map) {
                if (dest instanceof Map) {
                    mapToMap(source, dest, options);
                } else {
                    mapToBean(source, dest, options);
                }
            } else {
                if (dest instanceof Map) {
                    beanToMap(source, dest, options);
                } else {
                    beanToBean(source, dest, options);
                }
            }
        } catch (MapperException e) {
            throw e;
        } catch (Exception e) {
            throw new MapperException(e);
        }
    }

    private Type getSourceType(@Nullable Object source, MapperOptions options) {
        Type st = options.getSourceType();
        if (st != null) {
            return st;
        }
        return source == null ? Object.class : source.getClass();
    }

    private Type getDestType(Object dest, MapperOptions options) {
        Type dt = options.getDestType();
        if (dt != null) {
            return dt;
        }
        return dest.getClass();
    }

    private Object mapObject(Mapper mapper, @Nullable Object object, Type destType, MapperOptions options) {
        boolean ignoreError = options.isIgnoreError();
        try {
            Object result = mapper.mapObject(object, destType, options);
        } catch (MapperException e) {
            if (ignoreError) {

            }
        } catch (Exception e) {

        }
    }

    public void mapToMap(Object source, Object dest, MapperOptions options) {
        Type sourceType = getSourceType(source, options);
        Type destType = getDestType(dest, options);
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
        Collection<?> ignored = Jie.orDefault(options.getIgnored(), Collections.emptyList());
        boolean ignoreNull = options.isIgnoreNull();
        Function<Object, @Nullable Object> nameMapper = options.getNameMapper();
        Mapper mapper = Jie.orDefault(options.getMapper(), Mapper.defaultMapper());
        boolean ignoreError = options.isIgnoreError();
        boolean putNew = options.isPutNew();
        sourceMap.forEach((key, value) -> {
            if (ignored.contains(key)) {
                return;
            }
            if (value == null && ignoreNull) {
                return;
            }
            Object mappedKey = nameMapper == null ? key : nameMapper.apply(key);
            if (mappedKey == null) {
                return;
            }
            Object destKey;
            try {
                Object result = mapper.mapObject(mappedKey, destKeyType, options);
                if (Objects.equals(result, Flag.UNSUPPORTED)) {
                    if (ignoreError) {
                        return;
                    }
                    throw new MapperException(mappedKey.getClass(), destType);
                }
                if (result instanceof Val) {
                    destKey = ((Val<?>) result).get();
                } else {
                    destKey = result;
                }
            } catch (MapperException e) {
                if (ignoreError) {
                    return;
                }
                throw e;
            } catch (Exception e) {
                if (ignoreError) {
                    return;
                }
                throw new MapperException(e);
            }
            if (destKey == null) {
                if (ignoreNull) {
                    return;
                }
            }
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
            destValue = Mapper.resolveResult(destValue);
            destMap.put(destKey, destValue);
        });
    }

    public void mapToBean(Object source, Object dest, MapperOptions options) {
        GekParamType sourceParamType = JieReflect.getGenericSuperType(sourceType, Map.class);
        checkMapType(sourceParamType);
        Type sourceKeyType = sourceParamType.getActualTypeArgument(0);
        Type sourceValueType = sourceParamType.getActualTypeArgument(1);
        Map<Object, Object> sourceMap = Jie.as(source);
        BeanResolver resolver = Option.find(MapperOptions.Key.PROVIDER, BeanResolver.defaultResolver(), options);
        BeanInfo destData = resolver.resolve(destType);
        Map<String, PropertyInfo> destProperties = destData.getProperties();
        Mapper converter = Option.find(MapperOptions.Key.CONVERTER, Mapper.defaultMapper(), options);
        Object ignoredProperties = Option.find(MapperOptions.Key.IGNORED_PROPERTIES, options);
        Object ignoreNull = Option.find(MapperOptions.Key.IGNORE_NULL, options);
        Object thrownIfConversionFails = Option.find(MapperOptions.Key.THROWN_IF_ANY_FAILS, options);
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
            destValue = Mapper.resolveResult(destValue);
            destProperty.setValue(dest, destValue);
        });
    }

    public void beanToMap(Object source, Object dest, MapperOptions options) {
        BeanResolver resolver = Option.find(MapperOptions.Key.PROVIDER, BeanResolver.defaultResolver(), options);
        BeanInfo sourceData = resolver.resolve(sourceType);
        Map<String, PropertyInfo> sourceProperties = sourceData.getProperties();
        GekParamType destParamType = JieReflect.getGenericSuperType(destType, Map.class);
        checkMapType(destParamType);
        Type destKeyType = destParamType.getActualTypeArgument(0);
        Type destValueType = destParamType.getActualTypeArgument(1);
        Map<Object, Object> destMap = Jie.as(dest);
        Mapper converter = Option.find(MapperOptions.Key.CONVERTER, Mapper.defaultMapper(), options);
        Object ignoredProperties = Option.find(MapperOptions.Key.IGNORED_PROPERTIES, options);
        Object ignoreNull = Option.find(MapperOptions.Key.IGNORE_NULL, options);
        Object thrownIfConversionFails = Option.find(MapperOptions.Key.THROWN_IF_ANY_FAILS, options);
        Object putIfNotContained = Option.find(MapperOptions.Key.PUT_IF_NOT_CONTAINED, options);
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
            destKey = Mapper.resolveResult(destKey);
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
            destValue = Mapper.resolveResult(destValue);
            destMap.put(destKey, destValue);
        });
    }

    public void beanToBean(Object source, Object dest, MapperOptions options) {
        BeanResolver resolver = Option.find(MapperOptions.Key.PROVIDER, BeanResolver.defaultResolver(), options);
        BeanInfo sourceData = resolver.resolve(sourceType);
        Map<String, PropertyInfo> sourceProperties = sourceData.getProperties();
        BeanInfo destData = resolver.resolve(destType);
        Map<String, PropertyInfo> destProperties = destData.getProperties();
        Mapper converter = Option.find(MapperOptions.Key.CONVERTER, Mapper.defaultMapper(), options);
        Object ignoredProperties = Option.find(MapperOptions.Key.IGNORED_PROPERTIES, options);
        Object ignoreNull = Option.find(MapperOptions.Key.IGNORE_NULL, options);
        Object thrownIfConversionFails = Option.find(MapperOptions.Key.THROWN_IF_ANY_FAILS, options);
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
            destValue = Mapper.resolveResult(destValue);
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
