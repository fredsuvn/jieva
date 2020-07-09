package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;

import java.time.Duration;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author sunqian
 */
final class MapCache<K, V> implements Cache<K, V> {

    private final Map<K, V> map;

    MapCache(Map<K, V> map) {
        this.map = map;
    }

    MapCache(Supplier<Map<K, V>> mapSupplier) {
        this.map = mapSupplier.get();
    }

    @Override
    public boolean contains(K key) {
        return map.containsKey(key);
    }

    @Nullable
    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Nullable
    @Override
    public V get(K key, CacheLoader<? super K, ? extends V> loader) {
        try {
            return map.computeIfAbsent(key, k -> {
                @Nullable CacheValue<? extends V> cacheValue = loader.loadDetail(k);
                if (cacheValue == null) {
                    throw new LoadNullException();
                }
                return cacheValue.value();
            });
        } catch (LoadNullException e) {
            return null;
        }
    }

    @Override
    public V getOrDefault(K key, @Nullable V defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    @Override
    public void put(K key, @Nullable V value) {
        map.put(key, value);
    }

    @Override
    public void put(K key, CacheValue<? extends V> entry) {
        put(key, entry.value());
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

    private static final class LoadNullException extends RuntimeException {

        public LoadNullException() {
            super(null, null, false, false);
        }
    }
}
