package xyz.srclab.common.cache;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Require;

import java.util.Map;
import java.util.function.Function;

/**
 * @author sunqian
 */
public interface SimpleCache<K, V> {

    @Nullable
    V get(K key);

    @Nullable
    V get(K key, Function<K, @Nullable V> function);

    @Immutable
    Map<K, @Nullable V> getPresent(Iterable<K> keys);

    @Immutable
    Map<K, @Nullable V> getAll(Iterable<K> keys, Function<Iterable<K>, Map<K, @Nullable V>> function);

    default V getNonNull(K key) {
        return Require.nonNullElement(get(key), key);
    }

    V getNonNull(K key, Function<K, V> function);

    @Immutable
    Map<K, V> getPresentNonNull(Iterable<K> keys);

    @Immutable
    Map<K, V> getAllNonNull(Iterable<K> keys, Function<Iterable<K>, Map<K, V>> function);

    void put(K key, @Nullable V value);

    void putAll(Map<K, @Nullable V> entries);

    void invalidate(K key);

    void invalidateAll(Iterable<K> keys);

    void invalidateALL();
}
