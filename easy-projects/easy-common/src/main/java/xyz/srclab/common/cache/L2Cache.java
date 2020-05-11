package xyz.srclab.common.cache;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.IterableHelper;
import xyz.srclab.common.collection.MapHelper;
import xyz.srclab.common.lang.Ref;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;

final class L2Cache<K, V> implements Cache<K, V> {

    private final Cache<K, V> l1;
    private final Cache<K, V> l2;

    L2Cache(Cache<K, V> l1, Cache<K, V> l2) {
        this.l1 = l1;
        this.l2 = l2;
    }

    @Override
    public boolean has(K key) {
        return l2.has(key) || l1.has(key);
    }

    @Override
    public boolean hasAll(Iterable<K> keys) {
        if (l2.hasAll(keys)) {
            return true;
        }
        Set<K> keySet = IterableHelper.asSet(keys);
        int size = keySet.size();
        Map<K, @Nullable V> l2Present = l2.getPresent(keys);
        keySet.removeAll(l2Present.keySet());
        Map<K, @Nullable V> l1Present = l1.getPresent(keySet);
        return l2Present.size() + l1Present.size() >= size;
    }

    @Override
    public boolean hasAny(Iterable<K> keys) {
        return l2.hasAny(keys) || l1.hasAll(keys);
    }

    @Override
    public V get(K key) throws NoSuchElementException {
        return l2.get(key, l1::get);
    }

    @Override
    public @Nullable Ref<V> getIfPresent(K key) {
        @Nullable Ref<V> ref = l2.getIfPresent(key);
        if (ref != null) {
            return ref;
        }
        ref = l1.getIfPresent(key);
        if (ref == null) {
            return null;
        }
        l2.put(key, ref.get());
        return ref;
    }

    @Override
    public V get(K key, Function<K, @Nullable V> ifAbsent) {
        return l2.get(key, k -> l1.get(key, ifAbsent));
    }

    @Override
    public V get(K key, CacheValueFunction<K, @Nullable V> ifAbsent) {
        return get(key, (Function<K, @Nullable V>) ifAbsent);
    }

    @Override
    public @Immutable Map<K, V> getAll(Iterable<K> keys) throws NoSuchElementException {
        return l2.getAll(keys, l1::get);
    }

    @Override
    public @Immutable Map<K, V> getAll(Iterable<K> keys, Function<K, @Nullable V> ifAbsent) {
        return l2.getAll(keys, k -> l1.get(k, ifAbsent));
    }

    @Override
    public @Immutable Map<K, V> getAll(Iterable<K> keys, CacheValueFunction<K, @Nullable V> ifAbsent) {
        return getAll(keys, (Function<K, @Nullable V>) ifAbsent);
    }

    @Override
    public @Immutable Map<K, V> getPresent(Iterable<K> keys) {
        Set<K> keySet = IterableHelper.asSet(keys);
        int size = keySet.size();
        Map<K, @Nullable V> l2Present = l2.getPresent(keys);
        if (l2Present.size() == size) {
            return l2Present;
        }
        Map<K, @Nullable V> result = new HashMap<>(l2Present);
        keySet.removeAll(l2Present.keySet());
        Map<K, @Nullable V> l1Present = l1.getPresent(keySet);
        result.putAll(l1Present);
        return MapHelper.immutable(result);
    }

    @Override
    public void put(K key, @Nullable V value) {
        l2.put(key, value);
        l1.put(key, value);
    }

    @Override
    public void put(K key, Function<K, @Nullable V> valueFunction) {
        @Nullable V value = valueFunction.apply(key);
        l2.put(key, value);
        l1.put(key, value);
    }

    @Override
    public void put(K key, CacheValueFunction<K, @Nullable V> valueFunction) {
        put(key, (Function<K, @Nullable V>) valueFunction);
    }

    @Override
    public void putAll(Map<K, @Nullable V> data) {
        l2.putAll(data);
        l1.putAll(data);
    }

    @Override
    public void putAll(Iterable<K> keys, CacheValueFunction<K, @Nullable V> valueFunction) {
        putAll(keys, (Function<K, @Nullable V>) valueFunction);
    }

    @Override
    public void expire(K key, long seconds) {
        l2.expire(key, seconds);
        l1.expire(key, seconds);
    }

    @Override
    public void expire(K key, Duration duration) {
        l2.expire(key, duration);
        l1.expire(key, duration);
    }

    @Override
    public void expire(K key, Function<K, Duration> durationFunction) {
        l2.expire(key, durationFunction);
        l1.expire(key, durationFunction);
    }

    @Override
    public void expireAll(Iterable<K> keys, long seconds) {
        l2.expireAll(keys, seconds);
        l1.expireAll(keys, seconds);
    }

    @Override
    public void expireAll(Iterable<K> keys, Duration duration) {
        l2.expireAll(keys, duration);
        l1.expireAll(keys, duration);
    }

    @Override
    public void expireAll(Iterable<K> keys, Function<K, Duration> durationFunction) {
        l2.expireAll(keys, durationFunction);
        l1.expireAll(keys, durationFunction);
    }

    @Override
    public void remove(K key) {
        l2.remove(key);
        l1.remove(key);
    }

    @Override
    public void removeAll(Iterable<K> keys) {
        l2.removeAll(keys);
        l1.removeAll(keys);
    }

    @Override
    public void removeAll() {
        l2.removeAll();
        l1.removeAll();
    }
}
