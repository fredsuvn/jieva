package xyz.srclab.common.cache;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;

import java.time.Duration;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * @author sunqian
 */
final class ThreadLocalCache<K, V> implements Cache<K, V> {

    private final ThreadLocal<Cache<K, V>> threadLocal;

    ThreadLocalCache(Cache<K, V> cache) {
        this.threadLocal = ThreadLocal.withInitial(() -> cache);
    }

    private Cache<K, V> getCache() {
        return threadLocal.get();
    }

    @Override
    public boolean contains(K key) {
        return getCache().contains(key);
    }

    @Override
    public boolean containsAll(Iterable<? extends K> keys) {
        return getCache().containsAll(keys);
    }

    @Override
    public boolean containsAny(Iterable<? extends K> keys) {
        return getCache().containsAny(keys);
    }

    @Override
    @Nullable
    public V get(K key) {
        return getCache().get(key);
    }

    @Override
    @Nullable
    public V get(K key, CacheLoader<? super K, ? extends V> loader) {
        return getCache().get(key, loader);
    }

    @Override
    @Nullable
    public V getOrDefault(K key, @Nullable V defaultValue) {
        return getCache().getOrDefault(key, defaultValue);
    }

    @Override
    @Immutable
    public Map<K, @Nullable V> getPresent(Iterable<? extends K> keys) {
        return getCache().getPresent(keys);
    }

    @Override
    @Immutable
    public Map<K, @Nullable V> getAll(Iterable<? extends K> keys, CacheLoader<? super K, ? extends V> loader) {
        return getCache().getAll(keys, loader);
    }

    @Override
    public V getNonNull(K key) throws NoSuchElementException {
        return getCache().getNonNull(key);
    }

    @Override
    public V getNonNull(K key, CacheLoader<? super K, ? extends V> loader) throws NoSuchElementException {
        return getCache().getNonNull(key, loader);
    }

    @Override
    public void put(K key, @Nullable V value) {
        getCache().put(key, value);
    }

    @Override
    public void put(K key, @Nullable V value, @Nullable CacheExpiry expiry) {
        getCache().put(key, value, expiry);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> entries) {
        getCache().putAll(entries);
    }

    @Override
    public void putAll(Iterable<? extends K> keys, CacheLoader<? super K, ? extends V> loader) {
        getCache().putAll(keys, loader);
    }

    @Override
    public void expire(K key, long seconds) {
        getCache().expire(key, seconds);
    }

    @Override
    public void expire(K key, Duration duration) {
        getCache().expire(key, duration);
    }

    @Override
    public void expire(K key, Function<? super K, Duration> durationFunction) {
        getCache().expire(key, durationFunction);
    }

    @Override
    public void expireAll(Iterable<? extends K> keys, long seconds) {
        getCache().expireAll(keys, seconds);
    }

    @Override
    public void expireAll(Iterable<? extends K> keys, Duration duration) {
        getCache().expireAll(keys, duration);
    }

    @Override
    public void expireAll(Iterable<? extends K> keys, Function<? super K, Duration> durationFunction) {
        getCache().expireAll(keys, durationFunction);
    }

    @Override
    public void invalidate(K key) {
        getCache().invalidate(key);
    }

    @Override
    public void invalidateAll(Iterable<? extends K> keys) {
        getCache().invalidateAll(keys);
    }

    @Override
    public void invalidateAll() {
        getCache().invalidateAll();
    }
}
