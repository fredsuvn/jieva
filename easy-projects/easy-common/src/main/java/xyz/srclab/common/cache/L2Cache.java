package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;

import java.time.Duration;
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
    public V get(K key) throws NoSuchElementException {
        return l2.getOrCompute(key, k->l1.get(k));
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
    public V get(K key, CacheFunction<? super K, ? extends V> ifAbsent) {
        return null;
    }

    @Override
    public void put(K key, @Nullable V value) {

    }

    @Override
    public void put(K key, @Nullable V value, CacheExpiry expiry) {

    }

    @Override
    public void expire(K key, Duration duration) {

    }

    @Override
    public void expire(K key, Function<? super K, Duration> durationFunction) {

    }

    @Override
    public void remove(K key) {

    }

    @Override
    public void removeAll() {

    }
}
