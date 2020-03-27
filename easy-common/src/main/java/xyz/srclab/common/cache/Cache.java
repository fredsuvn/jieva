package xyz.srclab.common.cache;

import xyz.srclab.common.tuple.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public interface Cache<K, V> {

    boolean has(K key);

    default boolean hasAll(K... keys) {
        return hasAll(Arrays.asList(keys));
    }

    default boolean hasAll(Iterable<K> keys) {
        for (K key : keys) {
            if (!has(key)) {
                return false;
            }
        }
        return true;
    }

    default boolean hasAny(K... keys) {
        return hasAll(Arrays.asList(keys));
    }

    default boolean hasAny(Iterable<K> keys) {
        for (K key : keys) {
            if (has(key)) {
                return true;
            }
        }
        return false;
    }

    V get(K key);

    default Map<K, V> getAll(K... keys) {
        return getAll(Arrays.asList(keys));
    }

    default Map<K, V> getAll(Iterable<K> keys) {
        Map<K, V> map = new HashMap<>();
        for (K key : keys) {
            map.put(key, get(key));
        }
        return map;
    }

    V get(K key, Function<K, V> ifAbsent);

    default Map<K, V> getAllPair(Pair<K, Function<K, V>>... pairs) {
        return getAllPair(Arrays.asList(pairs));
    }

    default Map<K, V> getAllPair(Iterable<Pair<K, Function<K, V>>> pairs) {
        Map<K, V> map = new HashMap<>();
        for (Pair<K, Function<K, V>> pair : pairs) {
            map.put(pair.get0(), get(pair.get0(), pair.get1()));
        }
        return map;
    }

    void put(K key, V value);

    default void putAll(Map<K, V> pairs) {
        pairs.forEach(this::put);
    }

    void put(K key, V value, long expirationSeconds);

    default void putAll(Map<K, V> pairs, long expirationSeconds) {
        pairs.forEach((k, v) -> put(k, v, expirationSeconds));
    }

    void put(K key, V value, long expiration, TimeUnit expirationUnit);

    default void putAll(Map<K, V> pairs, long expirationSeconds, TimeUnit expirationUnit) {
        pairs.forEach((k, v) -> put(k, v, expirationSeconds, expirationUnit));
    }

    void renew(K key);

    default void renewAll(K... keys) {
        renewAll(Arrays.asList(keys));
    }

    default void renewAll(Iterable<K> keys) {
        for (K key : keys) {
            renew(key);
        }
    }

    void renew(K key, long expirationSeconds);

    default void renewAll(long expirationSeconds, K... keys) {
        renewAll(Arrays.asList(keys), expirationSeconds);
    }

    default void renewAll(Iterable<K> keys, long expirationSeconds) {
        for (K key : keys) {
            renew(key, expirationSeconds);
        }
    }

    void renew(K key, long expiration, TimeUnit expirationUnit);

    default void renewAll(long expirationSeconds, TimeUnit expirationUnit, K... keys) {
        renewAll(Arrays.asList(keys), expirationSeconds, expirationUnit);
    }

    default void renewAll(Iterable<K> keys, long expirationSeconds, TimeUnit expirationUnit) {
        for (K key : keys) {
            renew(key, expirationSeconds, expirationUnit);
        }
    }

    void invalidate(K key);

    default void invalidateAll(K... keys) {
        invalidateAll(Arrays.asList(keys));
    }

    default void invalidateAll(Iterable<K> keys) {
        for (K key : keys) {
            invalidate(key);
        }
    }

    void invalidateAll();
}
