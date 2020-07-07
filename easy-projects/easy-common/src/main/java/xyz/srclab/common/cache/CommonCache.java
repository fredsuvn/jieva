package xyz.srclab.common.cache;

import com.google.common.collect.MapMaker;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;

import java.time.Duration;
import java.util.WeakHashMap;
import java.util.function.Function;

final class CommonCache<K, V> implements Cache<K, V> {

    private final Cache<K, V> l1;
    private final Cache<K, V> l2;

    CommonCache(int concurrentLevel) {
        Cache<K, V> l1 = Cache.mapCache(
                new MapMaker()
                        .concurrencyLevel(concurrentLevel)
                        .weakKeys()
                        .makeMap()
        );
        Cache<K, V> l2 = Cache.threadLocal(Cache.mapCache(new WeakHashMap<>()));
        this.l1 = l1;
        this.l2 = l2;
    }

    @Override
    public boolean contains(K key) {
        return l2.contains(key) || l1.contains(key);
    }

    @Override
    public V get(K key) {
        return l2.getOrDefault(key, k -> {
            Cache<K, Object> _l1 = Cast.as(l1);
            Object defaultValue = CacheSupport.DEFAULT_VALUE;
            @Nullable Object value = _l1.getOrDefault(k, defaultValue);
            if (value == defaultValue) {
                return null;
            }
            @Nullable V v = Cast.asNullable(value);
            l2.put(k, v);
            return v;
        });
    }

    @Override
    public V get(K key, Function<? super K, ? extends V> ifAbsent) {
        return l2.get(key, k -> l1.get(k, ifAbsent));
    }

    @Override
    public V load(K key, CacheLoader<? super K, ? extends V> loader) {
        return get(key, k -> loader.load(k).value());
    }

    @Override
    public V getOrDefault(K key, @Nullable V defaultValue) {
        return l2.getOrDefault(key, k -> {
            Cache<K, Object> _l1 = Cast.as(l1);
            @Nullable Object value = _l1.getOrDefault(k, defaultValue);
            if (value == defaultValue) {
                return defaultValue;
            }
            @Nullable V v = Cast.asNullable(value);
            l2.put(k, v);
            return v;
        });
    }

    @Override
    public void put(K key, @Nullable V value) {
        l1.put(key, value);
        l2.put(key, value);
    }

    @Override
    public void put(K key, CacheValue<? extends V> entry) {
        l1.put(key, entry);
        l2.put(key, entry);
    }

    @Override
    public void expire(K key, long seconds) {
        l1.expire(key, seconds);
        l2.expire(key, seconds);
    }

    @Override
    public void expire(K key, Duration duration) {
        l1.expire(key, duration);
        l2.expire(key, duration);
    }

    @Override
    public void invalidate(K key) {
        l1.invalidate(key);
        l2.invalidate(key);
    }

    @Override
    public void invalidateAll() {
        l1.invalidateAll();
        l2.invalidateAll();
    }
}
