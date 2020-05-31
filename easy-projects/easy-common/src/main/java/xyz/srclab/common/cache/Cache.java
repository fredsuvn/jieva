package xyz.srclab.common.cache;

import com.google.common.collect.MapMaker;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.base.Defaults;
import xyz.srclab.common.collection.MapHelper;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public interface Cache<K, V> {

    static <K, V> Cache<K, V> newPermanent() {
        return newPermanent(Defaults.CONCURRENCY_LEVEL);
    }

    static <K, V> Cache<K, V> newPermanent(int concurrencyLevel) {
        Map<K, Object> map = concurrencyLevel <= 1 ?
                new HashMap<>() : new MapMaker().concurrencyLevel(concurrencyLevel).makeMap();
        return new MapCache<>(map);
    }

    static <K, V> Cache<K, V> newMapped(Map<K, Object> map) {
        return new MapCache<>(map);
    }

    static <K, V> Cache<K, V> newGcThreadLocal() {
        return new ThreadLocalCache<>(newMapped(CacheKit.newGcMap(0)));
    }

    static <K, V> Cache<K, V> newGcConcurrent() {
        return newGcConcurrent(Defaults.CONCURRENCY_LEVEL);
    }

    static <K, V> Cache<K, V> newGcConcurrent(int concurrencyLevel) {
        return new MapCache<>(CacheKit.newGcMap(concurrencyLevel));
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
    V get(K key, Function<? super K, @Nullable ? extends V> ifAbsent);

    @Nullable
    V load(K key, CacheLoader<? super K, @Nullable ? extends V> loader);

    @Nullable
    V getOrDefault(K key, @Nullable V defaultValue);

    @Nullable
    V getOrDefault(K key, Function<? super K, @Nullable ? extends V> defaultFunction);

    @Immutable
    default Map<K, @Nullable V> getPresent(Iterable<? extends K> keys) {
        Map<K, V> result = new LinkedHashMap<>();
        Cache<K, Object> _this = Cast.as(this);
        Object defaultValue = Cache0.DEFAULT_VALUE;
        for (K key : keys) {
            @Nullable Object value = _this.getOrDefault(key, defaultValue);
            if (value != defaultValue) {
                result.put(key, value == null ? null : Cast.as(value));
            }
        }
        return MapHelper.immutable(result);
    }

    @Immutable
    default Map<K, @Nullable V> getAll(
            Iterable<? extends K> keys, Function<? super K, @Nullable ? extends V> ifAbsent) {
        Map<K, V> result = new LinkedHashMap<>();
        for (K key : keys) {
            result.put(key, get(key, ifAbsent));
        }
        return MapHelper.immutable(result);
    }

    @Immutable
    default Map<K, @Nullable V> loadAll(
            Iterable<? extends K> keys, CacheLoader<? super K, @Nullable ? extends V> loader) {
        Map<K, V> result = new LinkedHashMap<>();
        for (K key : keys) {
            result.put(key, load(key, loader));
        }
        return MapHelper.immutable(result);
    }

    default V getNonNull(K key) throws NullPointerException {
        @Nullable V result = get(key);
        Checker.checkNull(result != null);
        return result;
    }

    default V getNonNull(K key, Function<? super K, @Nullable ? extends V> ifAbsent) throws NullPointerException {
        @Nullable V result = get(key, ifAbsent);
        Checker.checkNull(result != null);
        return result;
    }

    default V loadNonNull(K key, CacheLoader<? super K, @Nullable ? extends V> loader) throws NullPointerException {
        @Nullable V result = load(key, loader);
        Checker.checkNull(result != null);
        return result;
    }

    void put(K key, @Nullable V value);

    void put(CacheEntry<? extends K, @Nullable ? extends V> entry);

    default void putAll(Map<K, @Nullable ? extends V> entries) {
        entries.forEach(this::put);
    }

    default void putAll(Iterable<? extends CacheEntry<? extends K, @Nullable ? extends V>> entries) {
        entries.forEach(this::put);
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
