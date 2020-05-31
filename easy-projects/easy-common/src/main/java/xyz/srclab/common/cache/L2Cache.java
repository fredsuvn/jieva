package xyz.srclab.common.cache;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;

import java.time.Duration;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

final class L2Cache<K, V> implements Cache<K, V> {

    private final Cache<K, V> l1;
    private final Cache<K, V> l2;

    L2Cache(Cache<K, V> l1, Cache<K, V> l2) {
        this.l1 = l1;
        this.l2 = l2;
    }

    @Override
    public boolean contains(K key) {
        return l2.contains(key) || l1.contains(key);
    }

    @Override
    public V get(K key) {
        return l2.get(key, l1.get(key));
    }

    @Override
    public V get(K key, @Nullable V defaultValue) {
        return null;
    }

    @Override
    public V get(K key, Function<? super K, ? extends V> ifAbsent) {
        return null;
    }

    @Override
    public V load(K key, CacheLoader<? super K, ? extends V> loader) {
        return null;
    }

    @Override
    public @Immutable Map<K, V> getPresent(Iterable<? extends K> keys) {
        return null;
    }

    @Override
    public @Immutable Map<K, V> getAll(Iterable<? extends K> keys, Function<? super K, ? extends V> ifAbsent) {
        return null;
    }

    @Override
    public @Immutable Map<K, V> loadAll(Iterable<? extends K> keys, CacheLoader<? super K, ? extends V> loader) {
        return null;
    }

    @Override
    public V getNonNull(K key) throws NullPointerException {
        return null;
    }

    @Override
    public V getNonNull(K key, Function<? super K, ? extends V> ifAbsent) throws NullPointerException {
        return null;
    }

    @Override
    public V loadNonNull(K key, CacheLoader<? super K, ? extends V> loader) throws NullPointerException {
        return null;
    }

    @Override
    public void put(K key, @Nullable V value) {

    }

    @Override
    public void put(CacheEntry<? extends K, ? extends V> entry) {

    }

    @Override
    public void putAll(Map<K, ? extends V> entries) {

    }

    @Override
    public void putAll(Iterable<? extends CacheEntry<? extends K, ? extends V>> cacheEntries) {

    }

    @Override
    public void expire(K key, long seconds) {

    }

    @Override
    public void expire(K key, Duration duration) {

    }

    @Override
    public void expire(K key, Function<? super K, Duration> durationFunction) {

    }

    @Override
    public void expireAll(Iterable<? extends K> keys, long seconds) {

    }

    @Override
    public void expireAll(Iterable<? extends K> keys, Duration duration) {

    }

    @Override
    public void expireAll(Iterable<? extends K> keys, Function<? super K, Duration> durationFunction) {

    }

    @Override
    public void invalidate(K key) {

    }

    @Override
    public void invalidateAll(Iterable<? extends K> keys) {

    }

    @Override
    public void invalidateAll() {

    }
}
