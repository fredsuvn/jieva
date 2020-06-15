package xyz.srclab.common.object;

import org.apache.commons.lang3.ArrayUtils;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.bean.BeanClass;
import xyz.srclab.common.bean.BeanOperator;
import xyz.srclab.common.bean.BeanProperty;
import xyz.srclab.common.collection.IterableKit;
import xyz.srclab.common.collection.ListKit;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.collection.SetKit;
import xyz.srclab.common.lang.format.Formatter;
import xyz.srclab.common.reflect.ClassKit;
import xyz.srclab.common.reflect.TypeKit;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.*;

final class DefaultConvertHandler implements ConvertHandler {

    static DefaultConvertHandler INSTANCE = new DefaultConvertHandler();

    @Override
    public boolean supportConvert(Object from, Type to, BeanOperator beanOperator) {
        return true;
    }

    @Override
    public <T> T convert(Object from, Type to, BeanOperator beanOperator) {
        Object result = to instanceof Class ?
                convertClass(from, (Class<?>) to, beanOperator)
                :
                convertType(from, to, beanOperator);
        return (T) result;
    }

    private Object convertClass(Object from, Class<?> to, BeanOperator beanOperator) {
        if (to.isAssignableFrom(from.getClass())) {
            return from;
        }
        @Nullable Object basic = tryToBasic(from, to);
        if (basic != null) {
            return basic;
        }
        if (to.isArray()) {
            return toArray(from, to, beanOperator);
        }
        if (Map.class.equals(to)) {
            return toMap(from, Object.class, Object.class, beanOperator);
        }
        if (List.class.equals(to)) {
            return toList(from, Object.class, beanOperator);
        }
        if (Set.class.equals(to) || Collection.class.equals(to)) {
            return toSet(from, Object.class, beanOperator);
        }
        return toBean(from, to, beanOperator);
    }

    private Object convertType(Object from, Type to, BeanOperator beanOperator) {
        if (to instanceof GenericArrayType) {
            return toArray(from, to, beanOperator);
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
        Class<?> rawType = TypeKit.getRawType(parameterizedType);
        // Never reached
        // if (rawType.isArray()) {
        //     return convertToArray(from, to, beanOperator);
        // }
        if (Map.class.equals(rawType)) {
            Type[] kv = parameterizedType.getActualTypeArguments();
            return toMap(from, kv[0], kv[1], beanOperator);
        }
        if (List.class.equals(rawType)) {
            return toList(from, parameterizedType.getActualTypeArguments()[0], beanOperator);
        }
        if (Set.class.equals(rawType) || Collection.class.equals(rawType)) {
            return toSet(from, parameterizedType.getActualTypeArguments()[0], beanOperator);
        }
        return toGenericBean(from, rawType, parameterizedType.getActualTypeArguments(), beanOperator);
    }

    private Object convertWildcardType(Object from, WildcardType to, BeanOperator beanOperator) {
        Class<?> rawType = TypeKit.getRawType(to);
        return convertClass(from, rawType, beanOperator);
    }

    private Object convertTypeVariable(Object from, TypeVariable<?> to, BeanOperator beanOperator) {
        throw new IllegalArgumentException(
                Formatter.fastFormat("Cannot find runtime type for {}.{}",
                        to.getGenericDeclaration(), to.getName())
        );
    }

    private Map<Object, Object> toMap(Object from, Type keyType, Type valueType, BeanOperator beanOperator) {
        Map<Object, Object> map = new LinkedHashMap<>();
        beanOperator.preparePopulateProperties(from, map)
                .mapKey(k -> beanOperator.convert(k, keyType))
                .mapValue(v -> v == null ? null : beanOperator.convert(v, valueType))
                .doPopulate();
        return MapKit.immutable(map);
    }

    private Object toList(Object from, Type elementType, BeanOperator beanOperator) {
        if (from.getClass().isArray()) {
            List<Object> result = new LinkedList<>();
            putCollectionFromArray(from, elementType, beanOperator, result);
            return ListKit.immutable(result);
        }
        if (from instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>) from;
            List<Object> result = new LinkedList<>();
            for (@Nullable Object o : iterable) {
                result.add(o == null ? null : beanOperator.convert(o, elementType));
            }
            return ListKit.immutable(result);
        }
        throw new UnsupportedOperationException(Formatter.fastFormat(
                "Cannot convert object {} to list of element type {}", from, elementType));
    }

