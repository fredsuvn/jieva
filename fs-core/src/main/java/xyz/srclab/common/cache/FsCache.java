package xyz.srclab.common.cache;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.annotations.concurrent.ThreadSafe;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
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
     * Creates a new thread-safe Cache based by {@link SoftReference}.
     */
    static <K, V> FsCache<K, V> softCache() {
        return new FsCacheImpl<>(true);
    }

    /**
     * Creates a new thread-safe Cache based by {@link SoftReference} with remove listener,
     * the listener will be called <b>after</b> removing from the cache.
     *
     * @param removeListener remove listener called <b>after</b> removing from the cache,
     *                       the first argument is current cache and second is the key.
     */
    static <K, V> FsCache<K, V> softCache(RemoveListener<K, V> removeListener) {
        return new FsCacheImpl<>(true, removeListener);
    }

    /**
     * Creates a new thread-safe Cache based by {@link SoftReference} with initial capacity.
     *
     * @param initialCapacity initial capacity
     */
    static <K, V> FsCache<K, V> softCache(int initialCapacity) {
        return new FsCacheImpl<>(true, initialCapacity);
    }

    /**
     * Creates a new thread-safe Cache based by {@link SoftReference} with initial capacity and remove listener,
     * the listener will be called <b>after</b> removing from the cache.
     *
     * @param initialCapacity initial capacity
     * @param removeListener  remove listener called <b>after</b> removing from the cache,
     *                        the first argument is current cache and second is the key.
     */
    static <K, V> FsCache<K, V> softCache(int initialCapacity, RemoveListener<K, V> removeListener) {
        return new FsCacheImpl<>(true, initialCapacity, removeListener);
    }

    /**
     * Creates a new thread-safe Cache based by {@link WeakReference}.
     */
    static <K, V> FsCache<K, V> weakCache() {
        return new FsCacheImpl<>(false);
    }

    /**
     * Creates a new thread-safe Cache based by {@link WeakReference} with remove listener,
     * the listener will be called <b>after</b> removing from the cache.
     *
     * @param removeListener remove listener called <b>after</b> removing from the cache,
     *                       the first argument is current cache and second is the key.
     */
    static <K, V> FsCache<K, V> weakCache(RemoveListener<K, V> removeListener) {
        return new FsCacheImpl<>(false, removeListener);
    }

    /**
     * Creates a new thread-safe Cache based by {@link WeakReference} with initial capacity.
     *
     * @param initialCapacity initial capacity
     */
    static <K, V> FsCache<K, V> weakCache(int initialCapacity) {
        return new FsCacheImpl<>(false, initialCapacity);
    }

    /**
     * Creates a new thread-safe Cache based by {@link WeakReference} with initial capacity and remove listener,
     * the listener will be called <b>after</b> removing from the cache.
     *
     * @param initialCapacity initial capacity
     * @param removeListener  remove listener called <b>after</b> removing from the cache,
     *                        the first argument is current cache and second is the key.
     */
    static <K, V> FsCache<K, V> weakCache(int initialCapacity, RemoveListener<K, V> removeListener) {
        return new FsCacheImpl<>(false, initialCapacity, removeListener);
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
    void put(K key, @Nullable V value);

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
