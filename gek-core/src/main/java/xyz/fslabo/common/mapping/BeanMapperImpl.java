package xyz.fslabo.common.mapping;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.bean.BeanException;
import xyz.fslabo.common.bean.BeanInfo;
import xyz.fslabo.common.bean.BeanProvider;
import xyz.fslabo.common.bean.PropertyInfo;
import xyz.fslabo.common.coll.JieColl;
import xyz.fslabo.common.ref.Val;
import xyz.fslabo.common.reflect.JieReflect;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiFunction;

final class BeanMapperImpl implements BeanMapper {

    static BeanMapperImpl DEFAULT_MAPPER = new BeanMapperImpl();

    @Override
    public void copyProperties(
        Object source, Type sourceType, Object dest, Type destType, MappingOptions options
    ) throws MappingException {
        try {
            if (source instanceof Map) {
                if (dest instanceof Map) {
                    mapToMap(source, sourceType, dest, destType, options);
                } else {
                    mapToBean(source, sourceType, dest, destType, options);
                }
            } else {
                if (dest instanceof Map) {
                    beanToMap(source, sourceType, dest, destType, options);
                } else {
                    beanToBean(source, sourceType, dest, destType, options);
                }
            }
        } catch (MappingException e) {
            throw e;
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    private void mapToMap(Object source, Type sourceType, Object dest, Type destType, MappingOptions options) {
        List<Type> sourceTypeArgs = JieReflect.getActualTypeArguments(sourceType, Map.class);
        checkMapArgs(sourceTypeArgs, sourceType);
        Type sourceKeyType = sourceTypeArgs.get(0);
        Type sourceValueType = sourceTypeArgs.get(1);
        Map<Object, Object> sourceMap = Jie.as(source);
        List<Type> destParamType = JieReflect.getActualTypeArguments(destType, Map.class);
        checkMapArgs(destParamType, destType);
        Type destKeyType = destParamType.get(0);
        Type destValueType = destParamType.get(1);
        Map<Object, Object> destMap = Jie.as(dest);
        Collection<?> ignored = Jie.orDefault(options.getIgnored(), Collections.emptyList());
        boolean ignoreNull = options.isIgnoreNull();
        BiFunction<Object, Type, @Nullable Object> nameMapper = Jie.orDefault(options.getNameMapper(), (o1, o2) -> o1);
        Mapper mapper = Jie.orDefault(options.getMapper(), Mapper.defaultMapper());
        boolean ignoreError = options.isIgnoreError();
        sourceMap.forEach((key, value) -> {
            if (ignored.contains(key)) {
                return;
            }
            if (value == null && ignoreNull) {
                return;
            }
            Object mappedKey = nameMapper.apply(key, sourceKeyType);
            if (mappedKey == null) {
                return;
            }
            putToMap(
                mappedKey, sourceKeyType, destKeyType,
                value, sourceValueType, destValueType,
                destMap, mapper, options
            );
        });
    }

    public void mapToBean(Object source, Type sourceType, Object dest, Type destType, MappingOptions options) {
        List<Type> sourceTypeArgs = JieReflect.getActualTypeArguments(sourceType, Map.class);
        checkMapArgs(sourceTypeArgs, sourceType);
        Type sourceKeyType = sourceTypeArgs.get(0);
        Type sourceValueType = sourceTypeArgs.get(1);
        Map<Object, Object> sourceMap = Jie.as(source);
        BeanProvider beanProvider = Jie.orDefault(options.getBeanProvider(), BeanProvider.defaultProvider());
        BeanInfo destInfo = beanProvider.getBeanInfo(destType);
        Map<String, PropertyInfo> destProperties = destInfo.getProperties();
        Collection<?> ignored = Jie.orDefault(options.getIgnored(), Collections.emptyList());
        boolean ignoreNull = options.isIgnoreNull();
        BiFunction<Object, Type, @Nullable Object> nameMapper = Jie.orDefault(options.getNameMapper(), (o1, o2) -> o1);
        Mapper mapper = Jie.orDefault(options.getMapper(), Mapper.defaultMapper());
        boolean ignoreError = options.isIgnoreError();
        sourceMap.forEach((key, value) -> {
            if (ignored.contains(key)) {
                return;
            }
            if (value == null && ignoreNull) {
                return;
            }
            Object mappedKey = nameMapper.apply(key, sourceKeyType);
            if (mappedKey == null) {
                return;
            }
            putToBean(
                mappedKey, sourceKeyType, value, sourceValueType,
                dest, destProperties, mapper, options
            );
        });
    }

    public void beanToMap(Object source, Type sourceType, Object dest, Type destType, MappingOptions options) {
        BeanProvider beanProvider = Jie.orDefault(options.getBeanProvider(), BeanProvider.defaultProvider());
        BeanInfo sourceInfo = beanProvider.getBeanInfo(sourceType);
        Map<String, PropertyInfo> sourceProperties = sourceInfo.getProperties();
        List<Type> destTypeArgs = JieReflect.getActualTypeArguments(destType, Map.class);
        checkMapArgs(destTypeArgs, destType);
        Type destKeyType = destTypeArgs.get(0);
        Type destValueType = destTypeArgs.get(1);
        Map<Object, Object> destMap = Jie.as(dest);
        Collection<?> ignored = Jie.orDefault(options.getIgnored(), Collections.emptyList());
        boolean ignoreNull = options.isIgnoreNull();
        BiFunction<Object, Type, @Nullable Object> nameMapper = Jie.orDefault(options.getNameMapper(), (o1, o2) -> o1);
        Mapper mapper = Jie.orDefault(options.getMapper(), Mapper.defaultMapper());
        boolean ignoreError = options.isIgnoreError();
        sourceProperties.forEach((name, property) -> {
            if (ignored.contains(name)) {
                return;
            }
            Object mappedKey = nameMapper.apply(name, String.class);
            if (mappedKey == null) {
                return;
            }
            putToMap(
                mappedKey, String.class, destKeyType,
                property.getValue(source), property.getType(), destValueType,
                destMap, mapper, options
            );
        });
    }

    public void beanToBean(Object source, Type sourceType, Object dest, Type destType, MappingOptions options) {
        BeanProvider beanProvider = Jie.orDefault(options.getBeanProvider(), BeanProvider.defaultProvider());
        BeanInfo sourceInfo = beanProvider.getBeanInfo(sourceType);
        Map<String, PropertyInfo> sourceProperties = sourceInfo.getProperties();
        BeanInfo destInfo = beanProvider.getBeanInfo(destType);
        Map<String, PropertyInfo> destProperties = destInfo.getProperties();
        Collection<?> ignored = Jie.orDefault(options.getIgnored(), Collections.emptyList());
        boolean ignoreNull = options.isIgnoreNull();
        BiFunction<Object, Type, @Nullable Object> nameMapper = Jie.orDefault(options.getNameMapper(), (o1, o2) -> o1);
        Mapper mapper = Jie.orDefault(options.getMapper(), Mapper.defaultMapper());
        boolean ignoreError = options.isIgnoreError();
        sourceProperties.forEach((name, property) -> {
            if (ignored.contains(name)) {
                return;
            }
            Object mappedKey = nameMapper.apply(name, String.class);
            if (mappedKey == null) {
                return;
            }
            putToBean(
                mappedKey, String.class, property.getValue(source), property.getType(),
                dest, destProperties, mapper, options
            );
        });
    }

    private void checkMapArgs(@Nullable List<Type> args, Type type) {
        if (JieColl.isEmpty(args) || args.size() != 2) {
            throw new BeanException("Not a Map type: " + type.getTypeName() + "!");
        }
    }

    private void putToMap(
        Object mappedKey, Type sourceKeyType, Type destKeyType,
        Object sourceValue, Type sourceValueType, Type destValueType,
        Map<Object, Object> destMap, Mapper mapper, MappingOptions options
    ) {
        if (mappedKey instanceof Collection) {
            for (Object mk : ((Collection<?>) mappedKey)) {
                putToMap0(mk, sourceKeyType, destKeyType, sourceValue, sourceValueType, destValueType, destMap, mapper, options);
            }
        } else {
            putToMap0(mappedKey, sourceKeyType, destKeyType, sourceValue, sourceValueType, destValueType, destMap, mapper, options);
        }
    }

    private void putToMap0(
        Object mappedKey, Type sourceKeyType, Type destKeyType,
        Object sourceValue, Type sourceValueType, Type destValueType,
        Map<Object, Object> destMap, Mapper mapper, MappingOptions options
    ) {
        boolean ignoreError = options.isIgnoreError();
        Object destKey = getDestKey(mappedKey, sourceKeyType, destKeyType, mapper, options);
        if (destKey == F.RETURN) {
            return;
        }
        if (!destMap.containsKey(destKey) && !options.isPutNew()) {
            return;
        }
        Object destValue;
        try {
            destValue = mapper.map(sourceValue, sourceValueType, destValueType, options);
            if (Objects.equals(destValue, Flag.UNSUPPORTED)) {
                if (ignoreError) {
                    return;
                }
                throw new MappingException(sourceKeyType, destKeyType);
            }
            if (destValue instanceof Val) {
                destValue = ((Val<?>) destValue).get();
            }
        } catch (MappingException e) {
            if (ignoreError) {
                return;
            }
            throw e;
        } catch (Exception e) {
            if (ignoreError) {
                return;
            }
            throw new MappingException(e);
        }
        if (destValue == null) {
            if (options.isIgnoreNull()) {
                return;
            }
        }
        destMap.put(destKey, destValue);
    }

    private void putToBean(
        Object mappedKey, Type sourceKeyType, Object sourceValue, Type sourceValueType,
        Object dest, Map<String, PropertyInfo> destProperties, Mapper mapper, MappingOptions options
    ) {
        if (mappedKey instanceof Collection) {
            for (Object mk : ((Collection<?>) mappedKey)) {
                putToBean0(mk, sourceKeyType, sourceValue, sourceValueType, dest, destProperties, mapper, options);
            }
        } else {
            putToBean0(mappedKey, sourceKeyType, sourceValue, sourceValueType, dest, destProperties, mapper, options);
        }
    }

    private void putToBean0(
        Object mappedKey, Type sourceKeyType, Object sourceValue, Type sourceValueType,
        Object dest, Map<String, PropertyInfo> destProperties, Mapper mapper, MappingOptions options
    ) {
        boolean ignoreError = options.isIgnoreError();
        Object destKey = getDestKey(mappedKey, sourceKeyType, String.class, mapper, options);
        if (destKey == F.RETURN) {
            return;
        }
        String destName = String.valueOf(destKey);
        PropertyInfo destProperty = destProperties.get(destName);
        if (destProperty == null) {
            return;
        }
        Object destValue;
        try {
            destValue = mapper.mapProperty(sourceValue, sourceValueType, destProperty.getType(), destProperty, options);
            if (Objects.equals(destValue, Flag.UNSUPPORTED)) {
                if (ignoreError) {
                    return;
                }
                throw new MappingException(sourceKeyType, String.class);
            }
            if (destValue instanceof Val) {
                destValue = ((Val<?>) destValue).get();
            }
        } catch (MappingException e) {
            if (ignoreError) {
                return;
            }
            throw e;
        } catch (Exception e) {
            if (ignoreError) {
                return;
            }
            throw new MappingException(e);
        }
        if (destValue == null) {
            if (options.isIgnoreNull()) {
                return;
            }
        }
        destProperty.setValue(dest, destValue);
    }

    @Nullable
    private Object getDestKey(
        Object mappedKey, Type sourceKeyType, Type destKeyType, Mapper mapper, MappingOptions options) {
        boolean ignoreError = options.isIgnoreError();
        Object destKey;
        try {
            destKey = mapper.map(mappedKey, sourceKeyType, destKeyType, options);
            if (Objects.equals(destKey, Flag.UNSUPPORTED)) {
                if (ignoreError) {
                    return F.RETURN;
                }
                throw new MappingException(sourceKeyType, destKeyType);
            }
            if (destKey instanceof Val) {
                destKey = ((Val<?>) destKey).get();
            }
        } catch (MappingException e) {
            if (ignoreError) {
                return F.RETURN;
            }
            throw e;
        } catch (Exception e) {
            if (ignoreError) {
                return F.RETURN;
            }
            throw new MappingException(e);
        }
        if (destKey == null) {
            if (options.isIgnoreNull()) {
                return F.RETURN;
            }
        }
        return destKey;
    }

    private enum F {
        RETURN,
        ;
    }
}
