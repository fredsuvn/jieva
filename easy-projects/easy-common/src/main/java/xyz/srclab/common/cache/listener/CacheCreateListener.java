package xyz.srclab.common.cache.listener;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
public interface CacheCreateListener<K, V> extends CacheListener<K, V> {

    void beforeCreate(K key);

    void afterCreate(K key, @Nullable V value);
}
