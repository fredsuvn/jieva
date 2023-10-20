package xyz.fsgek.common.cache;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.annotations.ThreadSafe;
import xyz.fsgek.common.base.GekWrapper;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Cache interface, is expected to thread-safe and supporting null value.
 *
 * @author fresduvn
 */
@ThreadSafe
public interface GekCache<K, V> {

    /**
     * Creates a new {@link GekCache} based by {@link SoftReference}.
     *
     * @param <K> key type
     * @param <V> value type
     * @return a new {@link GekCache}
     */
    static <K, V> GekCache<K, V> softCache() {
        return new ReferencedCache<>(true);
    }

    /**
     * Creates a new {@link GekCache} based by {@link SoftReference} with remove listener,
     * the listener will be called <b>after</b> removing from the cache.
     *
     * @param removeListener remove listener called <b>after</b> removing from the cache,
     *                       the first argument is current cache and second is the key.
     * @param <K>            key type
     * @param <V>            value type
     * @return a new {@link GekCache}
     */
    static <K, V> GekCache<K, V> softCache(RemoveListener<K, V> removeListener) {
        return new ReferencedCache<>(true, removeListener);
    }

    /**
     * Creates a new {@link GekCache} based by {@link SoftReference} with initial capacity.
     *
     * @param initialCapacity initial capacity
     * @param <K>             key type
     * @param <V>             value type
     * @return a new {@link GekCache}
     */
    static <K, V> GekCache<K, V> softCache(int initialCapacity) {
        return new ReferencedCache<>(true, initialCapacity, null);
    }

    /**
     * Creates a new {@link GekCache} based by {@link SoftReference} with initial capacity and remove listener,
     * the listener will be called <b>after</b> removing from the cache.
     *
     * @param initialCapacity initial capacity
     * @param removeListener  remove listener called <b>after</b> removing from the cache,
     *                        the first argument is current cache and second is the key.
     * @param <K>             key type
     * @param <V>             value type
     * @return a new {@link GekCache}
     */
    static <K, V> GekCache<K, V> softCache(int initialCapacity, RemoveListener<K, V> removeListener) {
        return new ReferencedCache<>(true, initialCapacity, removeListener);
    }

    /**
     * Creates a new {@link GekCache} based by {@link WeakReference}.
     *
     * @param <K> key type
     * @param <V> value type
     * @return a new {@link GekCache}
     */
    static <K, V> GekCache<K, V> weakCache() {
        return new ReferencedCache<>(false);
    }

    /**
     * Creates a new {@link GekCache} based by {@link WeakReference} with remove listener,
     * the listener will be called <b>after</b> removing from the cache.
     *
     * @param removeListener remove listener called <b>after</b> removing from the cache,
     *                       the first argument is current cache and second is the key.
     * @param <K>            key type
     * @param <V>            value type
     * @return a new {@link GekCache}
     */
    static <K, V> GekCache<K, V> weakCache(RemoveListener<K, V> removeListener) {
        return new ReferencedCache<>(false, removeListener);
    }

    /**
     * Creates a new {@link GekCache} based by {@link WeakReference} with initial capacity.
     *
     * @param initialCapacity initial capacity
     * @param <K>             key type
     * @param <V>             value type
     * @return a new {@link GekCache}
     */
    static <K, V> GekCache<K, V> weakCache(int initialCapacity) {
        return new ReferencedCache<>(false, initialCapacity, null);
    }

    /**
     * Creates a new {@link GekCache} based by {@link WeakReference} with initial capacity and remove listener,
     * the listener will be called <b>after</b> removing from the cache.
     *
     * @param initialCapacity initial capacity
     * @param removeListener  remove listener called <b>after</b> removing from the cache,
     *                        the first argument is current cache and second is the key.
     * @param <K>             key type
     * @param <V>             value type
     * @return a new {@link GekCache}
     */
    static <K, V> GekCache<K, V> weakCache(int initialCapacity, RemoveListener<K, V> removeListener) {
        return new ReferencedCache<>(false, initialCapacity, removeListener);
    }

    /**
     * Returns value associating with given key from this cache,
     * return null if there is no entry for given key or the value is expired or the value itself is null.
     *
     * @param key given key
     * @return value associating with given key from this cache
     */
    @Nullable
    V get(K key);

    /**
     * Returns value associating with given key from this cache.
     * If there is no entry for given key or the value is expired, a new value will be created by given loader.
     * <p>
     * This method will return null if and only if the existed value or created value itself is null.
     *
     * @param key    given key
     * @param loader given loader
     * @return value associating with given key from this cache, or created one
     */
    @Nullable
    V get(K key, Function<? super K, @Nullable ? extends V> loader);

    /**
     * Returns wrapped value associating with given key from this cache,
     * or null if there is no entry for given key or the value is expired.
     *
     * @param key given key
     * @return value associating with given key from this cache
     */
    @Nullable
    GekWrapper<V> getWrapper(K key);

    /**
     * Returns wrapped value associating with given key from this cache.
     * If there is no entry for given key or the value is expired, a new wrapped value will be created by given loader.
     * If the loader returns null, no value will be put and this method will return null.
     * <p>
     * This method will return null if and only if the loader returns null.
     *
     * @param key    given key
     * @param loader given loader
     * @return value associating with given key from this cache, or created one
     */
    @Nullable
    GekWrapper<V> getWrapper(K key, Function<? super K, @Nullable GekWrapper<? extends V>> loader);

    /**
     * Sets the value associated with given key, return old value or null if there is no old value.
     *
     * @param key   given key
     * @param value the value
     * @return old value or null
     */
    V put(K key, V value);

    /**
     * Removes the value associated with given key.
     *
     * @param key given key
     */
    void remove(K key);

    /**
     * Removes values of which key and value (first and second param) pass given predicate.
     *
     * @param predicate given predicate
     */
    void removeIf(BiPredicate<K, V> predicate);

    /**
     * Returns current size.
     *
     * @return current size
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
     * Removing listener of {@link GekCache}.
     */
    interface RemoveListener<K, V> {

        /**
         * This method will be called after removing a key and its value.
         *
         * @param cache current cache
         * @param key   key of removed value
         */
        void onRemove(GekCache<K, V> cache, K key);
    }
}
