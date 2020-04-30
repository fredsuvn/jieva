package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;

import java.time.Duration;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * @author sunqian
 */
final class SimpleCache<K, V> implements Cache<K, V> {

    private final Map<K, ValueWrapper<V>> autoCleanMap;

    public SimpleCache(int concurrencyLevel) {
        this.autoCleanMap = CacheSupport.newAutoCleanMap(concurrencyLevel);
    }

    @Override
    public boolean has(K key) {
        return autoCleanMap.containsKey(key);
    }

    @Override
    public V get(K key) throws NoSuchElementException {
        @Nullable ValueWrapper<V> valueWrapper = autoCleanMap.get(key);
        if (valueWrapper == null) {
            throw new NoSuchElementException("key: " + key);
        }
        return valueWrapper.getValue();
    }

    @Override
    public V get(K key, Function<K, @Nullable V> ifAbsent) {
        return autoCleanMap.computeIfAbsent(key,
                k -> new ValueWrapper<>(ifAbsent.apply(k))).getValue();
    }

    @Override
    public V get(K key, CacheValueFunction<K, @Nullable V> ifAbsent) {
        return get(key, (Function<K, @Nullable V>) ifAbsent);
    }

    @Override
    public void put(K key, @Nullable V value) {
        autoCleanMap.put(key, new ValueWrapper<>(value));
    }

    @Override
    public void put(K key, Function<K, @Nullable V> valueFunction) {
        autoCleanMap.put(key, new ValueWrapper<>(valueFunction.apply(key)));
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
        autoCleanMap.remove(key);
    }

    @Override
    public void removeAll() {
        autoCleanMap.clear();
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
