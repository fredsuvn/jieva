package xyz.srclab.common.cache;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Null;
import xyz.srclab.common.collection.MapHelper;

import java.time.Duration;
import java.util.LinkedHashMap;
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
    public V get(K key, Function<? super K, ? extends @Nullable V> ifAbsent) {
        return map.computeIfAbsent(key, ifAbsent);
    }

    @Override
    public V get(K key, CacheLoader<? super K, ? extends V> loader) {
        return get(key, loader::load);
    }

    @Override
    public @Immutable Map<K, V> getPresent(Iterable<? extends K> keys) {
        Map<K, V> result = new LinkedHashMap<>();
        Map<K, Object> _map = (Map<K, Object>) map;
        for (K key : keys) {
            @Nullable Object value = _map.getOrDefault(key, Null.asObject());
            if (value != null && Null.isNull(value)) {
                continue;
            }
            result.put(key, (V) value);
        }
        return MapHelper.immutable(result);
    }

    @Override
    public void put(K key, @Nullable V value) {
        map.put(key, value);
    }

    @Override
    public void put(K key, CacheLoader<? super K, ? extends V> loader) {
        put(key, loader.load(key));
    }

    @Override
    public void expire(K key, Duration duration) {
    }

    @Override
    public void expire(K key, Function<? super K, Duration> durationFunction) {
    }

    @Override
    public void remove(K key) {
        map.remove(key);
    }

    @Override
    public void removeAll() {
        map.clear();
    }
}
