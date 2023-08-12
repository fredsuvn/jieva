package xyz.srclab.common.cache;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.annotations.concurrent.ThreadSafe;

import java.util.Optional;
import java.util.function.Function;

/**
 * Cache interface and static methods.
 * <p>
 * Note the implementation of this FsCache is expected to thread-safe for each interface methods.
 *
 * @author fresduvn
 */
@ThreadSafe
public interface FsCache<K, V> {

    /**
     * Creates a new thread-safe Cache based by SoftReference.
     */
    static <K, V> FsCache<K, V> newCache() {
        return new FsCacheImpl<>();
    }

    /**
     * Creates a new thread-safe Cache based by SoftReference.
     *
     * @param removeListener Listener triggered when value expiry was detected, the first argument is the key of value.
     */
    static <K, V> FsCache<K, V> newCache(RemoveListener<K, V> removeListener) {
        return new FsCacheImpl<>(removeListener);
    }

    /**
     * Creates a new thread-safe Cache based by SoftReference.
     *
     * @param initialCapacity initial capacity
     */
    static <K, V> FsCache<K, V> newCache(int initialCapacity) {
        return new FsCacheImpl<>(initialCapacity);
    }

    /**
     * Creates a new thread-safe Cache based by SoftReference.
     *
     * @param initialCapacity initial capacity
     * @param removeListener  Listener triggered when value expiry was detected, the first argument is the key of value.
     */
    static <K, V> FsCache<K, V> newCache(int initialCapacity, RemoveListener<K, V> removeListener) {
        return new FsCacheImpl<>(initialCapacity, removeListener);
    }

    /**
     * Returns value associating with given key from this cache,
     * return null if the value is not found or if it is null itself
     * (use the {@link #getOptional(Object)} to distinguish).
     *
     * @param key given key
     */
    @Nullable V get(K key);

    /**
     * Returns value associating with given key from this cache.
     * If the value is not found, given loader will be called to create and cache a new value,
     * that is, the return value is never null if the result of given loader is not null
     * (but if the result of loader is null then the return value will still be null,
     * use the {@link #getOptional(Object, Function)} to distinguish).
     *
     * @param key    given key
     * @param loader given loader
     */
    @Nullable V get(K key, Function<? super K, ? extends V> loader);

    /**
     * Returns value associating with given key from this cache, the return value will be wrapped by {@link Optional}.
     * It will return null if the value is not found.
     *
     * @param key given key
     */
    @Nullable Optional<V> getOptional(K key);

    /**
     * Returns value associating with given key from this cache, the return value will be wrapped by {@link Optional}.
     * If the value is not found, given loader will be called to create and cache a new value,
     * that is, the return value is never null.
     *
     * @param key    given key
     * @param loader given loader
     */
    Optional<V> getOptional(K key, Function<? super K, ? extends V> loader);

    /**
     * Sets the value associated with given key.
     *
     * @param key   given key
     * @param value the value
     */
    void set(K key, @Nullable V value);

    /**
     * Removes the value associated with given key.
     *
     * @param key given key
     */
    void remove(K key);

    /**
     * Returns current size.
     */
    int size();

    /**
     * Removes all values in this cache.
     */
    void clear();

    /**
     * Removes all expired values.
     */
    void cleanUp();

    /**
     * Removing listener of {@link FsCache}.
     */
    interface RemoveListener<K, V> {

        /**
         * This method will be called when a removing event occurs.
         *
         * @param cache current cache
         * @param key   key of removed value
         */
        void onRemove(FsCache<K, V> cache, K key);
    }
}
