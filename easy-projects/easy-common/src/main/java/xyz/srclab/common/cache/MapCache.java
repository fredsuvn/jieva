package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.base.Null;

import java.time.Duration;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * @author sunqian
 */
final class MapCache<K, V> implements Cache<K, V> {

    private final Map<K, Object> map;

    public MapCache(Map<K, Object> map) {
        this.map = map;
    }

    @Override
    public boolean has(K key) {
        return map.containsKey(key);
    }

    @Override
    public V get(K key) throws NoSuchElementException {
        @Nullable Object value = map.get(key);
        Checker.checkElementByKey(value != null, key);
        return unmask(value);
    }

    @Override
    public V getOrDefault(K key, @Nullable V defaultValue) {
        @Nullable Object value = map.getOrDefault(key, defaultValue);
        if (value == defaultValue) {
            return defaultValue;
        }
        if (value == null) {
            return null;
        }
        return unmask(value);
    }

    @Override
    public V getOrCompute(K key, Function<? super K, ? extends @Nullable V> ifAbsent) {
        Object result = map.computeIfAbsent(key,
                k -> {
                    @Nullable V value = ifAbsent.apply(k);
                    return value == null ? Null.asObject() : value;
                });
        return unmask(result);
    }

    @Override
    public V getOrCompute(K key, CacheFunction<? super K, ? extends @Nullable V> ifAbsent) {
        return getOrCompute(key, (Function<K, V>) k -> ifAbsent.apply(k).getValue());
    }

    @Override
    public void put(K key, @Nullable V value) {
        map.put(key, mask(value));
    }

    @Override
    public void put(K key, @Nullable V value, CacheExpiry expiry) {
        put(key, value);
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

    private Object mask(@Nullable V value) {
        return value == null ? Null.asObject() : value;
    }

    @Nullable
    private V unmask(Object object) {
        return Null.isNull(object) ? null : (V) object;
    }
}
