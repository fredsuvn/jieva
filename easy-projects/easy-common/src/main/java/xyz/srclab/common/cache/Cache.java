package xyz.srclab.common.cache;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Defaults;
import xyz.srclab.common.base.Require;
import xyz.srclab.common.collection.IterableKit;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.lang.ref.BooleanRef;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Cache<K, V> {

    static <K, V> Cache<K, V> commonCache() {
        return commonCache(Defaults.CONCURRENCY_LEVEL);
    }

    static <K, V> Cache<K, V> commonCache(int concurrentLevel) {
        return CommonCacheSupport.newCommonCache(concurrentLevel);
    }

    static <K, V> Cache<K, V> loadingCache(CacheLoader<? super K, ? extends V> cacheLoader) {
        return loadingCache(Defaults.CONCURRENCY_LEVEL, cacheLoader);
    }

    static <K, V> Cache<K, V> loadingCache(int concurrentLevel, CacheLoader<? super K, ? extends V> cacheLoader) {
        return CommonCacheSupport.newLoadingCache(concurrentLevel, cacheLoader);
    }

    static <K, V> Cache<K, V> mapCache(Map<K, V> map) {
        return MapCacheSupport.newMapCache(map);
    }

    static <K, V> Cache<K, V> mapCache(Supplier<Map<K, V>> mapSupplier) {
        return MapCacheSupport.newMapCache(mapSupplier);
    }

    static <K, V> Cache<K, V> loadingMapCache(
            Map<K, V> map, CacheLoader<? super K, ? extends V> cacheLoader) {
        return MapCacheSupport.newLoadingMapCache(map, cacheLoader);
    }

    static <K, V> Cache<K, V> loadingMapCache(
            Supplier<Map<K, V>> mapSupplier, CacheLoader<? super K, ? extends V> cacheLoader) {
        return MapCacheSupport.newLoadingMapCache(mapSupplier, cacheLoader);
    }

    static <K, V> Cache<K, V> caffeineCache(
            com.github.benmanes.caffeine.cache.Cache<K, Object> caffeineCache) {
        return CaffeineCacheSupport.newCaffeineCache(caffeineCache);
    }

    static <K, V> Cache<K, V> loadingCaffeineCache(
            com.github.benmanes.caffeine.cache.LoadingCache<K, Object> loadingCache) {
        return CaffeineCacheSupport.newLoadingCaffeineCache(loadingCache);
    }

    static <K, V> Cache<K, V> guavaCache(
            com.google.common.cache.Cache<K, Object> guavaCache) {
        return GuavaCacheSupport.newGuavaCache(guavaCache);
    }

    static <K, V> Cache<K, V> loadingGuavaCache(
            com.google.common.cache.LoadingCache<K, Object> loadingCache) {
        return GuavaCacheSupport.newLoadingGuavaCache(loadingCache);
    }

    static <K, V> Cache<K, V> threadLocal(Cache<K, V> cache) {
        return new ThreadLocalCache<>(cache);
    }

    static <K, V> CacheBuilder<K, V> newBuilder() {
        return new CacheBuilder<>();
    }

    @Nullable
    V get(K key);

    @Nullable
    V get(K key, CacheLoader<? super K, @Nullable ? extends V> loader);

    @Immutable
    default Map<K, @Nullable V> getPresent(Iterable<? extends K> keys) {
        Map<K, @Nullable V> result = new LinkedHashMap<>();
        BooleanRef resultFlag = BooleanRef.of(true);
        CacheLoader<K, @Nullable V> cacheLoader = new NoResultCacheLoader<>(resultFlag);
        for (K key : keys) {
            @Nullable V value = get(key, cacheLoader);
            if (!resultFlag.get()) {
                resultFlag.set(true);
                continue;
            }
            result.put(key, value);
        }
        return MapKit.unmodifiable(result);
    }

    @Immutable
    default Map<K, @Nullable V> getAll(
            Iterable<? extends K> keys, CacheLoader<? super K, @Nullable ? extends V> loader) {
        Map<K, @Nullable V> present = getPresent(keys);
        Map<K, @Nullable V> result = new LinkedHashMap<>(present);
        Set<K> presentKeys = present.keySet();
        Iterable<K> rest = IterableKit.filter(keys, k-> !presentKeys.contains(k));
        loader.loadAll(rest);
        return MapKit.unmodifiable(result);
    }

    default V getNonNull(K key) throws NoSuchElementException {
        return Require.nonNullElement(get(key));
    }

    V getNonNull(K key, CacheLoader<? super K, ? extends V> loader) throws NoSuchElementException;

    @Immutable
    default Map<K, V> getPresentNonNull(Iterable<? extends K> keys) {
        Map<K, V> result = new LinkedHashMap<>();
        for (K key : keys) {
            @Nullable V value = get(key);
            if (value != null) {
                result.put(key, value);
            }
        }
        return MapKit.unmodifiable(result);
    }

    @Immutable
    default Map<K, V> getAllNonNull(Iterable<? extends K> keys, CacheLoader<? super K, ? extends V> loader)
            throws NoSuchElementException {
        Map<K, V> result = new LinkedHashMap<>();
        for (K key : keys) {
            result.put(key, getNonNull(key, loader));
        }
        return MapKit.unmodifiable(result);
    }

    void put(K key, @Nullable V value);

    void put(K key, @Nullable V value, @Nullable CacheExpiry expiry);

    default void putAll(Map<? extends K, @Nullable ? extends V> entries) {
        entries.forEach(this::put);
    }

    default void putAll(Iterable<? extends K> keys, CacheLoader<? super K, @Nullable ? extends V> loader) {
        for (K key : keys) {
            @Nullable CacheLoader.Result<? extends V> result = loader.load(key);
            if (result == null) {
                continue;
            }
            put(key, result.value(), result.expiry());
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
