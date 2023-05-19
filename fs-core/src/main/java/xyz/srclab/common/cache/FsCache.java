package xyz.srclab.common.cache;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.annotations.concurrent.ThreadSafe;
import xyz.srclab.build.annotations.FsMethods;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Cache interface and static methods.
 * <p>
 * Note the implementation of this FsCache is expected to thread-safe for each interface methods.
 *
 * @author fresduvn
 */
@FsMethods
@ThreadSafe
public interface FsCache<T> {

    /**
     * Creates a new thread-safe Cache based by SoftReference.
     */
    static <V> FsCache<V> newCache() {
        return new FsCacheImpl<>();
    }

    /**
     * Creates a new thread-safe Cache based by SoftReference.
     *
     * @param removeListener Listener triggered when value expiry was detected, the first argument is the key of value.
     */
    static <V> FsCache<V> newCache(Consumer<Object> removeListener) {
        return new FsCacheImpl<>(removeListener);
    }

    /**
     * Creates a new thread-safe Cache based by SoftReference.
     *
     * @param initialCapacity initial capacity
     */
    static <V> FsCache<V> newCache(int initialCapacity) {
        return new FsCacheImpl<>(initialCapacity);
    }

    /**
     * Creates a new thread-safe Cache based by SoftReference.
     *
     * @param initialCapacity initial capacity
     * @param removeListener  Listener triggered when value expiry was detected, the first argument is the key of value.
     */
    static <V> FsCache<V> newCache(int initialCapacity, Consumer<Object> removeListener) {
        return new FsCacheImpl<>(initialCapacity, removeListener);
    }

    /**
     * Returns value associating with given key from this cache,
     * return null if the value is not found or if it is null itself
     * (use the {@link #getOptional(Object)} to distinguish).
     *
     * @param key given key
     */
    @Nullable T get(Object key);

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
    @Nullable <K> T get(K key, Function<K, T> loader);

    /**
     * Returns value associating with given key from this cache, the return value will be wrapped by {@link Optional}.
     * It will return null if the value is not found.
     *
     * @param key given key
     */
    @Nullable Optional<T> getOptional(Object key);

    /**
     * Returns value associating with given key from this cache, the return value will be wrapped by {@link Optional}.
     * If the value is not found, given loader will be called to create and cache a new value,
     * that is, the return value is never null.
     *
     * @param key    given key
     * @param loader given loader
     */
    <K> Optional<T> getOptional(K key, Function<K, T> loader);

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
