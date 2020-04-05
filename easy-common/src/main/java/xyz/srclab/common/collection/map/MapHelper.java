package xyz.srclab.common.collection.map;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapHelper {

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

    public static <K, V> void removeAll(Map<K, V> map, Iterable<? extends K> keys) {
        for (K key : keys) {
            map.remove(key);
        }
    }

    public static <K, V> Map<K, V> immutable(Map<? extends K, ? extends V> map) {
        return ImmutableMap.copyOf(map);
    }
}
