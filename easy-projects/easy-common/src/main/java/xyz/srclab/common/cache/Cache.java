package xyz.srclab.common.cache;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.base.Defaults;
import xyz.srclab.common.collection.MapHelper;
import xyz.srclab.common.lang.Ref;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

public interface Cache<K, V> {

    static <K, V> Cache<K, V> newPermanent() {
        return newPermanent(Defaults.CONCURRENCY_LEVEL);
    }

    static <K, V> Cache<K, V> newPermanent(int concurrencyLevel) {
        Map<K, Ref<V>> map = concurrencyLevel <= 1 ?
                new HashMap<>() : MapHelper.newConcurrentMap(concurrencyLevel);
        return new MapCache<>(map);
    }

    static <K, V> Cache<K, V> newMapped(Map<K, Ref<V>> map) {
        return new MapCache<>(map);
    }

    static <K, V> Cache<K, V> newGcThreadLocal() {
        return new ThreadLocalCache<>(newMapped(CacheSupport.newGcMap(0)));
    }

    static <K, V> Cache<K, V> newGcConcurrent() {
        return newGcConcurrent(Defaults.CONCURRENCY_LEVEL);
    }

    static <K, V> Cache<K, V> newGcConcurrent(int concurrencyLevel) {
        return new MapCache<>(CacheSupport.newGcMap(concurrencyLevel));
    }

    static <K, V> Cache<K, V> newL2Cache(Cache<K, V> l1, Cache<K, V> l2) {
        return new L2Cache<>(l1, l2);
    }

    static <K, V> Cache<K, V> newGcThreadLocalL2() {
        return newGcThreadLocalL2(Defaults.CONCURRENCY_LEVEL);
    }

    static <K, V> Cache<K, V> newGcThreadLocalL2(int concurrencyLevel) {
        Cache<K, V> l1 = newGcConcurrent(concurrencyLevel);
        return newGcThreadLocalL2(l1);
    }

    static <K, V> Cache<K, V> newGcThreadLocalL2(Cache<K, V> l1) {
        Cache<K, V> l2 = newGcThreadLocal();
        return newL2Cache(l1, l2);
    }

    static <K, V> CacheBuilder<K, V> newBuilder() {
        return new CacheBuilder<>();
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
    Ref<V> getIfPresent(K key);

    @Nullable
    V get(K key, Function<K, @Nullable V> ifAbsent);

    @Nullable
    V get(K key, CacheValueFunction<K, @Nullable V> ifAbsent);

    @Immutable
    default Map<K, @Nullable V> getAll(Iterable<K> keys) throws NoSuchElementException {
        Map<K, V> map = new LinkedHashMap<>();
        for (K key : keys) {
            map.put(key, get(key));
        }
        return MapHelper.immutable(map);
    }

    @Immutable
    default Map<K, @Nullable V> getAll(Iterable<K> keys, Function<K, @Nullable V> ifAbsent) {
        Map<K, V> map = new LinkedHashMap<>();
        for (K key : keys) {
            map.put(key, get(key, ifAbsent));
        }
        return MapHelper.immutable(map);
    }

    @Immutable
    default Map<K, @Nullable V> getAll(Iterable<K> keys, CacheValueFunction<K, @Nullable V> ifAbsent) {
        Map<K, V> map = new LinkedHashMap<>();
        for (K key : keys) {
            map.put(key, get(key, ifAbsent));
        }
        return MapHelper.immutable(map);
    }

    @Immutable
    default Map<K, @Nullable V> getPresent(Iterable<K> keys) {
        Map<K, V> map = new LinkedHashMap<>();
        for (K key : keys) {
            @Nullable Ref<V> ref = getIfPresent(key);
            if (ref != null) {
                map.put(key, ref.get());
            }
        }
        return MapHelper.immutable(map);
    }

    default V getNonNull(K key) throws NoSuchElementException, NullPointerException {
        @Nullable V result = get(key);
        Checker.checkNull(result != null);
        return result;
    }

    default V getNonNull(K key, Function<K, @Nullable V> ifAbsent) throws NullPointerException {
        @Nullable V result = get(key, ifAbsent);
        Checker.checkNull(result != null);
        return result;
    }

    default V getNonNull(K key, CacheValueFunction<K, @Nullable V> ifAbsent) throws NullPointerException {
        @Nullable V result = get(key, ifAbsent);
        Checker.checkNull(result != null);
        return result;
    }

    void put(K key, @Nullable V value);

    void put(K key, Function<K, @Nullable V> valueFunction);

    void put(K key, CacheValueFunction<K, @Nullable V> valueFunction);

    default void putAll(Map<K, @Nullable V> data) {
        data.forEach(this::put);
    }

    default void putAll(Iterable<K> keys, Function<K, @Nullable V> valueFunction) {
        Map<K, @Nullable V> data = new LinkedHashMap<>();
        for (K key : keys) {
            data.put(key, valueFunction.apply(key));
        }
        putAll(data);
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
