package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;

import java.time.Duration;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * @author sunqian
 */
final class SimpleCache<K, V> implements Cache<K, V> {

    private final Map<K, ValueWrapper<V>> gcMap;

    public SimpleCache(int concurrencyLevel) {
        this.gcMap = CacheSupport.newGcMap(concurrencyLevel);
    }

    @Override
    public boolean has(K key) {
        return gcMap.containsKey(key);
    }

    @Override
    public V get(K key) throws NoSuchElementException {
        @Nullable ValueWrapper<V> valueWrapper = gcMap.get(key);
        Checker.checkElementByKey(valueWrapper != null, key);
        return valueWrapper.getValue();
    }

    @Override
    public V get(K key, Function<K, @Nullable V> ifAbsent) {
        return gcMap.computeIfAbsent(key,
                k -> new ValueWrapper<>(ifAbsent.apply(k))).getValue();
    }

    @Override
    public V get(K key, CacheValueFunction<K, @Nullable V> ifAbsent) {
        return get(key, (Function<K, @Nullable V>) ifAbsent);
    }

    @Override
    public void put(K key, @Nullable V value) {
        gcMap.put(key, new ValueWrapper<>(value));
    }

    @Override
    public void put(K key, Function<K, @Nullable V> valueFunction) {
        gcMap.put(key, new ValueWrapper<>(valueFunction.apply(key)));
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

    private static final class ValueWrapper<V> {

        private final @Nullable V value;

        private ValueWrapper(@Nullable V value) {
            this.value = value;
        }

        @Nullable
        public V getValue() {
            return value;
        }
    }
}
