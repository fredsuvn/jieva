package xyz.srclab.common.cache;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Defaults;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.collection.SetKit;
import xyz.srclab.common.lang.ref.BooleanRef;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @param <K>
 * @param <V>
 * @see SimpleCache
 */
public interface Cache<K, V> extends SimpleCache<K, V> {

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
    V load(K key, CacheLoader<? super K, @Nullable ? extends V> loader);

    @Immutable
    default Map<K, @Nullable V> loadPresent(Iterable<? extends K> keys) {
        Map<K, @Nullable V> result = new LinkedHashMap<>();
        BooleanRef noResultFlag = BooleanRef.of(false);
        CacheLoader<K, @Nullable V> noResultCacheLoader = new NoResultCacheLoader<>(noResultFlag);
        for (K key : keys) {
            @Nullable V value = load(key, noResultCacheLoader);
            if (noResultFlag.get()) {
                noResultFlag.set(false);
                continue;
            }
            result.put(key, value);
        }
        return MapKit.unmodifiable(result);
    }

    @Immutable
    default Map<K, @Nullable V> loadAll(Iterable<? extends K> keys, CacheLoader<K, @Nullable ? extends V> loader) {
        Map<K, @Nullable V> present = loadPresent(keys);
        Set<K> presentsKeys = present.keySet();
        Set<K> restKeys = SetKit.filter(keys, k -> !presentsKeys.contains(k));
        if (restKeys.isEmpty()) {
            return present;
        }
        Map<K, ? extends CacheLoader.Result<@Nullable ? extends V>> restResultMap = loader.loadAll(restKeys);
        Iterable<CacheEntry<K, ? extends V>> restEntries = restResultMap.entrySet().stream()
                .filter(e -> e.getValue().needCache())
                .map(e -> e.getValue().toEntry(e.getKey()))
                .collect(Collectors.toList());
        putEntries(restEntries);
        Map<K, @Nullable V> restMap = MapKit.map(
                restResultMap,
                k -> k,
                CacheLoader.Result::value
        );
        return MapKit.merge(present, restMap);
    }

    V loadNonNull(K key, CacheLoader<? super K, ? extends V> loader) throws NoSuchElementException;

    @Immutable
    default Map<K, V> loadPresentNonNull(Iterable<? extends K> keys) {
        Map<K, V> result = new LinkedHashMap<>();
        BooleanRef noResultFlag = BooleanRef.of(false);
        CacheLoader<K, V> noResultCacheLoader = new NoResultCacheLoader<>(noResultFlag);
        for (K key : keys) {
            @Nullable V value = load(key, noResultCacheLoader);
            if (noResultFlag.get()) {
                noResultFlag.set(false);
                continue;
            }
            if (value == null) {
                continue;
            }
            result.put(key, value);
        }
        return MapKit.unmodifiable(result);
    }

    @Immutable
    default Map<K, V> loadAllNonNull(Iterable<? extends K> keys, CacheLoader<K, ? extends V> loader)
            throws NoSuchElementException {
        Map<K, V> present = loadPresentNonNull(keys);
        Set<K> presentsKeys = present.keySet();
        Set<K> restKeys = SetKit.filter(keys, k -> !presentsKeys.contains(k));
        if (restKeys.isEmpty()) {
            return present;
        }
        Map<K, ? extends CacheLoader.Result<? extends V>> restResultMap = loader.loadAllNonNull(restKeys);
        Iterable<CacheEntry<K, ? extends V>> restEntries = restResultMap.entrySet().stream()
                .filter(e -> e.getValue().needCache())
                .map(e -> e.getValue().toEntry(e.getKey()))
                .collect(Collectors.toList());
        putEntries(restEntries);
        Map<K, V> restMap = MapKit.map(
                restResultMap,
                k -> k,
                CacheLoader.Result::value
        );
        return MapKit.merge(present, restMap);
    }

    void putEntry(CacheEntry<? extends K, ? extends V> entry);

    default void putEntries(Iterable<? extends CacheEntry<? extends K, ? extends V>> entries) {
        for (CacheEntry<? extends K, ? extends V> entry : entries) {
            putEntry(entry);
        }
    }

    void expire(K key, long seconds);

    void expire(K key, Duration duration);

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

    default void expireAll(Map<? extends K, Duration> expiryMap) {
        expiryMap.forEach(this::expire);
    }
}
