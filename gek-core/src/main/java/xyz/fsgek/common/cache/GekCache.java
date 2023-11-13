package xyz.fsgek.common.cache;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.annotations.ThreadSafe;
import xyz.fsgek.common.base.Gek;
import xyz.fsgek.common.base.GekWrapper;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Cache interface, thread-safe and supporting null value.
 *
 * @param <K> type of key
 * @param <V> type of value
 * @author fresduvn
 */
@ThreadSafe
public interface GekCache<K, V> {

    /**
     * Returns new builder for {@link GekCache}.
     *
     * @param <K> type of key
     * @param <V> type of value
     * @return new builder for {@link GekCache}
     */
    static <K1 extends K, V1 extends V, K, V> Builder<K1, V1> newBuilder() {
        return new Builder<>();
    }

    /**
     * Creates a new {@link GekCache} based by {@link SoftReference}.
     *
     * @param <K> key type
     * @param <V> value type
     * @return a new {@link GekCache}
     */
    static <K, V> GekCache<K, V> softCache() {
        return new SimpleCache<>(true);
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
        return new SimpleCache<>(true, removeListener);
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
        return new SimpleCache<>(true, initialCapacity, null);
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
        return new SimpleCache<>(true, initialCapacity, removeListener);
    }

    /**
     * Creates a new {@link GekCache} based by {@link WeakReference}.
     *
     * @param <K> key type
     * @param <V> value type
     * @return a new {@link GekCache}
     */
    static <K, V> GekCache<K, V> weakCache() {
        return new SimpleCache<>(false);
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
        return new SimpleCache<>(false, removeListener);
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
        return new SimpleCache<>(false, initialCapacity, null);
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
        return new SimpleCache<>(false, initialCapacity, removeListener);
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
     * Duration of expiration for the created value will be set to the default expiration.
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
     * If there is no entry for given key or the value is expired, a new value info will be created by given loader.
     * If the loader returns null, no value will be put and this method will return null.
     * <p>
     * This method will return null if and only if the loader returns null.
     *
     * @param key    given key
     * @param loader given loader
     * @return value associating with given key from this cache, or created one
     */
    @Nullable
    GekWrapper<V> getWrapper(K key, Function<? super K, @Nullable ValueInfo<? extends V>> loader);

    /**
     * Sets the value associated with given key,
     * return old value or null if there is no old value or old value is expired.
     * Duration of expiration for the value will be set to the default expiration.
     *
     * @param key   given key
     * @param value the value
     * @return old value or null
     */
    V put(K key, V value);

    /**
     * Sets the value info associated with given key and specified expiration in milliseconds,
     * return old value or null if there is no old value or old value is expired.
     * Duration of expiration for the value will be set to the specified expiration in milliseconds, may be -1 if set to
     * use default expiration.
     *
     * @param key        given key
     * @param value      the value
     * @param expiration specified expiration in milliseconds, may be -1 if set to use default expiration
     * @return old value or null
     */
    V put(K key, V value, long expiration);

    /**
     * Sets the value info associated with given key and specified expiration,
     * return old value or null if there is no old value or old value is expired.
     * Duration of expiration for the value will be set to the specified expiration, may be null if set to use default
     * expiration.
     *
     * @param key        given key
     * @param value      the value
     * @param expiration specified expiration, may be null if set to use default expiration
     * @return old value or null
     */
    V put(K key, V value, @Nullable Duration expiration);

    /**
     * Sets duration of expiration in milliseconds for value associated with given key.
     * No effect if there is no old value or old value is expired.
     *
     * @param key        given key
     * @param expiration duration of expiration in milliseconds
     */
    void expire(K key, long expiration);

    /**
     * Sets duration of expiration for value associated with given key.
     * No effect if there is no old value or old value is expired.
     *
     * @param key        given key
     * @param expiration duration of expiration
     */
    void expire(K key, @Nullable Duration expiration);

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
     * Removes values of which key and value info (first and second param) pass given predicate.
     * This method is {@link ValueInfo} version of {@link #removeIf(BiPredicate)}.
     *
     * @param predicate given predicate
     */
    void removeEntry(BiPredicate<K, ValueInfo<V>> predicate);

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

    /**
     * Cache value info with duration of expiration.
     *
     * @param <V> type of value
     */
    interface ValueInfo<V> {

        /**
         * Returns a new instance of {@link ValueInfo} with given value and specified expiration in milliseconds.
         *
         * @param value            given value
         * @param expirationMillis specified expiration in milliseconds, may be -1 if set to use default expiration
         * @param <V>              type of value
         * @return a new instance of {@link ValueInfo} with given value and specified expiration in milliseconds
         */
        static <V> ValueInfo<V> of(@Nullable V value, long expirationMillis) {
            return of(value, Duration.ofMillis(expirationMillis));
        }

        /**
         * Returns a new instance of {@link ValueInfo} with given value and specified expiration.
         *
         * @param value      given value
         * @param expiration specified expiration, may be null if set to use default expiration
         * @param <V>        type of value
         * @return a new instance of {@link ValueInfo} with given value and specified expiration
         */
        static <V> ValueInfo<V> of(@Nullable V value, @Nullable Duration expiration) {
            return new ValueInfo<V>() {
                @Override
                public @Nullable V get() {
                    return value;
                }

                @Override
                public @Nullable Duration expiration() {
                    return expiration;
                }
            };
        }

        /**
         * Returns value.
         *
         * @return value
         */
        @Nullable
        V get();

        /**
         * Returns duration of expiration, may be null if set to use default expiration.
         *
         * @return duration of expiration, may be null if set to use default expiration
         */
        @Nullable
        Duration expiration();
    }

    /**
     * Builder for {@link GekCache}, based on {@link SoftReference} or {@link WeakReference}.
     *
     * @param <K> type of key
     * @param <V> type of value
     */
    class Builder<K, V> {

        private GekCache.RemoveListener<K, V> removeListener = null;
        private boolean isSoft = true;
        private long defaultExpiration = -1;
        private int initialCapacity = 0;

        /**
         * Sets default expiration in milliseconds,
         * may be -1 if set to use gc ({@link Reference}) to mark expired values.
         *
         * @param expirationMillis default expiration in milliseconds
         * @param <K1>             type of key
         * @param <V1>             type of value
         * @return this
         */
        public <K1 extends K, V1 extends V> Builder<K1, V1> defaultExpiration(long expirationMillis) {
            this.defaultExpiration = expirationMillis;
            return Gek.as(this);
        }

        /**
         * Sets default expiration,
         * may be -1 if set to use gc ({@link Reference}) to mark expired values.
         *
         * @param expirationMillis default expiration
         * @param <K1>             type of key
         * @param <V1>             type of value
         * @return this
         */
        public <K1 extends K, V1 extends V> Builder<K1, V1> defaultExpiration(@Nullable Duration expirationMillis) {
            this.defaultExpiration = expirationMillis == null ? -1 : expirationMillis.toMillis();
            return Gek.as(this);
        }

        /**
         * Sets listener on value removing.
         *
         * @param removeListener listener on value removing
         * @param <K1>           type of key
         * @param <V1>           type of value
         * @return this
         */
        public <K1 extends K, V1 extends V> Builder<K1, V1> removeListener(GekCache.RemoveListener<K, V> removeListener) {
            this.removeListener = removeListener;
            return Gek.as(this);
        }

        /**
         * Sets the cache is based on {@link SoftReference}.
         *
         * @param <K1> type of key
         * @param <V1> type of value
         * @return this
         */
        public <K1 extends K, V1 extends V> Builder<K1, V1> softReference() {
            this.isSoft = true;
            return Gek.as(this);
        }

        /**
         * Sets the cache is based on {@link WeakReference}.
         *
         * @param <K1> type of key
         * @param <V1> type of value
         * @return this
         */
        public <K1 extends K, V1 extends V> Builder<K1, V1> weakReference() {
            this.isSoft = false;
            return Gek.as(this);
        }

        /**
         * Sets initial capacity.
         *
         * @param initialCapacity initial capacity
         * @param <K1>            type of key
         * @param <V1>            type of value
         * @return this
         */
        public <K1 extends K, V1 extends V> Builder<K1, V1> initialCapacity(int initialCapacity) {
            this.initialCapacity = initialCapacity;
            return Gek.as(this);
        }

        /**
         * Builds the final cache.
         *
         * @param <K1> type of key
         * @param <V1> type of value
         * @return built final cache
         */
        public <K1 extends K, V1 extends V> GekCache<K1, V1> build() {
            return Gek.as(new FullCache<>(isSoft, defaultExpiration, initialCapacity, removeListener));
        }
    }
}
