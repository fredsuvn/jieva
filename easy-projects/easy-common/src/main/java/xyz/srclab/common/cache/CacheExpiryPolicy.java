package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;

import java.time.Duration;

/**
 * @author sunqian
 */
public interface CacheExpiryPolicy<K, @Nullable V> {

    Duration getExpiryAfterCreate(K key, V value);

    Duration getExpiryAfterUpdate(K key, V value);

    Duration getExpiryAfterRead(K key, V value);
}
