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
        Map<NK, NV> result = new LinkedHashMap<>();
        map.forEach((k, v) -> result.put(keyMapper.apply(k), valueMapper.apply(v)));
        return unmodifiable(result);
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
        return unmodifiable(result);
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

    @SafeVarargs
    @Immutable
    public static <K, V> Map<K, V> merge(Map<K, V>... maps) {
        return merge(Arrays.asList(maps));
    }

    @Immutable
    public static <K, V> Map<K, V> merge(Iterable<? extends Map<K, V>> maps) {
        Map<K, V> result = new LinkedHashMap<>();
        for (Map<K, V> map : maps) {
            result.putAll(map);
        }
        return unmodifiable(result);
    }

    @Immutable
    public static <K, V> Map<K, V> immutable(Map<? extends K, ? extends V> map) {
        return ImmutableSupport.map(map);
    }

    public static <K, V> Map<K, V> unmodifiable(Map<? extends K, ? extends V> map) {
        return Collections.unmodifiableMap(map);
    }

    @Immutable
    public static <K, V> Map<K, V> empty() {
        return Collections.emptyMap();
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
        return unmodifiable(result);
    }

    @Immutable
    public static <K, V> Map<K, V> valueToMap(
            Iterable<? extends V> values, Function<? super V, ? extends K> mapper) {
        Map<K, V> result = new LinkedHashMap<>();
        for (V value : values) {
            result.put(mapper.apply(value), value);
        }
        return unmodifiable(result);
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
        return unmodifiable(result);
    }

    @SafeVarargs
    @Immutable
    public static <K, V, E> Map<K, V> pairToMap(E... elements) {
        Map<K, V> result = new LinkedHashMap<>();
        for (int i = 0; i < elements.length; ) {
            K key = Cast.as(elements[i]);
            i++;
            if (i < elements.length) {
                V value = Cast.asNullable(elements[i]);
                result.put(key, value);
            } else {
                result.put(key, null);
            }
        }
        return unmodifiable(result);
    }
}
