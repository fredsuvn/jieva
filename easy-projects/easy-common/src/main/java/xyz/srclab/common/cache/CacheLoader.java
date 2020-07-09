package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
public interface CacheLoader<K, V> {

    @Nullable
    default CacheValue<V> loadDetail(K key) {
        return CacheValue.of(load(key));
    }

    @Nullable
    V load(K key);
}
