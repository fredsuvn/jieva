package xyz.srclab.common.bean;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.jetbrains.annotations.Nullable;
import xyz.srclab.common.lang.format.FormatHelper;
import xyz.srclab.common.reflect.ReflectHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultBeanConverterHandler implements BeanConverterHandler {

    public static DefaultBeanConverterHandler getInstance() {
        return INSTANCE;
    }

    private static final DefaultBeanConverterHandler INSTANCE = new DefaultBeanConverterHandler();

    private final ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();

    @Override
    public boolean supportConvert(@Nullable Object from, Type to, BeanOperator beanOperator) {
        return true;
    }

    @Override
    public boolean supportConvert(@Nullable Object from, Class<?> to, BeanOperator beanOperator) {
        return true;
    }

    @Nullable
    @Override
    public <T> T convert(@Nullable Object from, Type to, BeanOperator beanOperator) {
        if (to instanceof Class) {
            return convert(from, (Class<T>) to, beanOperator);
        }
        return (T) convertByBeanOperator(from, to, beanOperator);
    }

    @Override
    @Nullable
    public <T> T convert(@Nullable Object from, Class<T> to, BeanOperator beanOperator) {
        if (Map.class.equals(to)) {
            return (T) convertToMap(from, Object.class, Object.class, beanOperator);
        }

        Object result = convertByConvertUtilsBean(from, to);
        if (result == null) {
            return null;
        }
        if (ReflectHelper.isAssignable(result, to)) {
            return (T) result;
        }
        result = convertToBean(from, to, beanOperator);
        if (result == null) {
            return null;
        }
        if (ReflectHelper.isAssignable(result, to)) {
            return (T) result;
        }
        throw new UnsupportedOperationException(
                FormatHelper.fastFormat("Cannot convert object {} to type {}", from, to)
        );
    }

    @Nullable
    private Object convertByConvertUtilsBean(@Nullable Object from, Class<?> to) {
        return convertUtilsBean.convert(from, to);
    }

    private Object convertByBeanOperator(@Nullable Object from, Type to, BeanOperator beanOperator) {
        if (to.equals(Map.class)) {
            return convertToMap(from, String.class, Object.class, beanOperator);
        }
        if (to instanceof Class) {
            Class<?> cls = (Class<?>) to;
            if (cls.isInterface()) {
                throw new UnsupportedOperationException(
                        FormatHelper.fastFormat("Cannot convert object {} to type {}", from, to));
            }
            return convert(from, cls, beanOperator);
        }
        Class<?> toClass = ReflectHelper.getClass(to);
        if (to instanceof ParameterizedType && Map.class.equals(toClass)) {
            ParameterizedType mapType = (ParameterizedType) to;
            Type[] mapGenericType = mapType.getActualTypeArguments();
            Type keyType = mapGenericType[0];
            Type valueType = mapGenericType[1];
            return convertToMap(from, keyType, valueType, beanOperator);
        }
        return convert(from, toClass, beanOperator);
    }

    private Object convertToBean(@Nullable Object any, Class<?> type, BeanOperator beanOperator) {
        if (any == null) {
            return null;
        }
        Object toInstance = ReflectHelper.newInstance(type);
        beanOperator.copyProperties(any, toInstance);
        return toInstance;
    }

    private Map convertToMap(@Nullable Object any, Type keyType, Type valueType, BeanOperator beanOperator) {
        if (any == null) {
            return Collections.emptyMap();
        }
        Map map = new HashMap<>();
        if (any instanceof Map) {
            Map src = (Map) any;
            src.forEach((k, v) -> {
                Object key = convert(k, keyType, beanOperator);
                Object value = convert(v, valueType, beanOperator);
                map.put(key, value);
            });
        } else {
            BeanDescriptor sourceDescriptor = beanOperator.resolve(any);
            sourceDescriptor.getPropertyDescriptors().forEach((name, descriptor) -> {
                if (!descriptor.isReadable()) {
                    return;
                }
                Object sourceValue = descriptor.getValue(any);
                Object key = convert(name, keyType, beanOperator);
                Object value = convert(sourceValue, valueType, beanOperator);
                map.put(key, value);
            });
        }
        return Collections.unmodifiableMap(map);
    }
}
