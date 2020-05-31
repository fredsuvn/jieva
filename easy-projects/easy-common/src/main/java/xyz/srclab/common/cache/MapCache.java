package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;

import java.time.Duration;
import java.util.Map;
import java.util.function.Function;

/**
 * @author sunqian
 */
final class MapCache<K, V> implements Cache<K, V> {

    private final Map<K, V> map;

    public MapCache(Map<K, V> map) {
        this.map = map;
    }

    @Override
    public boolean contains(K key) {
        return map.containsKey(key);
    }

    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Override
    public V get(K key, @Nullable V defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    @Override
    public V get(K key, Function<? super K, ? extends V> ifAbsent) {
        return map.computeIfAbsent(key, ifAbsent);
    }

    @Override
    public V load(K key, CacheLoader<? super K, ? extends V> loader) {
        return map.computeIfAbsent(key, k -> loader.load(k).value());
    }

    @Override
    public void put(K key, @Nullable V value) {
        map.put(key, value);
    }

    @Override
    public void put(CacheEntry<? extends K, ? extends V> entry) {
        map.put(entry.key(), entry.value());
    }

    @Override
    public void expire(K key, long seconds) {
    }

    @Override
    public void expire(K key, Duration duration) {
    }

    @Override
    public void invalidate(K key) {
        map.remove(key);
    }

    @Override
    public void invalidateAll() {
        map.clear();
    }
}
