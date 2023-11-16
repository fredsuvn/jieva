package xyz.fsgek.common.cache;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.annotations.ThreadSafe;
import xyz.fsgek.common.base.Gek;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Cache interface, provides get/compute/put/expire/remove/clear/clean methods which perform like
 * {@link ConcurrentHashMap}. Implementations should be thread-safe, this is defined in this interface.
 * <p>
 * This cache supports set different expiration for each entry, and null value is permitted.
 * However, default implementation is based on GC (Garbage Collection, {@link Reference}), invalidation of values is
 * hard to estimate. Even if a value hasn't exceeded the expiration, it's still possible to not be retrieved.
 * <p>
 * Default implementation will call {@link #cleanUp()} before end of each other method calling to clean up expired data.
 * This means that expired data may still occupy memory until the next invocation of {@link #cleanUp()}.
 * Scheduling task for {@link #cleanUp()} can effectively resolve this problem.
 * <p>
 * {@link RemoveListener} is used to listen removing action of ths cache.
 * As mentioned above, if a value is cleared by GC before {@link RemoveListener#onRemove(Object, Object, GekCache)} is
 * called, the value parameter will be passed to null.
 *
 * @param <K> key type
 * @param <V> value type
 * @author fresduvn
 */
@ThreadSafe
public interface GekCache<K, V> {

    /**
     * Returns new builder of {@link GekCache}.
     *
     * @param <K> key type
     * @param <V> value type
     * @return new builder of {@link GekCache}
     */
    static <K, V> Builder<K, V> newBuilder() {
        return new Builder<>();
    }

    /**
     * Creates a new {@link GekCache} based on {@link SoftReference}.
     *
     * @param <K> key type
     * @param <V> value type
     * @return a new {@link GekCache}
     */
    static <K, V> GekCache<K, V> softCache() {
        return newBuilder().build();
    }

    /**
     * Creates a new {@link GekCache} based on {@link SoftReference} with remove listener,
     * the listener will be called <b>after</b> removing from the cache.
     *
     * @param removeListener remove listener called <b>after</b> removing from the cache,
     *                       the first argument is current cache and second is the key.
     * @param <K>            key type
     * @param <V>            value type
     * @return a new {@link GekCache}
     */
    static <K, V> GekCache<K, V> softCache(RemoveListener<K, V> removeListener) {
        return newBuilder().removeListener(removeListener).build();
    }

    /**
     * Creates a new {@link GekCache} based on {@link WeakReference}.
     *
     * @param <K> key type
     * @param <V> value type
     * @return a new {@link GekCache}
     */
    static <K, V> GekCache<K, V> weakCache() {
        return newBuilder().useSoft(false).build();
    }

    /**
     * Creates a new {@link GekCache} based on {@link WeakReference} with remove listener,
     * the listener will be called <b>after</b> removing from the cache.
     *
     * @param removeListener remove listener called <b>after</b> removing from the cache,
     *                       the first argument is current cache and second is the key.
     * @param <K>            key type
     * @param <V>            value type
     * @return a new {@link GekCache}
     */
    static <K, V> GekCache<K, V> weakCache(RemoveListener<K, V> removeListener) {
        return newBuilder().useSoft(false).removeListener(removeListener).build();
    }

    /**
     * Returns value associating with specified key.
     * The value will be null if there is no entry for specified key or is expired or itself is null.
     *
     * @param key specified key
     * @return value associating with specified key
     */
    @Nullable
    V get(K key);

    /**
     * Returns value associating with specified key, and it performs like
     * {@link ConcurrentHashMap#computeIfAbsent(Object, Function)}:
     * <p>
     * If the specified key is not already associated with a value, attempts to compute its value using the given
     * loader and enters it into this cache. Null is permitted so the null value will also be entered.
     * <p>
     * This cache is thread-safe so the loader function is applied at most once per key, and blocks other thread.
     *
     * @param key    specified key
     * @param loader given loader
     * @return value associating with specified key, may be computed from given loader
     */
    @Nullable
    V get(K key, Function<? super K, @Nullable ? extends V> loader);

    /**
     * Returns value wrapped by {@link Optional} associating with specified key.
     * The returned {@link Optional} will be null if there is no entry for specified key.
     *
     * @param key specified key
     * @return value wrapped by {@link Optional} associating with specified key, or null if not found
     */
    @Nullable
    Optional<V> getOptional(K key);

    /**
     * Returns value wrapped by {@link Optional} associating with specified key, and it performs like
     * {@link ConcurrentHashMap#computeIfAbsent(Object, Function)}:
     * <p>
     * If the specified key is not already associated with a value, attempts to compute its value as {@link ValueInfo}
     * using the given loader and enters it into this cache. If the loader returns null, no mapping will be recorded and
     * this method will return null.
     * <p>
     * This cache is thread-safe so the loader function is applied at most once per key, and blocks other thread.
     *
     * @param key    specified key
     * @param loader given loader
     * @return value associating with specified key, may be computed from given loader
     */
    @Nullable
    Optional<V> getOptional(K key, Function<? super K, @Nullable ValueInfo<? extends V>> loader);

    /**
     * Sets the value associated with specified key,
     * return old value or null if there is no old value or old value is expired.
     *
     * @param key   specified key
     * @param value the value
     * @return old value or null
     */
    V put(K key, V value);

    /**
     * Sets the value info associated with specified key and specified expiration in milliseconds,
     * return old value or null if there is no old value or old value is expired.
     * The expiration can be set to -1, in this case the value uses default expiration rule.
     *
     * @param key              specified key
     * @param value            the value
     * @param expirationMillis specified expiration in milliseconds
     * @return old value or null
     */
    V put(K key, V value, long expirationMillis);

    /**
     * Sets the value info associated with specified key and specified expiration,
     * return old value or null if there is no old value or old value is expired.
     * The expiration can be set to null, in this case the value uses default expiration rule.
     *
     * @param key        specified key
     * @param value      the value
     * @param expiration specified expiration
     * @return old value or null
     */
    V put(K key, V value, @Nullable Duration expiration);

    /**
     * Sets expiration in milliseconds for value associated with specified key.
     * No effect if there is no old value or old value is expired.
     * The expiration can be set to -1, in this case the value uses default expiration rule.
     *
     * @param key              specified key
     * @param expirationMillis expiration in milliseconds
     */
    void expire(K key, long expirationMillis);

    /**
     * Sets expiration for value associated with specified key.
     * No effect if there is no old value or old value is expired.
     * The expiration can be set to null, in this case the value uses default expiration rule.
     *
     * @param key        specified key
     * @param expiration expiration
     */
    void expire(K key, @Nullable Duration expiration);

    /**
     * Removes the value associated with specified key.
     *
     * @param key specified key
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
     * Removes all expired values in this cache.
     */
    void cleanUp();

    /**
     * Removing listener of {@link GekCache}.
     *
     * @param <K> key type
     * @param <V> value type
     */
    interface RemoveListener<K, V> {

        /**
         * This method will be called after removing a key and its value.
         *
         * @param key   key of removed value
         * @param value removed value, may be null if the GC has already cleared the value
         * @param cache current cache
         */
        void onRemove(K key, @Nullable V value, GekCache<K, V> cache);
    }

    /**
     * Cache value info with expiration.
     *
     * @param <V> value type
     */
    interface ValueInfo<V> {

        /**
         * Returns a new instance with given value and specified expiration in milliseconds.
         *
         * @param value            given value
         * @param expirationMillis specified expiration in milliseconds, may be -1 if set to use default expiration rule
         * @param <V>              value type
         * @return a new instance with given value and specified expiration in milliseconds
         */
        static <V> ValueInfo<V> of(@Nullable V value, long expirationMillis) {
            Duration duration = expirationMillis < 0 ? null : Duration.ofMillis(expirationMillis);
            return new ValueInfo<V>() {
                @Override
                public @Nullable V get() {
                    return value;
                }

                @Override
                public @Nullable Duration expiration() {
                    return duration;
                }

                @Override
                public long expirationMillis() {
                    return expirationMillis;
                }
            };
        }

        /**
         * Returns a new instance with given value and specified expiration.
         *
         * @param value      given value
         * @param expiration specified expiration, may be null if set to use default expiration rule
         * @param <V>        value type
         * @return a new instance with given value and specified expiration
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

                @Override
                public long expirationMillis() {
                    return expiration == null ? -1 : expiration.toMillis();
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
         * Returns expiration, may be null if set to use default expiration rule.
         *
         * @return expiration, may be null if set to use default expiration rule
         */
        @Nullable
        Duration expiration();

        /**
         * Returns expiration in milliseconds, may be -1 if set to use default expiration rule.
         *
         * @return expiration in milliseconds, may be -1 if set to use default expiration rule
         */
        long expirationMillis();
    }

    /**
     * Builder for {@link GekCache}.
     *
     * @param <K> key type
     * @param <V> value type
     */
    class Builder<K, V> {

        private boolean useSoft = true;
        private int initialCapacity = -1;
        private GekCache.RemoveListener<K, V> removeListener;
        private long defaultExpirationMillis = -1;

        /**
         * Set whether based on {@link SoftReference}, true for {@link SoftReference}, false for {@link WeakReference}.
         *
         * @param useSoft whether based on {@link SoftReference}
         * @param <K1>    key type
         * @param <V1>    value type
         * @return this builder
         */
        public <K1 extends K, V1 extends V> Builder<K1, V1> useSoft(boolean useSoft) {
            this.useSoft = useSoft;
            return Gek.as(this);
        }

        /**
         * Set initial capacity.
         *
         * @param initialCapacity initial capacity
         * @param <K1>            key type
         * @param <V1>            value type
         * @return this builder
         */
        public <K1 extends K, V1 extends V> Builder<K1, V1> initialCapacity(int initialCapacity) {
            this.initialCapacity = initialCapacity;
            return Gek.as(this);
        }

        /**
         * Set removing event listener.
         *
         * @param removeListener removing event listener
         * @param <K1>           key type
         * @param <V1>           value type
         * @return this builder
         */
        public <K1 extends K, V1 extends V> Builder<K1, V1> removeListener(GekCache.RemoveListener<K1, V1> removeListener) {
            this.removeListener = (RemoveListener<K, V>) removeListener;
            return Gek.as(this);
        }

        /**
         * Set default expiration in milliseconds, may be -1 if set to expired by GC
         *
         * @param defaultExpirationMillis default expiration in milliseconds, may be -1 if set to expired by GC
         * @param <K1>                    key type
         * @param <V1>                    value type
         * @return this builder
         */
        public <K1 extends K, V1 extends V> Builder<K1, V1> defaultExpirationMillis(long defaultExpirationMillis) {
            this.defaultExpirationMillis = defaultExpirationMillis;
            return Gek.as(this);
        }

        /**
         * Set default expiration, may be null if set to expired by GC
         *
         * @param defaultExpiration default expiration, may be null if set to expired by GC
         * @param <K1>              key type
         * @param <V1>              value type
         * @return this builder
         */
        public <K1 extends K, V1 extends V> Builder<K1, V1> defaultExpiration(@Nullable Duration defaultExpiration) {
            this.defaultExpirationMillis = defaultExpiration == null ? -1 : defaultExpiration.toMillis();
            return Gek.as(this);
        }

        /**
         * Builds {@link GekCache}.
         *
         * @return built {@link GekCache}
         */
        public <K1 extends K, V1 extends V> GekCache<K1, V1> build() {
            return Gek.as(new GcCache<>(useSoft, defaultExpirationMillis, initialCapacity, removeListener));
        }
    }
}
