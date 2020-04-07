package xyz.srclab.common.collection.map;

import com.google.common.collect.ImmutableMap;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.WriteReturn;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MapHelper {

    @Immutable
    public static <TK, TV, SK, SV> Map<TK, TV> map(
            Map<SK, SV> sourceMap, Function<SK, TK> keyMapper, Function<SV, TV> valueMapper) {
        return immutable(
                sourceMap
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                e -> keyMapper.apply(e.getKey()),
                                e -> valueMapper.apply(e.getValue())
                        )))
                ;
    }

    public static <K, V> void removeAll(@WriteReturn Map<K, V> map, Iterable<? extends K> keys) {
        for (K key : keys) {
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
}
