package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.cache.weak.WeakCacheBuilder;
import xyz.srclab.common.collection.map.MapHelper;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

public interface Cache<K, V> {

    static <K, V> WeakCacheBuilder<K, V> newWeakCacheBuilder() {
        return new WeakCacheBuilder<>();
    }

    boolean has(K key);

    default boolean hasAll(Iterable<K> keys) {
        for (K key : keys) {
            if (!has(key)) {
                return false;
            }
        }
        return true;
    }

    default boolean hasAny(Iterable<K> keys) {
        for (K key : keys) {
            if (has(key)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    V getIfPresent(K key) throws NoSuchElementException;

    default Map<K, @Nullable V> getIfPresent(Iterable<K> keys) throws NoSuchElementException {
        Map<K, V> map = new LinkedHashMap<>();
        for (K key : keys) {
            map.put(key, getIfPresent(key));
        }
        return MapHelper.immutable(map);
    }

    default Map<K, @Nullable V> getPresent(Iterable<K> keys) throws NoSuchElementException {
        Map<K, V> map = new LinkedHashMap<>();
        for (K key : keys) {
            try {
                map.put(key, getIfPresent(key));
            } catch (NoSuchElementException ignored) {
            }
        }
        return MapHelper.immutable(map);
    }

    @Nullable
    default V getIfPresent(K key, Function<K, @Nullable V> ifAbsent) {
        return getIfPresent(key, getDefaultExpirationPeriod(), ifAbsent);
    }

    @Nullable
    default V getIfPresent(K key, long expirationPeriodSeconds, Function<K, @Nullable V> ifAbsent) {
        return getIfPresent(key, Duration.ofSeconds(expirationPeriodSeconds), ifAbsent);
    }

    @Nullable
    V getIfPresent(K key, Duration expirationPeriod, Function<K, @Nullable V> ifAbsent);

    default V getNonNull(K key) throws NoSuchElementException, NullPointerException {
        @Nullable V result = getIfPresent(key);
        if (result == null) {
            throw new NullPointerException();
        }
        return result;
    }

    default V getNonNull(K key, Function<K, V> ifAbsent) {
        return getNonNull(key, getDefaultExpirationPeriod(), ifAbsent);
    }

    default V getNonNull(K key, long expirationPeriodSeconds, Function<K, V> ifAbsent) {
        return getNonNull(key, Duration.ofSeconds(expirationPeriodSeconds), ifAbsent);
    }

    default V getNonNull(K key, Duration expirationPeriod, Function<K, V> ifAbsent) {
        @Nullable V result = getIfPresent(key, expirationPeriod, ifAbsent);
        if (result == null) {
            throw new NullPointerException();
        }
        return result;
    }

    default void put(K key, @Nullable V value) {
        put(key, value, getDefaultExpirationPeriod());
    }

    default void putAll(Map<K, @Nullable V> data) {
        data.forEach(this::put);
    }

    default void put(K key, @Nullable V value, long expirationPeriodSeconds) {
        put(key, value, Duration.ofSeconds(expirationPeriodSeconds));
    }

    default void putAll(Map<K, @Nullable V> data, long expirationPeriodSeconds) {
        putAll(data, Duration.ofSeconds(expirationPeriodSeconds));
    }

    void put(K key, @Nullable V value, Duration expirationPeriod);

    default void putAll(Map<K, @Nullable V> data, Duration expirationPeriod) {
        data.forEach((k, v) -> put(k, v, expirationPeriod));
    }

    default void expire(K key) {
        expire(key, getDefaultExpirationPeriod());
    }

    default void expireAll(K... keys) {
        expireAll(Arrays.asList(keys));
    }

    default void expireAll(Iterable<K> keys) {
        for (K key : keys) {
            expire(key);
        }
    }

    default void expire(K key, long expirationPeriodSeconds) {
        expire(key, Duration.ofSeconds(expirationPeriodSeconds));
    }

    default void expireAll(long expirationPeriodSeconds, K... keys) {
        expireAll(Duration.ofSeconds(expirationPeriodSeconds), keys);
    }

    default void expireAll(Iterable<K> keys, long expirationPeriodSeconds) {
        expireAll(keys, Duration.ofSeconds(expirationPeriodSeconds));
    }

    void expire(K key, Duration expirationPeriod);

    default void expireAll(Duration expirationPeriod, K... keys) {
        expireAll(Arrays.asList(keys), expirationPeriod);
    }

    default void expireAll(Iterable<K> keys, Duration expirationPeriod) {
        for (K key : keys) {
            expire(key, expirationPeriod);
        }
    }

    void invalidate(K key);

    default void invalidateAll(K... keys) {
        invalidateAll(Arrays.asList(keys));
    }

    default void invalidateAll(Iterable<K> keys) {
        for (K key : keys) {
            invalidate(key);
        }
    }

    void invalidateAll();
}
