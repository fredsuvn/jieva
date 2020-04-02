package xyz.srclab.common.bean;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.common.array.ArrayBuilder;
import xyz.srclab.common.array.ArrayHelper;
import xyz.srclab.common.collection.iterable.IterableHelper;
import xyz.srclab.common.collection.list.ListHelper;
import xyz.srclab.common.collection.map.MapHelper;
import xyz.srclab.common.collection.set.SetHelper;
import xyz.srclab.common.reflect.instance.InstanceHelper;
import xyz.srclab.common.reflect.type.TypeHelper;
import xyz.srclab.common.string.format.fastformat.FastFormat;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@ThreadSafe
public class DefaultBeanConverterHandler implements BeanConverterHandler {

    @Override
    public boolean supportConvert(Object from, Type to, BeanOperator beanOperator) {
        return true;
    }

    @Override
    public boolean supportConvert(Object from, Class<?> to, BeanOperator beanOperator) {
        return true;
    }

    @Override
    public <T> T convert(Object from, Type to, BeanOperator beanOperator) {
        return (T) convertType(from, to, beanOperator);
    }

    @Override
    public <T> T convert(Object from, Class<T> to, BeanOperator beanOperator) {
        return (T) convertClass(from, to, beanOperator);
    }

    private Object convertClass(Object from, Class<?> to, BeanOperator beanOperator) {
        if (to.isAssignableFrom(from.getClass())) {
            return from;
        }
        @Nullable Object basic = tryConvertToBasic(from, to);
        if (basic != null) {
            return basic;
        }
        if (to.isArray()) {
            return convertToArray(from, Object.class, beanOperator);
        }
        if (Map.class.equals(to)) {
            return convertToMap(from, Object.class, Object.class, beanOperator);
        }
        if (List.class.equals(to)) {
            return convertToList(from, Object.class, beanOperator);
        }
        if (Set.class.equals(to) || Collection.class.equals(to)) {
            return convertToSet(from, Object.class, beanOperator);
        }
        return convertToBean(from, to, beanOperator);
    }

    private Object convertType(Object from, Type to, BeanOperator beanOperator) {
        if (to instanceof Class) {
            return convertClass(from, (Class<?>) to, beanOperator);
        }
        Class<?> rawType = TypeHelper.getRawClass(to);
        if (!(to instanceof ParameterizedType)) {
            return convertToBean(from, rawType, beanOperator);
        }
        ParameterizedType parameterizedType = (ParameterizedType) to;
        if (rawType.isArray()) {
            return convertToArray(from, TypeHelper.getGenericTypes(parameterizedType)[0], beanOperator);
        }
        if (Map.class.equals(rawType)) {
            Type[] kv = TypeHelper.getGenericTypes(parameterizedType);
            return convertToMap(from, kv[0], kv[1], beanOperator);
        }
        if (List.class.equals(rawType)) {
            return convertToList(from, TypeHelper.getGenericTypes(parameterizedType)[0], beanOperator);
        }
        if (Set.class.equals(rawType) || Collection.class.equals(rawType)) {
            return convertToSet(from, TypeHelper.getGenericTypes(parameterizedType)[0], beanOperator);
        }
        return convertToBean(from, rawType, beanOperator);
    }

    private Object convertToBean(Object from, Class<?> to, BeanOperator beanOperator) {
        Object toInstance = InstanceHelper.newInstance(to);
        beanOperator.copyProperties(from, toInstance);
        return toInstance;
    }

    private Map convertToMap(Object from, Type toKeyType, Type toValueType, BeanOperator beanOperator) {
        Map map = new HashMap<>();
        if (from instanceof Map) {
            Map src = (Map) from;
            src.forEach((k, v) -> {
                Object key = convert(k, toKeyType, beanOperator);
                Object value = convert(v, toValueType, beanOperator);
                map.put(key, value);
            });
        } else {
            BeanDescriptor sourceDescriptor = beanOperator.resolve(from);
            sourceDescriptor.getPropertyDescriptors().forEach((name, descriptor) -> {
                if (!descriptor.isReadable()) {
                    return;
                }
                @Nullable Object sourceValue = descriptor.getValue(from);
                Object key = convert(name, toKeyType, beanOperator);
                @Nullable Object value = sourceValue == null ? null : convert(sourceValue, toValueType, beanOperator);
                map.put(key, value);
            });
        }
        return MapHelper.immutableMap(map);
    }

    private Object convertToList(Object from, Type toElementType, BeanOperator beanOperator) {
        if (from.getClass().isArray()) {
            Object[] array = (Object[]) from;
            return ListHelper.immutableList(
                    Arrays.stream(array)
                            .map(o -> o == null ? null : convert(o, toElementType, beanOperator))
                            .collect(Collectors.toList())
            );
        }
        if (Iterable.class.isAssignableFrom(from.getClass())) {
            Iterable<Object> iterable = (Iterable<Object>) from;
            return ListHelper.immutableList(
                    IterableHelper.asList(iterable)
                            .stream()
                            .map(o -> o == null ? null : convert(o, toElementType, beanOperator))
                            .collect(Collectors.toList())
            );
        }
        throw new UnsupportedOperationException(FastFormat.format(
                "Cannot convert object {} to list of element type {}", from, toElementType));
    }

