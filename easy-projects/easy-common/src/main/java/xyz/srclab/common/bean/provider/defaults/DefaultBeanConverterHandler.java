package xyz.srclab.common.bean.provider.defaults;

import org.apache.commons.lang3.ArrayUtils;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayHelper;
import xyz.srclab.common.bean.BeanConverterHandler;
import xyz.srclab.common.bean.BeanOperator;
import xyz.srclab.common.collection.iterable.IterableHelper;
import xyz.srclab.common.collection.list.ListHelper;
import xyz.srclab.common.collection.map.MapHelper;
import xyz.srclab.common.collection.set.SetHelper;
import xyz.srclab.common.lang.format.fastformat.FastFormat;
import xyz.srclab.common.reflect.instance.InstanceHelper;
import xyz.srclab.common.reflect.type.TypeHelper;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.*;

final class DefaultBeanConverterHandler implements BeanConverterHandler {

    static DefaultBeanConverterHandler INSTANCE = new DefaultBeanConverterHandler();

    @Override
    public boolean supportConvert(Object from, Type to, BeanOperator beanOperator) {
        return true;
    }

    @Override
    public Object convert(Object from, Type to, BeanOperator beanOperator) {
        return to instanceof Class ?
                convertClass(from, (Class<?>) to, beanOperator)
                :
                convertType(from, to, beanOperator);
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
            return convertToArray(from, to, beanOperator);
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
        if (to instanceof GenericArrayType) {
            return convertToArray(from, to, beanOperator);
        }
        if (to instanceof ParameterizedType) {
            return convertParameterizedType(from, (ParameterizedType) to, beanOperator);
        }
        if (to instanceof TypeVariable) {
            return convertTypeVariable(from, (TypeVariable<?>) to, beanOperator);
        }
        if (to instanceof WildcardType) {
            return convertWildcardType(from, (WildcardType) to, beanOperator);
        }
        throw new IllegalArgumentException("Cannot convert object {" + from + "} to type: " + to);
    }

    private Object convertParameterizedType(
            Object from, ParameterizedType parameterizedType, BeanOperator beanOperator) {
        Class<?> rawType = TypeHelper.getRawType(parameterizedType);
        // Never reached
        // if (rawType.isArray()) {
        //     return convertToArray(from, to, beanOperator);
        // }
        if (Map.class.equals(rawType)) {
            Type[] kv = parameterizedType.getActualTypeArguments();
            return convertToMap(from, kv[0], kv[1], beanOperator);
        }
        if (List.class.equals(rawType)) {
            return convertToList(from, parameterizedType.getActualTypeArguments()[0], beanOperator);
        }
        if (Set.class.equals(rawType) || Collection.class.equals(rawType)) {
            return convertToSet(from, parameterizedType.getActualTypeArguments()[0], beanOperator);
        }
        return convertToGenericBean(from, rawType, parameterizedType.getActualTypeArguments(), beanOperator);
    }

    private Object convertWildcardType(Object from, WildcardType to, BeanOperator beanOperator) {
        Class<?> rawType = TypeHelper.getRawType(to);
        return convertClass(from, rawType, beanOperator);
    }

    private Object convertTypeVariable(Object from, TypeVariable<?> to, BeanOperator beanOperator) {
        throw new IllegalArgumentException(
                FastFormat.format("Cannot find runtime type for {}.{}",
                        to.getGenericDeclaration(), to.getName())
        );
    }

    private Map<Object, Object> convertToMap(Object from, Type keyType, Type valueType, BeanOperator beanOperator) {
        Map<Object, Object> map = new LinkedHashMap<>();
        beanOperator.populateProperties(from, map,
                (sourcePropertyName, sourcePropertyValue, keyValueSetter, beanOperator1) -> {
                    Object key = beanOperator.convert(sourcePropertyName, keyType);
                    @Nullable Object value = sourcePropertyValue == null ?
                            null : beanOperator.convert(sourcePropertyValue, valueType);
                    keyValueSetter.set(key, value);
                });
        return MapHelper.immutable(map);
    }

