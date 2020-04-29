package xyz.srclab.common.cache.threadlocal;

import com.google.common.cache.CacheBuilder;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.cache.weak.WeakCache;
import xyz.srclab.common.cache.weak.WeakCacheBuilder;

import java.time.Duration;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class ThreadLocalCache<K, V> implements Cache<K, V> {

    private final ThreadLocal<WeakCache<K, V>> threadLocal;

    ThreadLocalCache(Duration defaultExpirationPeriod) {
        com.google.common.cache.Cache<String,String> guavaCache = CacheBuilder.newBuilder()
                .build();
        WeakCache<String, String> weakCache = Cache.newWeakCacheBuilder()
                .build0();
        this.threadLocal = ThreadLocal.withInitial(() -> {
                    WeakCacheBuilder<K, V> weakCacheBuilder = Cache.newWeakCacheBuilder();
                    return Cache.newWeakCacheBuilder()
                            .setDefaultExpirationPeriod(defaultExpirationPeriod)
                            .build();
                }
        );
    }

    @Override
    public Duration getDefaultExpirationPeriod() {
        return threadLocal.get().getDefaultExpirationPeriod();
    }

    @Override
    public boolean has(K key) {
        return threadLocal.get().has(key);
    }

    @Override
    public boolean hasAll(K... keys) {
        return threadLocal.get().hasAll(keys);
    }

    @Override
    public boolean hasAll(Iterable<K> keys) {
        return threadLocal.get().hasAll(keys);
    }

    @Override
    public boolean hasAny(K... keys) {
        return threadLocal.get().hasAny(keys);
    }

    @Override
    public boolean hasAny(Iterable<K> keys) {
        return threadLocal.get().hasAny(keys);
    }

    @Override
    @Nullable
    public V get(K key) throws NoSuchElementException {
        return threadLocal.get().get(key);
    }

    @Override
    public Map<K, @Nullable V> getAll(K... keys) throws NoSuchElementException {
        return threadLocal.get().getIfPresent(keys);
    }

    @Override
    public Map<K, @Nullable V> getAll(Iterable<K> keys) throws NoSuchElementException {
        return threadLocal.get().getAll(keys);
    }

    @Override
    public Map<K, @Nullable V> getPresent(K... keys) throws NoSuchElementException {
        return threadLocal.get().getPresent(keys);
    }

    @Override
    public Map<K, @Nullable V> getPresent(Iterable<K> keys) throws NoSuchElementException {
        return threadLocal.get().getPresent(keys);
    }

    @Override
    @Nullable
    public V get(K key, Function<K, @Nullable V> ifAbsent) {
        return threadLocal.get().get(key, ifAbsent);
    }

    @Override
    @Nullable
    public V get(K key, long expirationPeriodSeconds, Function<K, @Nullable V> ifAbsent) {
        return threadLocal.get().get(key, expirationPeriodSeconds, ifAbsent);
    }

    @Override
    @Nullable
    public V get(K key, Duration expirationPeriod, Function<K, @Nullable V> ifAbsent) {
        return threadLocal.get().get(key, expirationPeriod, ifAbsent);
    }

    @Override
    public void put(K key, @Nullable V value) {
        threadLocal.get().put(key, value);
    }

    @Override
    public void putAll(Map<K, @Nullable V> data) {
        threadLocal.get().putAll(data);
    }

    @Override
    public void put(K key, @Nullable V value, long expirationPeriodSeconds) {
        threadLocal.get().put(key, value, expirationPeriodSeconds);
    }

    @Override
    public void putAll(Map<K, @Nullable V> data, long expirationPeriodSeconds) {
        threadLocal.get().putAll(data, expirationPeriodSeconds);
    }

    @Override
    public void put(K key, @Nullable V value, Duration expirationPeriod) {
        threadLocal.get().put(key, value, expirationPeriod);
    }

    @Override
    public void putAll(Map<K, @Nullable V> data, Duration expirationPeriod) {
        threadLocal.get().putAll(data, expirationPeriod);
    }

    @Override
    public void expire(K key) {
        threadLocal.get().expire(key);
    }

    @Override
    public void expireAll(K... keys) {
        threadLocal.get().expireAll(keys);
    }

    @Override
    public void expireAll(Iterable<K> keys) {
        threadLocal.get().expireAll(keys);
    }

    @Override
    public void expire(K key, long expirationPeriodSeconds) {
        threadLocal.get().expire(key, expirationPeriodSeconds);
    }

    @Override
    public void expireAll(long expirationPeriodSeconds, K... keys) {
        threadLocal.get().expireAll(expirationPeriodSeconds, keys);
    }

    @Override
    public void expireAll(Iterable<K> keys, long expirationSeconds) {
        threadLocal.get().expireAll(keys, expirationSeconds);
    }

    @Override
    public void expire(K key, Duration expirationPeriod) {
        threadLocal.get().expire(key, expirationPeriod);
    }

    @Override
    public void expireAll(Duration expirationPeriod, K... keys) {
        threadLocal.get().expireAll(expirationPeriod, keys);
    }

    @Override
    public void expireAll(Iterable<K> keys, Duration expirationPeriod) {
        threadLocal.get().expireAll(keys, expirationPeriod);
    }

    @Override
    public void remove(K key) {
        threadLocal.get().remove(key);
    }

    public void removeAll(K... keys) {
        threadLocal.get().removeAll(keys);
    }

    @Override
    public void removeAll(Iterable<K> keys) {
        threadLocal.get().removeAll(keys);
    }

    @Override
    public void removeAll() {
        threadLocal.get().removeAll();
    }
}
