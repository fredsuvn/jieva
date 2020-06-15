package xyz.srclab.common.collection;

import com.google.common.collect.ImmutableMap;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.annotation.Out;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class MapKit {

    @Immutable
    public static <NK, NV, OK, OV> Map<NK, NV> map(
            Map<? extends OK, ? extends OV> map,
            Function<OK, ? extends NK> keyMapper,
            Function<OV, ? extends NV> valueMapper
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
        return ImmutableMap.copyOf(map);
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
}
