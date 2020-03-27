package xyz.srclab.common.cache.local;

import xyz.srclab.common.cache.Cache;

import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class LocalCache<K, V> implements Cache<K, V> {

    ThreadLocal<WeakHashMap<K, V>> threadLocal = ThreadLocal.withInitial(WeakHashMap::new);

    @Override
    public boolean has(K key) {
        return threadLocal.get().containsKey(key);
    }

    @Override
    public V get(K key) {
        return threadLocal.get().get(key);
    }

    @Override
    public V get(K key, Function<K, V> ifAbsent) {
        return threadLocal.get().computeIfAbsent(key, ifAbsent);
    }

    @Override
    public void put(K key, V value) {
        threadLocal.get().put(key, value);
    }

    @Override
    public void put(K key, V value, long expirationSeconds) {
        put(key, value);
    }

    @Override
    public void put(K key, V value, long expiration, TimeUnit expirationUnit) {
        put(key, value);
    }

    @Override
    public void renew(K key) {
    }

    @Override
    public void renew(K key, long expirationSeconds) {
    }

    @Override
    public void renew(K key, long expiration, TimeUnit expirationUnit) {
    }

    @Override
    public void invalidate(K key) {
        threadLocal.get().remove(key);
    }

    @Override
    public void invalidateAll() {
        threadLocal.get().clear();
    }
}