    private Object convertToArray(Object from, Type toElementType, BeanOperator beanOperator) {
        Class<?> componentType = TypeHelper.getRawClass(toElementType);
        Class<?> arrayType = ArrayHelper.findArrayType(componentType);
        if (from.getClass().isArray()) {
            return ArrayBuilder.newArray(arrayType, componentType, Array.getLength(from))
                    .setEachElement(i -> {
                        Object o = Array.get(from, i);
                        return o == null ? null : convert(o, toElementType, beanOperator);
                    })
                    .build();
        }
        if (Iterable.class.isAssignableFrom(from.getClass())) {
            Iterable<Object> iterable = (Iterable<Object>) from;
            List<Object> list = IterableHelper.asList(iterable);
            return list.stream()
                    .map(o -> o == null ? null : convert(o, toElementType, beanOperator))
                    .toArray(length -> ArrayHelper.newArray(componentType, length));
        }
        throw new UnsupportedOperationException(FastFormat.format(
                "Cannot convert object {} to array of element type {}", from, toElementType));
    }

    private Object convertToSet(Object from, Type toElementType, BeanOperator beanOperator) {
        if (from.getClass().isArray()) {
            Object[] array = (Object[]) from;
            return SetHelper.immutableSet(
                    Arrays.stream(array)
                            .map(o -> o == null ? null : convert(o, toElementType, beanOperator))
                            .collect(Collectors.toSet())
            );
        }
        if (Iterable.class.isAssignableFrom(from.getClass())) {
            Iterable<Object> iterable = (Iterable<Object>) from;
            return SetHelper.immutableSet(
                    IterableHelper.asList(iterable)
                            .stream()
                            .map(o -> o == null ? null : convert(o, toElementType, beanOperator))
                            .collect(Collectors.toSet())
            );
        }
        throw new UnsupportedOperationException(FastFormat.format(
                "Cannot convert object {} to set of element type {}", from, toElementType));
    }

    @Nullable
    private Object tryConvertToBasic(Object from, Class<?> to) {
        if (String.class.equals(to) || CharSequence.class.equals(to)) {
            return from.toString();
        }
        if (boolean.class.equals(to) || Boolean.class.equals(to)) {
            return Boolean.valueOf(from.toString());
        }
        if (int.class.equals(to) || Integer.class.equals(to)) {
            if (from instanceof Number) {
                return ((Number) from).intValue();
            }
            return Integer.valueOf(from.toString());
        }
        if (long.class.equals(to) || Long.class.equals(to)) {
            if (from instanceof Number) {
                return ((Number) from).longValue();
            }
            return Long.valueOf(from.toString());
        }
        if (char.class.equals(to) || Character.class.equals(to)) {
            if (from instanceof Number) {
                return (char) (((Number) from).intValue());
            }
            String fromString = from.toString();
            if (fromString.length() == 1) {
                return fromString.charAt(0);
            }
            throw new UnsupportedOperationException(
                    FastFormat.format("Cannot convert object {} to type {}", from, to));
        }
        if (float.class.equals(to) || Float.class.equals(to)) {
            if (from instanceof Number) {
                return ((Number) from).floatValue();
            }
            return Float.valueOf(from.toString());
        }
        if (double.class.equals(to) || Double.class.equals(to)) {
            if (from instanceof Number) {
                return ((Number) from).doubleValue();
            }
            return Double.valueOf(from.toString());
        }
        if (byte.class.equals(to) || Byte.class.equals(to)) {
            if (from instanceof Number) {
                return ((Number) from).byteValue();
            }
            return Byte.valueOf(from.toString());
        }
        if (short.class.equals(to) || Short.class.equals(to)) {
            if (from instanceof Number) {
                return ((Number) from).shortValue();
            }
            return Short.valueOf(from.toString());
        }
        if (BigDecimal.class.equals(to)) {
            if (from instanceof BigInteger) {
                return new BigDecimal((BigInteger) from);
            }
            return new BigDecimal(from.toString());
        }
        if (BigInteger.class.equals(to)) {
            return new BigInteger(from.toString());
        }
        if (Date.class.equals(to)) {
            try {
                return DateUtils.parseDate(from.toString(),
                        DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.toString());
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
        if (LocalDateTime.class.equals(to)) {
            return LocalDateTime.parse(from.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        if (Instant.class.equals(to)) {
            return Instant.parse(from.toString());
        }
        if (Duration.class.equals(to)) {
            return Duration.parse(from.toString());
        }
        return null;
    }
}
