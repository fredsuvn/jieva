package xyz.srclab.common.cache.concurent;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.common.array.ArrayHelper;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.cache.weak.WeakCache;
import xyz.srclab.common.reflect.type.TypeRef;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.function.Function;

@ThreadSafe
public class ConcurrentCache<K, V> implements Cache<K, V> {

    private static final int CONCURRENCY_LEVEL = 32;

    private final Duration defaultExpirationPeriod;
    private final Cache<K, V>[] segments;

    public ConcurrentCache() {
        this(Duration.ZERO, CONCURRENCY_LEVEL);
    }

    public ConcurrentCache(Duration defaultExpirationPeriod) {
        this(defaultExpirationPeriod, CONCURRENCY_LEVEL);
    }

    public ConcurrentCache(int concurrencyLevel) {
        this(Duration.ZERO, concurrencyLevel);
    }

    public ConcurrentCache(Duration defaultExpirationPeriod, int concurrencyLevel) {
        this.defaultExpirationPeriod = defaultExpirationPeriod;
        this.segments = ArrayHelper.buildArray(
                new Cache[concurrencyLevel],
                new TypeRef<Cache<K, V>>() {},
                i -> new WeakCache<>(defaultExpirationPeriod, true)
        );
    }

    private Cache<K, V> getSegment(K key) {
        return segments[(key.hashCode() & 0x7fffffff) % segments.length];
    }

    @Override
    public Duration getDefaultExpirationPeriod() {
        return defaultExpirationPeriod;
    }

    @Override
    public boolean has(K key) {
        return getSegment(key).has(key);
    }

    @Override
    @Nullable
    public V getIfPresent(K key) throws NoSuchElementException {
        return getSegment(key).getIfPresent(key);
    }

    @Override
    @Nullable
    public V getIfPresent(K key, Function<K, @Nullable V> ifAbsent) {
        return getSegment(key).getIfPresent(key, ifAbsent);
    }

    @Override
    @Nullable
    public V getIfPresent(K key, long expirationPeriodSeconds, Function<K, @Nullable V> ifAbsent) {
        return getSegment(key).getIfPresent(key, expirationPeriodSeconds, ifAbsent);
    }

    @Override
    @Nullable
    public V getIfPresent(K key, Duration expirationPeriod, Function<K, @Nullable V> ifAbsent) {
        return getSegment(key).getIfPresent(key, expirationPeriod, ifAbsent);
    }

    @Override
    public void put(K key, @Nullable V value) {
        getSegment(key).put(key, value);
    }

    @Override
    public void put(K key, @Nullable V value, long expirationPeriodSeconds) {
        getSegment(key).put(key, value, expirationPeriodSeconds);
    }

    @Override
    public void put(K key, @Nullable V value, Duration expirationPeriod) {
        getSegment(key).put(key, value, expirationPeriod);
    }

    @Override
    public void expire(K key) {
        getSegment(key).expire(key);
    }

    @Override
    public void expire(K key, long expirationPeriodSeconds) {
        getSegment(key).expire(key, expirationPeriodSeconds);
    }

    @Override
    public void expire(K key, Duration expirationPeriod) {
        getSegment(key).expire(key, expirationPeriod);
    }

    @Override
    public void invalidate(K key) {
        getSegment(key).invalidate(key);
    }

    @Override
    public void invalidateAll() {
        for (Cache<K, V> segment : segments) {
            segment.invalidateAll();
        }
    }
}
