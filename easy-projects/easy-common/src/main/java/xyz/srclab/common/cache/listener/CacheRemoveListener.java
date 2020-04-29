package xyz.srclab.common.cache.listener;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
public interface CacheRemoveListener<K, V> extends CacheListener<K, V> {

    void beforeRemove(K key, @Nullable V value);

    void afterRemove(K key, @Nullable V value);
}
