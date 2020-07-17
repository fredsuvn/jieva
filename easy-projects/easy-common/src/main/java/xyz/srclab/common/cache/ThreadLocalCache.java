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

    private Cache<K, V> getLocalCache() {
        return threadLocal.get();
    }

    @Override
    @Nullable
    public V load(K key, CacheLoader<? super K, ? extends V> loader) {
        return getLocalCache().load(key, loader);
    }

    @Override
    @Immutable
    public Map<K, @Nullable V> loadPresent(Iterable<? extends K> keys) {
        return getLocalCache().loadPresent(keys);
    }

    @Override
    @Immutable
    public Map<K, @Nullable V> loadAll(Iterable<? extends K> keys, CacheLoader<K, ? extends V> loader) {
        return getLocalCache().loadAll(keys, loader);
    }

    @Override
    public V loadNonNull(K key, CacheLoader<? super K, ? extends V> loader) throws NoSuchElementException {
        return getLocalCache().loadNonNull(key, loader);
    }

    @Override
    @Immutable
    public Map<K, V> loadPresentNonNull(Iterable<? extends K> keys) {
        return getLocalCache().loadPresentNonNull(keys);
    }

    @Override
    @Immutable
    public Map<K, V> loadAllNonNull(Iterable<? extends K> keys, CacheLoader<K, ? extends V> loader) throws NoSuchElementException {
        return getLocalCache().loadAllNonNull(keys, loader);
    }

    @Override
    public void putEntry(CacheEntry<? extends K, ? extends V> entry) {
        getLocalCache().putEntry(entry);
    }

    @Override
    public void putEntries(Iterable<? extends CacheEntry<? extends K, ? extends V>> cacheEntries) {
        getLocalCache().putEntries(cacheEntries);
    }

    @Override
    public void expire(K key, long seconds) {
        getLocalCache().expire(key, seconds);
    }

    @Override
    public void expire(K key, Duration duration) {
        getLocalCache().expire(key, duration);
    }

    @Override
    public void expireAll(Iterable<? extends K> keys, long seconds) {
        getLocalCache().expireAll(keys, seconds);
    }

    @Override
    public void expireAll(Iterable<? extends K> keys, Duration duration) {
        getLocalCache().expireAll(keys, duration);
    }

    @Override
    public void expireAll(Map<? extends K, Duration> expiryMap) {
        getLocalCache().expireAll(expiryMap);
    }

    @Override
    @Nullable
    public V get(K key) {
        return getLocalCache().get(key);
    }

    @Override
    @Nullable
    public V get(K key, Function<? super K, ? extends V> function) {
        return getLocalCache().get(key, function);
    }

    @Override
    @Immutable
    public Map<K, @Nullable V> getPresent(Iterable<? extends K> keys) {
        return getLocalCache().getPresent(keys);
    }

    @Override
    @Immutable
    public Map<K, @Nullable V> getAll(Iterable<? extends K> keys, Function<Iterable<? extends K>, Map<K, @Nullable V>> function) {
        return getLocalCache().getAll(keys, function);
    }

    @Override
    public V getNonNull(K key) throws NoSuchElementException {
        return getLocalCache().getNonNull(key);
    }

    @Override
    public V getNonNull(K key, Function<? super K, ? extends V> function) throws NoSuchElementException {
        return getLocalCache().getNonNull(key, function);
    }

    @Override
    @Immutable
    public Map<K, V> getPresentNonNull(Iterable<? extends K> keys) {
        return getLocalCache().getPresentNonNull(keys);
    }

    @Override
    @Immutable
    public Map<K, V> getAllNonNull(Iterable<? extends K> keys, Function<Iterable<? extends K>, Map<K, V>> function) throws NoSuchElementException {
        return getLocalCache().getAllNonNull(keys, function);
    }

    @Override
    public void put(K key, @Nullable V value) {
        getLocalCache().put(key, value);
    }

    @Override
    public void putAll(Map<K, @Nullable V> entries) {
        getLocalCache().putAll(entries);
    }

    @Override
    public void invalidate(K key) {
        getLocalCache().invalidate(key);
    }

    @Override
    public void invalidateAll(Iterable<? extends K> keys) {
        getLocalCache().invalidateAll(keys);
    }

    @Override
    public void invalidateALL() {
        getLocalCache().invalidateALL();
    }
}