    private Object convertToList(Object from, Type elementType, BeanOperator beanOperator) {
        if (from.getClass().isArray()) {
            List<Object> result = new LinkedList<>();
            putCollectionFromArray(from, elementType, beanOperator, result);
            return ListHelper.immutable(result);
        }
        if (from instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>) from;
            List<Object> result = new LinkedList<>();
            for (@Nullable Object o : iterable) {
                result.add(o == null ? null : beanOperator.convert(o, elementType));
            }
            return ListHelper.immutable(result);
        }
        throw new UnsupportedOperationException(FastFormat.format(
                "Cannot convert object {} to list of element type {}", from, elementType));
    }

    private Object convertToSet(Object from, Type elementType, BeanOperator beanOperator) {
        if (from.getClass().isArray()) {
            Set<Object> result = new LinkedHashSet<>();
            putCollectionFromArray(from, elementType, beanOperator, result);
            return SetHelper.immutable(result);
        }
        if (from instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>) from;
            Set<Object> result = new LinkedHashSet<>();
            for (@Nullable Object o : iterable) {
                result.add(o == null ? null : beanOperator.convert(o, elementType));
            }
            return SetHelper.immutable(result);
        }
        throw new UnsupportedOperationException(FastFormat.format(
                "Cannot convert object {} to set of element type {}", from, elementType));
    }

    private Object convertToArray(Object from, Type arrayType, BeanOperator beanOperator) {
        Type componentType = ArrayHelper.getGenericComponentType(arrayType);
        if (from.getClass().isArray()) {
            int arrayLength = Array.getLength(from);
            return ArrayHelper.buildArray(
                    ArrayHelper.newArray(componentType, arrayLength),
                    i -> {
                        @Nullable Object fromValue = Array.get(from, i);
                        @Nullable Object toValue =
                                fromValue == null ? null : beanOperator.convert(fromValue, componentType);
                        return toValue;
                    }
            );
        }
        if (from instanceof Iterable) {
            Collection<?> collection = IterableHelper.asCollection((Iterable<?>) from);
            Object resultArray = ArrayHelper.newArray(componentType, collection.size());
            int i = 0;
            for (@Nullable Object o : collection) {
                @Nullable Object resultValue =
                        o == null ? null : beanOperator.convert(o, componentType);
                Array.set(resultArray, i, resultValue);
                i++;
            }
            return resultArray;
        }
        throw new UnsupportedOperationException(FastFormat.format(
                "Cannot convert object {} to array of element type {}", from, componentType));
    }

    private void putCollectionFromArray(
            Object array, Type elementType, BeanOperator beanOperator, Collection<?> collection) {
        int arrayLength = Array.getLength(array);
        for (int i = 0; i < arrayLength; i++) {
            @Nullable Object value = Array.get(array, i);
            collection.add(value == null ? null : beanOperator.convert(value, elementType));
        }
    }

    private Object convertToBean(Object from, Class<?> to, BeanOperator beanOperator) {
        Object toInstance = InstanceHelper.newInstance(to);
        beanOperator.copyProperties(from, toInstance);
        return toInstance;
    }

    private Object convertToGenericBean(Object from, Class<?> rawType, Type[] genericTypes, BeanOperator beanOperator) {
        TypeVariable<?>[] typeVariables = rawType.getTypeParameters();
        Object toInstance = InstanceHelper.newInstance(rawType);
        beanOperator.copyProperties(from, toInstance,
                (sourcePropertyName, sourcePropertyValue, destPropertyType, destPropertySetter, beanOperator1) -> {
                    Type destType = Object.class;
                    int index = ArrayUtils.indexOf(typeVariables, destPropertyType);
                    if (index >= 0) {
                        destType = genericTypes[index];
                    }
                    @Nullable Object destValue = sourcePropertyValue == null ?
                            null : beanOperator.convert(sourcePropertyValue, destType);
                    destPropertySetter.set(destValue);
                });
        return toInstance;
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
        if (byte.class.equals(to) || Byte.class.equals(to)) {
            if (from instanceof Number) {
                return ((Number) from).byteValue();
            }
            return Byte.valueOf(from.toString());
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
            return toDate(from);
        }
        if (LocalDateTime.class.equals(to)) {
            return toLocalDateTime(from);
        }
        if (Instant.class.equals(to)) {
            return toInstant(from);
        }
        if (Duration.class.equals(to)) {
            return toDuration(from);
        }
        if (ZonedDateTime.class.equals(to)) {
            return toZonedDateTime(from);
        }
        if (OffsetDateTime.class.equals(to)) {
            return toZonedDateTime(from).toOffsetDateTime();
        }
        return null;
    }

    private Date toDate(Object from) {
        if (from instanceof Number) {
            return new Date(((Number) from).longValue());
        }
        if (from instanceof Instant) {
            return Date.from((Instant) from);
        }
        if (from instanceof TemporalAccessor) {
            TemporalAccessor temporalAccessor = toOffsetTemporalAccessor((TemporalAccessor) from);
            return Date.from(Instant.from(temporalAccessor));
        }
        return Date.from(toZonedDateTime(from).toInstant());
    }

    private Instant toInstant(Object from) {
        if (from instanceof Number) {
            return Instant.ofEpochMilli(((Number) from).longValue());
        }
        if (from instanceof TemporalAccessor) {
            TemporalAccessor temporalAccessor = toOffsetTemporalAccessor((TemporalAccessor) from);
            return Instant.from(temporalAccessor);
        }
        return toZonedDateTimeWithoutTemporal(from).toInstant();
    }

    private ZonedDateTime toZonedDateTime(Object from) {
        if (from instanceof TemporalAccessor) {
            TemporalAccessor temporalAccessor = toOffsetTemporalAccessor((TemporalAccessor) from);
            return toZonedDateTimeWithTemporal(temporalAccessor);
        }
        return toZonedDateTimeWithoutTemporal(from);
    }

    private ZonedDateTime toZonedDateTimeWithTemporal(TemporalAccessor temporalAccessor) {
        return ZonedDateTime.from(temporalAccessor);
    }

    private ZonedDateTime toZonedDateTimeWithoutTemporal(Object from) {
        if (from instanceof Number) {
            return ZonedDateTime.ofInstant(Instant.ofEpochMilli((Long) from), ZoneId.systemDefault());
        }
        TemporalAccessor temporalAccessor = toOffsetTemporalAccessor(
                DateTimeFormatter.ISO_DATE_TIME.parse(from.toString()));
        return toZonedDateTimeWithTemporal(temporalAccessor);
    }

    private LocalDateTime toLocalDateTime(Object from) {
        if (from instanceof Number) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) from), ZoneId.systemDefault());
        }
        return LocalDateTime.parse(from.toString(), DateTimeFormatter.ISO_DATE_TIME);
    }

    private Duration toDuration(Object from) {
        if (from instanceof Number) {
            return Duration.ofMillis(((Number) from).longValue());
        }
        return Duration.parse(from.toString());
    }

    private TemporalAccessor toOffsetTemporalAccessor(TemporalAccessor temporalAccessor) {
        if (temporalAccessor.isSupported(ChronoField.OFFSET_SECONDS)) {
            return temporalAccessor;
        }
        LocalDateTime localDateTime = LocalDateTime.from(temporalAccessor);
        return localDateTime.atZone(ZoneId.systemDefault());
    }
}
