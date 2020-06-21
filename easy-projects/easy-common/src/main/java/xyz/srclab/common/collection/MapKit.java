package xyz.srclab.common.collection;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.annotation.Out;
import xyz.srclab.common.base.Cast;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class MapKit {

    @Immutable
    public static <NK, NV, OK, OV> Map<NK, NV> map(
            Map<? extends OK, ? extends OV> map,
            Function<? super OK, ? extends NK> keyMapper,
            Function<? super OV, ? extends NV> valueMapper
    ) {
        Map<NK, NV> newMap = new LinkedHashMap<>();
        map.forEach((k, v) -> newMap.put(keyMapper.apply(k), valueMapper.apply(v)));
        return immutable(newMap);
    }

    @Immutable
    public static <K, V> Map<K, V> filter(
            Map<? extends K, ? extends V> map, BiPredicate<? super K, ? super V> predicate) {
        Map<K, V> result = new LinkedHashMap<>();
        map.forEach((k, v) -> {
            if (predicate.test(k, v)) {
                result.put(k, v);
            }
        });
        return immutable(result);
    }

    public static void removeAll(@Out Map<?, ?> map, Object... keys) {
        removeAll(map, Arrays.asList(keys));
    }

    public static void removeAll(@Out Map<?, ?> map, Iterable<?> keys) {
        for (Object key : keys) {
            map.remove(key);
        }
    }

    public static <K, V> void removeIf(@Out Map<K, V> map, BiPredicate<? super K, ? super V> predicate) {
        List<Object> removed = new LinkedList<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (predicate.test(entry.getKey(), entry.getValue())) {
                removed.add(entry.getKey());
            }
        }
        removeAll(map, removed);
    }

    @Immutable
    public static <K, V> Map<K, V> immutable(Map<? extends K, ? extends V> map) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(map));
    }

    public static <K, V> Map.Entry<K, V> firstEntry(Map<K, V> map) throws NoSuchElementException {
        return map.entrySet().iterator().next();
    }

    @Nullable
    public static <V> V firstValue(Map<?, ? extends V> map) throws NoSuchElementException {
        return map.entrySet().iterator().next().getValue();
    }

    public static <V> V firstValueNonNull(Map<?, ? extends V> map) throws NoSuchElementException {
        return Objects.requireNonNull(firstValue(map));
    }

    @Immutable
    public static <K, V> Map<K, V> keyToMap(
            Iterable<? extends K> keys, Function<? super K, ? extends V> mapper) {
        Map<K, V> result = new LinkedHashMap<>();
        for (K key : keys) {
            result.put(key, mapper.apply(key));
        }
        return immutable(result);
    }

    @Immutable
    public static <K, V> Map<K, V> valueToMap(
            Iterable<? extends V> values, Function<? super V, ? extends K> mapper) {
        Map<K, V> result = new LinkedHashMap<>();
        for (V value : values) {
            result.put(mapper.apply(value), value);
        }
        return immutable(result);
    }

    @Immutable
    public static <K, V> Map<K, V> pairToMap(
            Iterable<?> elements,
            Function<Object, ? extends K> keyMapper,
            Function<Object, ? extends V> valueMapper) {
        Map<K, V> result = new LinkedHashMap<>();
        Iterator<?> iterator = elements.iterator();
        while (iterator.hasNext()) {
            Object k = iterator.next();
            K key = keyMapper.apply(k);
            if (iterator.hasNext()) {
                Object v = iterator.next();
                V value = valueMapper.apply(v);
                result.put(key, value);
            } else {
                result.put(key, null);
            }
        }
        return immutable(result);
    }

    @Immutable
    public static <K, V> Map<K, V> pairToMap(Object... elements) {
        Map<K, V> result = new LinkedHashMap<>();
        for (int i = 0; i < elements.length; ) {
            K key = Cast.as(elements[i]);
            i++;
            if (i < elements.length) {
                V value = Cast.nullable(elements[i]);
                result.put(key, value);
            } else {
                result.put(key, null);
            }
        }
        return immutable(result);
    }
}
