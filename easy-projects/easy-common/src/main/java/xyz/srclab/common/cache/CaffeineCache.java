package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * @author sunqian
 */
final class CaffeineCache<K,V> implements Cache<K,V>{

    private final com.github.benmanes.caffeine.cache.Cache<K,V> caffeine;

    CaffeineCache(com.github.benmanes.caffeine.cache.Cache<K, V> caffeine) {
        this.caffeine = caffeine;
    }

    @Override
    public boolean has(K key) {
        return false;
    }

    @Override
    public V get(K key) throws NoSuchElementException {
        return null;
    }

    @Override
    public V get(K key, Function<K, @Nullable V> ifAbsent) {
        return null;
    }

    @Override
    public V get(K key, CacheValueFunction<K, @Nullable V> ifAbsent) {
        return null;
    }

    @Override
    public void put(K key, @Nullable V value) {

    }

    @Override
    public void put(K key, Function<K, @Nullable V> valueFunction) {

    }

    @Override
    public void put(K key, CacheValueFunction<K, @Nullable V> valueFunction) {

    }

    @Override
    public void expire(K key, Duration duration) {

    }

    @Override
    public void expire(K key, Function<K, Duration> durationFunction) {

    }

    @Override
    public void remove(K key) {

    }

    @Override
    public void removeAll() {

    }
}
