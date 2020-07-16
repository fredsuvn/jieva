package xyz.srclab.common.cache;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Check;
import xyz.srclab.common.base.Require;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.collection.SetKit;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;

/**
 * @author sunqian
 */
public interface SimpleCache<K, V> {

    @Nullable
    V get(K key);

    @Nullable
    V get(K key, Function<? super K, @Nullable ? extends V> function);

    @Immutable
    default Map<K, @Nullable V> getPresent(Iterable<? extends K> keys) {
        Map<K, @Nullable V> result = new LinkedHashMap<>();
        for (K key : keys) {
            result.put(key, get(key));
        }
        return MapKit.unmodifiable(result);
    }

    @Immutable
    default Map<K, @Nullable V> getAll(
            Iterable<? extends K> keys, Function<Iterable<? extends K>, Map<K, @Nullable V>> function) {
        Map<K, @Nullable V> present = getPresent(keys);
        Set<K> presentsKeys = present.keySet();
        Set<K> restKeys = SetKit.filter(keys, k -> !presentsKeys.contains(k));
        if (restKeys.isEmpty()) {
            return present;
        }
        Map<K, @Nullable V> restMap = function.apply(restKeys);
        putAll(restMap);
        return MapKit.merge(present, restMap);
    }

    default V getNonNull(K key) throws NoSuchElementException {
        return Require.nonNullElement(get(key), key);
    }

    default V getNonNull(K key, Function<? super K, ? extends V> function) throws NoSuchElementException {
        return Require.nonNullElement(
                get(key, k -> Require.nonNullElement(function.apply(k), k)),
                key
        );
    }

    @Immutable
    default Map<K, V> getPresentNonNull(Iterable<? extends K> keys) {
        Map<K, V> result = new LinkedHashMap<>();
        for (K key : keys) {
            @Nullable V value = get(key);
            if (value == null) {
                continue;
            }
            result.put(key, value);
        }
        return MapKit.unmodifiable(result);
    }

    @Immutable
    default Map<K, V> getAllNonNull(Iterable<? extends K> keys, Function<Iterable<? extends K>, Map<K, V>> function)
            throws NoSuchElementException {
        Map<K, V> present = getPresentNonNull(keys);
        Set<K> presentsKeys = present.keySet();
        Set<K> restKeys = SetKit.filter(keys, k -> !presentsKeys.contains(k));
        if (restKeys.isEmpty()) {
            return present;
        }
        Map<K, V> restMap = function.apply(restKeys);
        restMap.forEach((k, v) -> Check.checkElement(v != null, k));
        putAll(restMap);
        return MapKit.merge(present, restMap);
    }

    void put(K key, @Nullable V value);

    default void putAll(Map<K, @Nullable V> entries) {
        entries.forEach(this::put);
    }

    void invalidate(K key);

    default void invalidateAll(Iterable<? extends K> keys) {
        for (K key : keys) {
            invalidate(key);
        }
    }

    void invalidateALL();
}
