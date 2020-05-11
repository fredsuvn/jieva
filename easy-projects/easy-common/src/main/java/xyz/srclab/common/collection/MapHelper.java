package xyz.srclab.common.collection;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.annotation.WrittenReturn;
import xyz.srclab.common.base.Checker;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class MapHelper {

    @Nullable
    public static <V> V getFirstValue(Map<?, ? extends V> map) throws NoSuchElementException {
        Optional<? extends V> optional = map.values().stream().findFirst();
        return optional.orElse(null);
    }

    public static <V> V getFirstValueNonNull(Map<?, ? extends V> map) throws NoSuchElementException {
        @Nullable V result = getFirstValue(map);
        Checker.checkElement(result != null);
        return result;
    }

    @Immutable
    public static <NK, NV, OK, OV> Map<NK, NV> map(
            Map<OK, OV> sourceMap,
            Function<OK, ? extends NK> keyMapper,
            Function<OV, ? extends NV> valueMapper
    ) {
        Map<NK, NV> newMap = new LinkedHashMap<>();
        sourceMap.forEach((k, v) -> newMap.put(keyMapper.apply(k), valueMapper.apply(v)));
        return immutable(newMap);
    }

    public static void removeAll(@WrittenReturn Map<?, ?> map, Object... keys) {
        removeAll(map, Arrays.asList(keys));
    }

    public static void removeAll(@WrittenReturn Map<?, ?> map, Iterable<Object> keys) {
        for (Object key : keys) {
            map.remove(key);
        }
    }

    @Immutable
    public static <K, V> Map<K, V> immutable(Map<? extends K, ? extends V> map) {
        return ImmutableMap.copyOf(map);
    }

    @Immutable
    public static <K, V> Map<K, V> filter(
            Map<? extends K, ? extends V> map, Predicate<Map.Entry<? extends K, ? extends V>> predicate) {
        Map<K, V> result = new LinkedHashMap<>();
        map.entrySet().forEach(e -> {
            if (predicate.test(e)) {
                result.put(e.getKey(), e.getValue());
            }
        });
        return immutable((result));
    }

    public static <K, V> Map<K, V> newConcurrentMap(int concurrentLevel) {
        return new MapMaker().concurrencyLevel(concurrentLevel).makeMap();
    }
}
