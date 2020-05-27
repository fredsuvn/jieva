package xyz.srclab.common.cache;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;

import java.time.Duration;
import java.util.Map;
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
    public V get(K key, @Nullable V defaultValue) {
        return getCache().get(key, defaultValue);
    }

    @Override
    @Nullable
    public V get(K key, Function<? super K, ? extends @Nullable V> ifAbsent) {
        return getCache().get(key, ifAbsent);
    }

    @Override
    @Nullable
    public V get(K key, CacheLoader<? super K, ? extends @Nullable V> loader) {
        return getCache().get(key, loader);
    }

    @Override
    @Immutable
    public Map<K, @Nullable V> getPresent(Iterable<? extends K> keys) {
        return getCache().getPresent(keys);
    }

    @Override
    @Immutable
    public Map<K, @Nullable V> getAll(Iterable<? extends K> keys, Function<? super K, ? extends @Nullable V> ifAbsent) {
        return getCache().getAll(keys, ifAbsent);
    }

    @Override
    @Immutable
    public Map<K, @Nullable V> getAll(Iterable<? extends K> keys, CacheLoader<? super K, ? extends @Nullable V> loader) {
        return getCache().getAll(keys, loader);
    }

    @Override
    public V getNonNull(K key) throws NullPointerException {
        return getCache().getNonNull(key);
    }

    @Override
    public V getNonNull(K key, Function<? super K, ? extends @Nullable V> ifAbsent) throws NullPointerException {
        return getCache().getNonNull(key, ifAbsent);
    }

    @Override
    public V getNonNull(K key, CacheLoader<? super K, ? extends @Nullable V> loader) throws NullPointerException {
        return getCache().getNonNull(key, loader);
    }

    @Override
    public void put(K key, @Nullable V value) {
        getCache().put(key, value);
    }

    @Override
    public void put(K key, CacheLoader<? super K, ? extends @Nullable V> loader) {
        getCache().put(key, loader);
    }

    @Override
    public void putAll(Map<K, ? extends @Nullable V> data) {
        getCache().putAll(data);
    }

    @Override
    public void putAll(Iterable<? extends K> keys, CacheLoader<? super K, ? extends @Nullable V> loader) {
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
    public void remove(K key) {
        getCache().remove(key);
    }

    @Override
    public void removeAll(Iterable<? extends K> keys) {
        getCache().removeAll(keys);
    }

    @Override
    public void removeAll() {
        getCache().removeAll();
    }
}
