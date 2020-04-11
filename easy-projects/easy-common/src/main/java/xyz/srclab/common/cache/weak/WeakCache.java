package xyz.srclab.common.cache.weak;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.time.TimeHelper;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;
import java.util.function.Function;

public class WeakCache<K, V> implements Cache<K, V> {

    private final Map<K, Node<V>> weakHashMap;
    private final Duration defaultExpirationPeriod;

    public WeakCache() {
        this(Duration.ZERO, false);
    }

    public WeakCache(Duration defaultExpirationPeriod) {
        this(defaultExpirationPeriod, false);
    }

    public WeakCache(boolean isSynchronized) {
        this(Duration.ZERO, false);
    }

    public WeakCache(Duration defaultExpirationPeriod, boolean isSynchronized) {
        this.defaultExpirationPeriod = defaultExpirationPeriod;
        this.weakHashMap = isSynchronized ? Collections.synchronizedMap(new WeakHashMap<>()) : new WeakHashMap<>();
    }

    @Override
    public Duration getDefaultExpirationPeriod() {
        return defaultExpirationPeriod;
    }

    @Override
    public boolean has(K key) {
        return weakHashMap.containsKey(key)
                && !isExpired(weakHashMap.get(key).getExpirationMillisAt(), TimeHelper.nowMillis());
    }

    @Nullable
    @Override
    public V get(K key) throws NoSuchElementException {
        Node<V> node = weakHashMap.get(key);
        long now = TimeHelper.nowMillis();
        if (node == null || isExpired(node.getExpirationMillisAt(), now)) {
            throw new NoSuchElementException("Key: " + key);
        }
        return node.getValue();
    }

    @Nullable
    @Override
    public V get(K key, Duration expirationPeriod, Function<K, @Nullable V> ifAbsent) {
        Node<V> node = weakHashMap.get(key);
        long now = TimeHelper.nowMillis();
        if (node == null) {
            node = new Node<>();
            node.setValue(ifAbsent.apply(key));
            node.setExpirationMillisAt(computeExpirationMillisAt(expirationPeriod, now));
            weakHashMap.put(key, node);
            return node.getValue();
        }
        if (isExpired(node.getExpirationMillisAt(), now)) {
            node.setValue(ifAbsent.apply(key));
            node.setExpirationMillisAt(computeExpirationMillisAt(expirationPeriod, now));
            return node.getValue();
        }
        return node.getValue();
    }

    @Override
    public void put(K key, @Nullable V value, Duration expirationPeriod) {
        Node<V> node = weakHashMap.get(key);
        if (node == null) {
            node = new Node<>();
            weakHashMap.put(key, node);
        }
        long now = TimeHelper.nowMillis();
        node.setValue(value);
        node.setExpirationMillisAt(computeExpirationMillisAt(expirationPeriod, now));
    }

    @Override
    public void expire(K key, Duration expirationPeriod) {
        Node<V> node = weakHashMap.get(key);
        if (node == null) {
            return;
        }
        long now = TimeHelper.nowMillis();
        node.setExpirationMillisAt(computeExpirationMillisAt(expirationPeriod, now));
    }

    @Override
    public void invalidate(K key) {
        weakHashMap.remove(key);
    }

    @Override
    public void invalidateAll() {
        weakHashMap.clear();
    }

    private boolean isExpired(long expirationMillisAt, long comparingMillis) {
        if (expirationMillisAt == 0) {
            return false;
        }
        return expirationMillisAt < comparingMillis;
    }

    private long computeExpirationMillisAt(Duration expirationPeriod, long now) {
        if (Duration.ZERO.equals(expirationPeriod)) {
            return 0;
        }
        return expirationPeriod.toMillis() + now;
    }

    private static final class Node<@Nullable V> {

        private @Nullable V value;
        private long expirationMillisAt;

        @Nullable
        public V getValue() {
            return value;
        }

        public void setValue(@Nullable V value) {
            this.value = value;
        }

        public long getExpirationMillisAt() {
            return expirationMillisAt;
        }

        public void setExpirationMillisAt(long expirationMillisAt) {
            this.expirationMillisAt = expirationMillisAt;
        }
    }
}
