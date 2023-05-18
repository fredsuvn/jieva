package xyz.srclab.common.cache;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.build.annotations.FsMethods;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Cache interface and static methods.
 *
 * @author fresduvn
 */
@FsMethods
public interface FsCache<T> {

    /**
     * Creates a new Cache based by SoftReference.
     */
    static <V> FsCache<V> newCache() {
        return new FsCacheImpl<>();
    }

    /**
     * Creates a new Cache based by SoftReference.
     *
     * @param removeListener Listener triggered when value expiry was detected, the first argument is the key of value.
     */
    static <V> FsCache<V> newCache(Consumer<Object> removeListener) {
        return new FsCacheImpl<>(removeListener);
    }

    /**
     * Creates a new Cache based by SoftReference.
     *
     * @param initialCapacity initial capacity
     */
    static <V> FsCache<V> newCache(int initialCapacity) {
        return new FsCacheImpl<>(initialCapacity);
    }

    /**
     * Creates a new Cache based by SoftReference.
     *
     * @param initialCapacity initial capacity
     * @param removeListener  Listener triggered when value expiry was detected, the first argument is the key of value.
     */
    static <V> FsCache<V> newCache(int initialCapacity, Consumer<Object> removeListener) {
        return new FsCacheImpl<>(initialCapacity, removeListener);
    }

    /**
     * Returns value associated with given key from this cache,
     * return null if the value is not found or if it is null itself
     * (use the {@link #getOptional(Object)} to distinguish).
     *
     * @param key given key
     */
    @Nullable T get(Object key);

    /**
     * Returns value associated with given key from this cache, return null if the value is not found.
     *
     * @param key given key
     */
    @Nullable Optional<T> getOptional(Object key);

    /**
     * Sets the value associated with given key.
     *
     * @param key   given key
     * @param value the value
     */
    void set(Object key, @Nullable T value);

    /**
     * Removes the value associated with given key.
     *
     * @param key given key
     */
    void remove(Object key);

    /**
     * Removes all values in this cache.
     */
    void clear();

    /**
     * Removes all expired values.
     */
    void cleanUp();
}
