package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.lang.ref.Ref;

import java.time.Duration;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * @author sunqian
 */
final class MapCache<K, V> implements Cache<K, V> {

    private final Map<K, Ref<V>> map;

    public MapCache(Map<K, Ref<V>> map) {
        this.map = map;
    }

    @Override
    public boolean has(K key) {
        return map.containsKey(key);
    }

    @Override
    public V get(K key) throws NoSuchElementException {
        @Nullable Ref<V> ref = map.get(key);
        Checker.checkElementByKey(ref != null, key);
        return ref.get();
    }

    @Override
    public @Nullable Ref<V> getIfPresent(K key) {
        return map.get(key);
    }

    @Override
    public V get(K key, Function<K, @Nullable V> ifAbsent) {
        return map.computeIfAbsent(key,
                k -> Ref.of(ifAbsent.apply(k))).get();
    }

    @Override
    public V get(K key, CacheValueFunction<K, @Nullable V> ifAbsent) {
        return get(key, (Function<K, @Nullable V>) ifAbsent);
    }

    @Override
    public void put(K key, @Nullable V value) {
        map.put(key, Ref.of(value));
    }

    @Override
    public void put(K key, Function<K, @Nullable V> valueFunction) {
        map.put(key, Ref.of(valueFunction.apply(key)));
    }

    @Override
    public void put(K key, CacheValueFunction<K, @Nullable V> valueFunction) {
        put(key, (Function<K, @Nullable V>) valueFunction);
    }

    @Override
    public void expire(K key, Duration duration) {
    }

    @Override
    public void expire(K key, Function<K, Duration> durationFunction) {
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
