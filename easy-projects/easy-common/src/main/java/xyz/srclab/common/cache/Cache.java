package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.map.MapHelper;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

public interface Cache<K, V> {

    static <K, V> Cache<K, V> newSimpleThreadLocal() {
        return new ThreadLocalCache<>(new SimpleCache<>(0));
    }

    static <K, V> Cache<K, V> newSimpleConcurrent() {
        return new SimpleCache<>(CacheSupport.DEFAULT_CONCURRENCY_LEVEL);
    }

    static <K, V> Cache<K, V> newSimpleConcurrent(int concurrencyLevel) {
        return new SimpleCache<>(concurrencyLevel);
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
    V get(K key) throws NoSuchElementException;

    @Nullable
    V get(K key, Function<K, @Nullable V> ifAbsent);

    @Nullable
    V get(K key, CacheValueFunction<K, @Nullable V> ifAbsent);

    default Map<K, @Nullable V> getAll(Iterable<K> keys) throws NoSuchElementException {
        Map<K, V> map = new LinkedHashMap<>();
        for (K key : keys) {
            map.put(key, get(key));
        }
        return MapHelper.immutable(map);
    }

    default Map<K, @Nullable V> getAll(Iterable<K> keys, Function<K, @Nullable V> ifAbsent) {
        Map<K, V> map = new LinkedHashMap<>();
        for (K key : keys) {
            map.put(key, get(key, ifAbsent));
        }
        return MapHelper.immutable(map);
    }

    default Map<K, @Nullable V> getAll(Iterable<K> keys, CacheValueFunction<K, @Nullable V> ifAbsent) {
        Map<K, V> map = new LinkedHashMap<>();
        for (K key : keys) {
            map.put(key, get(key, ifAbsent));
        }
        return MapHelper.immutable(map);
    }

    default Map<K, @Nullable V> getPresent(Iterable<K> keys) {
        Map<K, V> map = new LinkedHashMap<>();
        for (K key : keys) {
            try {
                map.put(key, get(key));
            } catch (NoSuchElementException ignored) {
            }
        }
        return MapHelper.immutable(map);
    }

    default V getNonNull(K key) throws NoSuchElementException, NullPointerException {
        @Nullable V result = get(key);
        if (result == null) {
            throw new NullPointerException();
        }
        return result;
    }

    default V getNonNull(K key, Function<K, @Nullable V> ifAbsent) throws NullPointerException {
        @Nullable V result = get(key, ifAbsent);
        if (result == null) {
            throw new NullPointerException();
        }
        return result;
    }

    default V getNonNull(K key, CacheValueFunction<K, @Nullable V> ifAbsent) throws NullPointerException {
        @Nullable V result = get(key, ifAbsent);
        if (result == null) {
            throw new NullPointerException();
        }
        return result;
    }

    void put(K key, @Nullable V value);

    void put(K key, Function<K, @Nullable V> valueFunction);

    void put(K key, CacheValueFunction<K, @Nullable V> valueFunction);

    default void putAll(Map<K, @Nullable V> data) {
        data.forEach(this::put);
    }

    default void putAll(Iterable<K> keys, Function<K, @Nullable V> valueFunction) {
        keys.forEach(k -> put(k, valueFunction));
    }

    default void putAll(Iterable<K> keys, CacheValueFunction<K, @Nullable V> valueFunction) {
        keys.forEach(k -> put(k, valueFunction));
    }

    default void expire(K key, long seconds) {
        expire(key, Duration.ofSeconds(seconds));
    }

    void expire(K key, Duration duration);

    void expire(K key, Function<K, Duration> durationFunction);

    default void expireAll(Iterable<K> keys, long seconds) {
        expireAll(keys, Duration.ofSeconds(seconds));
    }

    default void expireAll(Iterable<K> keys, Duration duration) {
        expireAll(keys, k -> duration);
    }

    default void expireAll(Iterable<K> keys, Function<K, Duration> durationFunction) {
        keys.forEach(k -> expire(k, durationFunction));
    }

    void remove(K key);

    default void removeAll(Iterable<K> keys) {
        for (K key : keys) {
            remove(key);
        }
    }

    void removeAll();
}
