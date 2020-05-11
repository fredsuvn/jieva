package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.lang.Ref;

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
    public boolean has(K key) {
        return getCache().has(key);
    }

    @Override
    public boolean hasAll(Iterable<K> keys) {
        return getCache().hasAll(keys);
    }

    @Override
    public boolean hasAny(Iterable<K> keys) {
        return getCache().hasAny(keys);
    }

    @Override
    @Nullable
    public V get(K key) throws NoSuchElementException {
        return getCache().get(key);
    }

    @Override
    public @Nullable Ref<V> getIfPresent(K key) {
        return getCache().getIfPresent(key);
    }

    @Override
    @Nullable
    public V get(K key, Function<K, @Nullable V> ifAbsent) {
        return getCache().get(key, ifAbsent);
    }

    @Override
    @Nullable
    public V get(K key, CacheValueFunction<K, @Nullable V> ifAbsent) {
        return getCache().get(key, ifAbsent);
    }

    @Override
    public Map<K, @Nullable V> getAll(Iterable<K> keys) throws NoSuchElementException {
        return getCache().getAll(keys);
    }

    @Override
    public Map<K, @Nullable V> getAll(Iterable<K> keys, Function<K, @Nullable V> ifAbsent) {
        return getCache().getAll(keys, ifAbsent);
    }

    @Override
    public Map<K, @Nullable V> getAll(Iterable<K> keys, CacheValueFunction<K, @Nullable V> ifAbsent) {
        return getCache().getAll(keys, ifAbsent);
    }

    @Override
    public Map<K, @Nullable V> getPresent(Iterable<K> keys) {
        return getCache().getPresent(keys);
    }

    @Override
    public V getNonNull(K key) throws NoSuchElementException, NullPointerException {
        return getCache().getNonNull(key);
    }

    @Override
    public V getNonNull(K key, Function<K, @Nullable V> ifAbsent) throws NullPointerException {
        return getCache().getNonNull(key, ifAbsent);
    }

    @Override
    public V getNonNull(K key, CacheValueFunction<K, @Nullable V> ifAbsent) throws NullPointerException {
        return getCache().getNonNull(key, ifAbsent);
    }

    @Override
    public void put(K key, @Nullable V value) {
        getCache().put(key, value);
    }

    @Override
    public void put(K key, Function<K, @Nullable V> valueFunction) {
        getCache().put(key, valueFunction);
    }

    @Override
    public void put(K key, CacheValueFunction<K, @Nullable V> valueFunction) {
        getCache().put(key, valueFunction);
    }

    @Override
    public void putAll(Map<K, @Nullable V> data) {
        getCache().putAll(data);
    }

    @Override
    public void putAll(Iterable<K> keys, Function<K, @Nullable V> valueFunction) {
        getCache().putAll(keys, valueFunction);
    }

    @Override
    public void putAll(Iterable<K> keys, CacheValueFunction<K, @Nullable V> valueFunction) {
        getCache().putAll(keys, valueFunction);
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
    public void expire(K key, Function<K, Duration> durationFunction) {
        getCache().expire(key, durationFunction);
    }

    @Override
    public void expireAll(Iterable<K> keys, long seconds) {
        getCache().expireAll(keys, seconds);
    }

    @Override
    public void expireAll(Iterable<K> keys, Duration duration) {
        getCache().expireAll(keys, duration);
    }

    @Override
    public void expireAll(Iterable<K> keys, Function<K, Duration> durationFunction) {
        getCache().expireAll(keys, durationFunction);
    }

    @Override
    public void remove(K key) {
        getCache().remove(key);
    }

    @Override
    public void removeAll(Iterable<K> keys) {
        getCache().removeAll(keys);
    }

    @Override
    public void removeAll() {
        getCache().removeAll();
    }
}
