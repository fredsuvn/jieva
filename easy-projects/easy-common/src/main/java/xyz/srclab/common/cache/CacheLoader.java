package xyz.srclab.common.cache;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.collection.SetHelper;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author sunqian
 */
public interface CacheLoader<K, V> {

    CacheEntry<K, V> load(K key);

    @Immutable
    default Set<CacheEntry<K, V>> loadAll(Iterable<? extends K> keys) {
        Set<CacheEntry<K, V>> result = new LinkedHashSet<>();
        for (K key : keys) {
            result.add(load(key));
        }
        return SetHelper.immutable(result);
    }
}
