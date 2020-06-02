package xyz.srclab.common.cache;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.collection.MapKit;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author sunqian
 */
public interface CacheLoader<K, V> {

    CacheValue<V> load(K key);

    @Immutable
    default Map<K, CacheValue<V>> loadAll(Iterable<? extends K> keys) {
        Map<K, CacheValue<V>> result = new LinkedHashMap<>();
        for (K key : keys) {
            result.put(key, load(key));
        }
        return MapKit.immutable(result);
    }
}
