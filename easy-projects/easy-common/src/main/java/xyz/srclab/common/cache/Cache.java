package xyz.srclab.common.cache;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Defaults;
import xyz.srclab.common.base.Require;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.lang.ref.BooleanRef;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

public interface Cache<K, V> {

    static <K, V> Cache<K, V> commonCache() {
        return commonCache(Defaults.CONCURRENCY_LEVEL);
    }

    static <K, V> Cache<K, V> commonCache(int concurrentLevel) {
        return new CommonCache<>(concurrentLevel);
    }

    static <K, V> Cache<K, V> mapCache(Map<K, V> map) {
        return new MapCache<>(map);
    }

    static <K, V> Cache<K, V> caffeineCache(com.github.benmanes.caffeine.cache.Cache<K, Object> caffeine) {
        return new CaffeineCache<>(caffeine);
    }

    static <K, V> Cache<K, V> guavaCache(com.google.common.cache.Cache<K, Object> guavaCache) {
        return new GuavaCache<>(guavaCache);
    }

    static <K, V> Cache<K, V> threadLocal(Cache<K, V> cache) {
        return new ThreadLocalCache<>(cache);
    }

    static <K, V> CacheBuilder<K, V> newBuilder() {
        return new CacheBuilder<>();
    }

    boolean contains(K key);

    default boolean containsAll(Iterable<? extends K> keys) {
        for (K key : keys) {
            if (!contains(key)) {
                return false;
            }
        }
        return true;
    }

    default boolean containsAny(Iterable<? extends K> keys) {
        for (K key : keys) {
            if (contains(key)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    V get(K key);

    @Nullable
    V get(K key, CacheLoader<? super K, @Nullable ? extends V> loader);

    @Nullable
    default V getOrDefault(K key, @Nullable V defaultValue) {
        BooleanRef containsFlag = BooleanRef.of(true);
        @Nullable V value = get(key, CacheSupport.newContainsCacheLoader(containsFlag));
        if (containsFlag.get()) {
            return value;
        }
        return defaultValue;
    }

    @Immutable
    default Map<K, @Nullable V> getPresent(Iterable<? extends K> keys) {
        Map<K, V> result = new LinkedHashMap<>();
        BooleanRef containsFlag = BooleanRef.of(true);
        for (K key : keys) {
            @Nullable V value = get(key, CacheSupport.newContainsCacheLoader(containsFlag));
            if (containsFlag.get()) {
                result.put(key, value);
            }
            containsFlag.set(true);
        }
        return MapKit.unmodifiable(result);
    }

    @Immutable
    default Map<K, @Nullable V> getAll(
            Iterable<? extends K> keys, CacheLoader<? super K, @Nullable ? extends V> loader) {
        Map<K, V> result = new LinkedHashMap<>();
        for (K key : keys) {
            result.put(key, get(key, loader));
        }
        return MapKit.unmodifiable(result);
    }

    default V getNonNull(K key) throws NoSuchElementException {
        return Require.nonNullElement(get(key));
    }

    default V getNonNull(K key, CacheLoader<? super K, @Nullable ? extends V> loader) throws NoSuchElementException {
        return Require.nonNullElement(get(key, loader));
    }

    void put(K key, @Nullable V value);

    void put(K key, CacheValue<@Nullable ? extends V> cacheValue);

    default void putAll(Map<? extends K, @Nullable ? extends V> entries) {
        entries.forEach(this::put);
    }

    default void putAll(Iterable<? extends K> keys, CacheLoader<? super K, @Nullable ? extends V> loader) {
        for (K key : keys) {
            @Nullable CacheValue<? extends V> cacheValue = loader.loadDetail(key);
            if (cacheValue == null) {
                continue;
            }
            put(key, cacheValue.value());
        }
    }

    void expire(K key, long seconds);

    void expire(K key, Duration duration);

    default void expire(K key, Function<? super K, Duration> durationFunction) {
        expire(key, durationFunction.apply(key));
    }

    default void expireAll(Iterable<? extends K> keys, long seconds) {
        for (K key : keys) {
            expire(key, seconds);
        }
    }

    default void expireAll(Iterable<? extends K> keys, Duration duration) {
        for (K key : keys) {
            expire(key, duration);
        }
    }

    default void expireAll(Iterable<? extends K> keys, Function<? super K, Duration> durationFunction) {
        for (K key : keys) {
            expire(key, durationFunction);
        }
    }

    void invalidate(K key);

    default void invalidateAll(Iterable<? extends K> keys) {
        for (K key : keys) {
            invalidate(key);
        }
    }

    void invalidateAll();
}
