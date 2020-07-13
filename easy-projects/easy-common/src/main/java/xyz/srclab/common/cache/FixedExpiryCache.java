package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;

import java.time.Duration;
import java.util.function.Function;

/**
 * @author sunqian
 */
public interface FixedExpiryCache<K, V> extends Cache<K, V> {

    @Override
    default void put(K key, @Nullable V value, @Nullable CacheExpiry expiry) {
        put(key, value);
    }

    @Override
    default void expire(K key, long seconds) {
    }

    @Override
    default void expire(K key, Duration duration) {
    }

    @Override
    default void expire(K key, Function<? super K, Duration> durationFunction) {
    }

    @Override
    default void expireAll(Iterable<? extends K> keys, long seconds) {
    }

    @Override
    default void expireAll(Iterable<? extends K> keys, Duration duration) {
    }

    @Override
    default void expireAll(Iterable<? extends K> keys, Function<? super K, Duration> durationFunction) {
    }
}
