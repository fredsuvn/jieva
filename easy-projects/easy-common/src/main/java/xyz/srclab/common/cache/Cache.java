package xyz.srclab.common.cache;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Defaults;
import xyz.srclab.common.base.Require;
import xyz.srclab.common.collection.MapKit;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public interface Cache<K, V> {

    static <K, V> Cache<K, V> newCommonCache() {
        return newCommonCache(Defaults.CONCURRENCY_LEVEL);
    }

    static <K, V> Cache<K, V> newCommonCache(int concurrentLevel) {
        return new CommonCache<>(concurrentLevel);
    }

    static <K, V> Cache<K, V> newMapCache(Map<K, V> map) {
        return new MapCache<>(map);
    }

    static <K, V> Cache<K, V> toThreadLocal(Cache<K, V> cache) {
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
    V get(K key, Function<? super K, @Nullable ? extends V> ifAbsent);

    @Nullable
    V load(K key, CacheLoader<? super K, @Nullable ? extends V> loader);

    @Nullable
    V getOrDefault(K key, @Nullable V defaultValue);

    @Nullable
    default V getOrDefault(K key, Function<? super K, @Nullable ? extends V> defaultFunction) {
        Cache<K, Object> _this = Cast.as(this);
        Object defaultValue = CacheSupport.DEFAULT_VALUE;
        @Nullable Object value = _this.getOrDefault(key, defaultValue);
        if (value == defaultValue) {
            return defaultFunction.apply(key);
        }
        return Cast.asNullable(value);
    }

    @Immutable
    default Map<K, @Nullable V> getPresent(Iterable<? extends K> keys) {
        Map<K, V> result = new LinkedHashMap<>();
        Cache<K, Object> _this = Cast.as(this);
        Object defaultValue = CacheSupport.DEFAULT_VALUE;
        for (K key : keys) {
            @Nullable Object value = _this.getOrDefault(key, defaultValue);
            if (value != defaultValue) {
                result.put(key, Cast.asNullable(value));
            }
        }
        return MapKit.immutable(result);
    }

    @Immutable
    default Map<K, @Nullable V> getAll(
            Iterable<? extends K> keys, Function<? super K, @Nullable ? extends V> ifAbsent) {
        Map<K, V> result = new LinkedHashMap<>();
        for (K key : keys) {
            result.put(key, get(key, ifAbsent));
        }
        return MapKit.immutable(result);
    }

    @Immutable
    default Map<K, @Nullable V> loadAll(
            Iterable<? extends K> keys, CacheLoader<? super K, @Nullable ? extends V> loader) {
        Map<K, V> result = new LinkedHashMap<>();
        for (K key : keys) {
            result.put(key, load(key, loader));
        }
        return MapKit.immutable(result);
    }

    default V getNonNull(K key) throws NullPointerException {
        return Require.nonNull(get(key));
    }

    default V getNonNull(K key, Function<? super K, @Nullable ? extends V> ifAbsent) throws NullPointerException {
        return Require.nonNull(get(key, ifAbsent));
    }

    default V loadNonNull(K key, CacheLoader<? super K, @Nullable ? extends V> loader) throws NullPointerException {
        return Require.nonNull(load(key, loader));
    }

    void put(K key, @Nullable V value);

    void put(K key, CacheValue<@Nullable ? extends V> entry);

    default void putAll(Map<? extends K, @Nullable ? extends V> entries) {
        entries.forEach(this::put);
    }

    default void putAll(Iterable<? extends K> keys, CacheLoader<? super K, @Nullable ? extends V> loader) {
        for (K key : keys) {
            put(key, loader.load(key));
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
