package xyz.srclab.common.cache.threadlocal;

import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.time.TimeHelper;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;
import java.util.function.Function;

public class ThreadLocalCache<K, V> implements Cache<K, V> {

    private final ThreadLocal<WeakHashMap<K, Node<V>>> threadLocal = ThreadLocal.withInitial(WeakHashMap::new);
    private final Duration defaultExpirationPeriod;

    public ThreadLocalCache() {
        this(Duration.ZERO);
    }

    public ThreadLocalCache(Duration defaultExpirationPeriod) {
        this.defaultExpirationPeriod = defaultExpirationPeriod;
    }

    @Override
    public Duration getDefaultExpirationPeriod() {
        return defaultExpirationPeriod;
    }

    @Override
    public boolean has(K key) {
        return threadLocal.get().containsKey(key);
    }

    @Override
    public V get(K key) throws NoSuchElementException {
        Node<V> node = threadLocal.get().get(key);
        long now = TimeHelper.nowMillis();
        if (node == null || isExpired(node.getExpirationMillisAt(), now)) {
            throw new NoSuchElementException("Key: " + key);
        }
        return node.getValue();
    }

    @Override
    public V get(K key, Duration expirationPeriod, Function<K, V> ifAbsent) {
        Node<V> node = threadLocal.get().get(key);
        long now = TimeHelper.nowMillis();
        if (node == null) {
            node = new Node<>();
            node.setValue(ifAbsent.apply(key));
            node.setExpirationMillisAt(computeExpirationMillisAt(expirationPeriod, now));
            threadLocal.get().put(key, node);
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
    public void put(K key, V value, Duration expirationPeriod) {
        Node<V> node = threadLocal.get().get(key);
        if (node == null) {
            node = new Node<>();
            threadLocal.get().put(key, node);
        }
        long now = TimeHelper.nowMillis();
        node.setValue(value);
        node.setExpirationMillisAt(computeExpirationMillisAt(expirationPeriod, now));
    }

    @Override
    public void expire(K key, Duration expirationPeriod) {
        Node<V> node = threadLocal.get().get(key);
        if (node == null) {
            return;
        }
        long now = TimeHelper.nowMillis();
        node.setExpirationMillisAt(computeExpirationMillisAt(expirationPeriod, now));
    }

    @Override
    public void invalidate(K key) {
        Node<V> node = threadLocal.get().get(key);
        if (node == null) {
            return;
        }
        node.setExpirationMillisAt(-1);
    }

    @Override
    public void invalidateAll() {
        threadLocal.get().clear();
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

    private static final class Node<V> {

        private V value;
        private long expirationMillisAt;

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
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
