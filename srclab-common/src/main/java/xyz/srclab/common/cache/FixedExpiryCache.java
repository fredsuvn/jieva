package xyz.srclab.common.cache;

import java.time.Duration;
import java.util.Map;

/**
 * @author sunqian
 */
public interface FixedExpiryCache<K, V> extends Cache<K, V> {

    @Override
    default void expire(K key, long seconds) {
    }

    @Override
    default void expire(K key, Duration duration) {
    }

    @Override
    default void expireAll(Iterable<? extends K> keys, long seconds) {
    }

    @Override
    default void expireAll(Iterable<? extends K> keys, Duration duration) {
    }

    @Override
    default void expireAll(Map<? extends K, Duration> expiryMap) {
    }
}