    private Object toSet(Object from, Type elementType, BeanOperator beanOperator) {
        if (from.getClass().isArray()) {
            Set<Object> result = new LinkedHashSet<>();
            putCollectionFromArray(from, elementType, beanOperator, result);
            return SetKit.immutable(result);
        }
        if (from instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>) from;
            Set<Object> result = new LinkedHashSet<>();
            for (@Nullable Object o : iterable) {
                result.add(o == null ? null : beanOperator.convert(o, elementType));
            }
            return SetKit.immutable(result);
        }
        throw new UnsupportedOperationException(Formatter.fastFormat(
                "Cannot convert object {} to set of element type {}", from, elementType));
    }

    private void putCollectionFromArray(
            Object array, Type elementType, BeanOperator beanOperator, Collection<?> collection) {
        if (array instanceof Object[]) {
            putCollectionFromArray((Object[]) array, elementType, beanOperator, collection);
        } else if (array instanceof boolean[]) {
            putCollectionFromArray((boolean[]) array, elementType, beanOperator, collection);
        } else if (array instanceof byte[]) {
            putCollectionFromArray((byte[]) array, elementType, beanOperator, collection);
        } else if (array instanceof short[]) {
            putCollectionFromArray((short[]) array, elementType, beanOperator, collection);
        } else if (array instanceof char[]) {
            putCollectionFromArray((char[]) array, elementType, beanOperator, collection);
        } else if (array instanceof int[]) {
            putCollectionFromArray((int[]) array, elementType, beanOperator, collection);
        } else if (array instanceof long[]) {
            putCollectionFromArray((long[]) array, elementType, beanOperator, collection);
        } else if (array instanceof float[]) {
            putCollectionFromArray((float[]) array, elementType, beanOperator, collection);
        } else if (array instanceof double[]) {
            putCollectionFromArray((double[]) array, elementType, beanOperator, collection);
        } else {
            throw new IllegalStateException("Unknown array type: " + array);
        }
    }

    private void putCollectionFromArray(
            Object[] array, Type elementType, BeanOperator beanOperator, Collection<?> collection) {
        for (@Nullable Object o : array) {
            collection.add(o == null ? null : beanOperator.convert(o, elementType));
        }
    }

    private void putCollectionFromArray(
            boolean[] array, Type elementType, BeanOperator beanOperator, Collection<?> collection) {
        for (boolean o : array) {
            collection.add(beanOperator.convert(o, elementType));
        }
    }

    private void putCollectionFromArray(
            byte[] array, Type elementType, BeanOperator beanOperator, Collection<?> collection) {
        for (byte o : array) {
            collection.add(beanOperator.convert(o, elementType));
        }
    }

    private void putCollectionFromArray(
            short[] array, Type elementType, BeanOperator beanOperator, Collection<?> collection) {
        for (short o : array) {
            collection.add(beanOperator.convert(o, elementType));
        }
    }

    private void putCollectionFromArray(
            char[] array, Type elementType, BeanOperator beanOperator, Collection<?> collection) {
        for (char o : array) {
            collection.add(beanOperator.convert(o, elementType));
        }
    }

    private void putCollectionFromArray(
            int[] array, Type elementType, BeanOperator beanOperator, Collection<?> collection) {
        for (int o : array) {
            collection.add(beanOperator.convert(o, elementType));
        }
    }

    private void putCollectionFromArray(
            long[] array, Type elementType, BeanOperator beanOperator, Collection<?> collection) {
        for (long o : array) {
            collection.add(beanOperator.convert(o, elementType));
        }
    }

    private void putCollectionFromArray(
            float[] array, Type elementType, BeanOperator beanOperator, Collection<?> collection) {
        for (float o : array) {
            collection.add(beanOperator.convert(o, elementType));
        }
    }

    private void putCollectionFromArray(
            double[] array, Type elementType, BeanOperator beanOperator, Collection<?> collection) {
        for (double o : array) {
            collection.add(beanOperator.convert(o, elementType));
        }
    }

    private Object toArray(Object from, Type arrayType, BeanOperator beanOperator) {
        Type componentType = ArrayKit.getComponentType(arrayType);
        if (from.getClass().isArray()) {
            int arrayLength = Array.getLength(from);
            return ArrayKit.buildArray(
                    ArrayKit.newArray(componentType, arrayLength),
                    i -> {
                        @Nullable Object fromValue = Array.get(from, i);
                        @Nullable Object toValue =
                                fromValue == null ? null : beanOperator.convert(fromValue, componentType);
                        return toValue;
                    }
            );
        }
        if (from instanceof Iterable) {
            Collection<?> collection = IterableKit.asCollection((Iterable<?>) from);
            Object resultArray = ArrayKit.newArray(componentType, collection.size());
            int i = 0;
            for (@Nullable Object o : collection) {
                @Nullable Object resultValue =
                        o == null ? null : beanOperator.convert(o, componentType);
                Array.set(resultArray, i, resultValue);
                i++;
            }
            return resultArray;
        }
        throw new UnsupportedOperationException(Formatter.fastFormat(
                "Cannot convert object {} to array of element type {}", from, componentType));
    }

    private Object toBean(Object from, Class<?> to, BeanOperator beanOperator) {
        Object toInstance = ClassKit.newInstance(to);
        beanOperator.copyProperties(from, toInstance);
        return toInstance;
    }

    private Object toGenericBean(Object from, Class<?> rawType, Type[] genericTypes, BeanOperator beanOperator) {
        TypeVariable<?>[] typeVariables = rawType.getTypeParameters();
        Object toInstance = ClassKit.newInstance(rawType);
        BeanClass toBeanClass = beanOperator.resolveBean(rawType);
        if (from instanceof Map) {
            Map map = (Map) from;
            map.forEach((key, value) -> {
                if (key == null) {
                    return;
                }
                String propertyName = key.toString();
                if (!toBeanClass.canWriteProperty(propertyName)) {
                    return;
                }
                BeanProperty destProperty = toBeanClass.property(propertyName);
                @Nullable Object destValue = value == null ? null :
                        beanOperator.convert(value, findTargetType(
                                destProperty.genericType(), typeVariables, genericTypes));
                destProperty.setValue(toInstance, destValue);
            });
        } else {
            BeanClass fromBeanClass = beanOperator.resolveBean(from.getClass());
            fromBeanClass.readableProperties().forEach((name, property) -> {
                if (!toBeanClass.canWriteProperty(name)) {
                    return;
                }
                BeanProperty destProperty = toBeanClass.property(name);
                @Nullable Object value = property.getValue(from);
                @Nullable Object destValue = value == null ? null :
                        beanOperator.convert(value, findTargetType(
                                destProperty.genericType(), typeVariables, genericTypes));
                destProperty.setValue(toInstance, destValue);
            });
        }
        return toInstance;
    }

    private Type findTargetType(Type propertyType, TypeVariable<?>[] typeVariables, Type[] genericTypes) {
        Type destType = Object.class;
        int index = ArrayUtils.indexOf(typeVariables, propertyType);
        if (index >= 0) {
            destType = genericTypes[index];
        }
        return destType;
    }

    @Nullable
    private Object tryToBasic(Object from, Class<?> to) {
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
                    Formatter.fastFormat("Cannot convert object {} to type {}", from, to));
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
