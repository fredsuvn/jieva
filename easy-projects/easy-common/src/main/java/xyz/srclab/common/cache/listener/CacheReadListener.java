package xyz.srclab.common.cache.listener;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
public interface CacheReadListener<K, V> extends CacheListener<K, V> {

    void beforeRead(K key);

    void onHit(K key, @Nullable V value);

    void onMiss(K key);
}
