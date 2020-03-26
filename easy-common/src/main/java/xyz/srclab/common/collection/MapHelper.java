package xyz.srclab.common.collection;

import xyz.srclab.common.collection.map.FastFixedKeysMap;

import java.util.Collection;
import java.util.Map;

public class MapHelper {

    public static <K, V> void removeAll(Map<K, V> map, Collection<K> keys) {
        for (K key : keys) {
            map.remove(key);
        }
    }

    public static <K, V> FastFixedKeysMap<K, V> fastFixedKeysMap(Map<? extends K, ? extends V> data) {
        return new FastFixedKeysMap<>(data);
    }

    public static <K, V> FastFixedKeysMap<K, V> fastFixedKeysMap(Iterable<? extends K> keys) {
        return new FastFixedKeysMap<>(keys);
    }
}
