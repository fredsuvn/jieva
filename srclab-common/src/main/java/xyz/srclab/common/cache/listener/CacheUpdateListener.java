package xyz.srclab.common.cache.listener;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
public interface CacheUpdateListener<K, V> extends CacheListener<K, V> {

    void beforeUpdate(K key, @Nullable V oldValue);

    void afterUpdate(K key, @Nullable V oldValue, @Nullable V newValue);
}
