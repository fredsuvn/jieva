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
final class SimpleCache<K, V> implements Cache<K, V> {

    private final Map<K, Ref<V>> gcMap;

    public SimpleCache(int concurrencyLevel) {
        this.gcMap = CacheSupport.newGcMap(concurrencyLevel);
    }

    @Override
    public boolean has(K key) {
        return gcMap.containsKey(key);
    }

    @Override
    public V get(K key) throws NoSuchElementException {
        @Nullable Ref<V> ref = gcMap.get(key);
        Checker.checkElementByKey(ref != null, key);
        return ref.get();
    }

    @Override
    public @Nullable Ref<V> getIfPresent(K key) {
        return gcMap.get(key);
    }

    @Override
    public V get(K key, Function<K, @Nullable V> ifAbsent) {
        return gcMap.computeIfAbsent(key,
                k -> Ref.of(ifAbsent.apply(k))).get();
    }

    @Override
    public V get(K key, CacheValueFunction<K, @Nullable V> ifAbsent) {
        return get(key, (Function<K, @Nullable V>) ifAbsent);
    }

    @Override
    public void put(K key, @Nullable V value) {
        gcMap.put(key, Ref.of(value));
    }

    @Override
    public void put(K key, Function<K, @Nullable V> valueFunction) {
        gcMap.put(key, Ref.of(valueFunction.apply(key)));
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
        gcMap.remove(key);
    }

    @Override
    public void removeAll() {
        gcMap.clear();
    }
}
