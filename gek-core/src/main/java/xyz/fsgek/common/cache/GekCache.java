package xyz.fsgek.common.cache;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.annotations.ThreadSafe;
import xyz.fsgek.common.base.Gek;
import xyz.fsgek.common.base.Geko;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Cache interface, provides get/compute/put/contains/expire/remove/clear/clean methods which perform like
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
 * {@link RemovalListener} is used to listen removing event of ths cache. As mentioned above, if a value is cleared by
 * GC, the value parameter will be passed to null on
 * {@link RemovalListener#onRemoval(Object, Object, RemovalListener.Cause)}.
 *
 * @param <K> type of keys
 * @param <V> type of values
 * @author fresduvn
 */
@ThreadSafe
public interface GekCache<K, V> {

    /**
     * Returns new builder of {@link GekCache}.
     *
     * @param <K> type of keys
     * @param <V> type of values
     * @return new builder of {@link GekCache}
     */
    static <K, V> Builder<K, V> newBuilder() {
        return new Builder<>();
    }

    /**
     * Creates a new {@link GekCache} based on {@link SoftReference}.
     *
     * @param <K> type of keys
     * @param <V> type of values
     * @return a new {@link GekCache}
     */
    static <K, V> GekCache<K, V> softCache() {
        return newBuilder().softValues().build();
    }

    /**
     * Creates a new {@link GekCache} based on {@link SoftReference} with removal listener,
     * the listener will be called <b>after</b> removing from the cache.
     *
     * @param removalListener removal listener called <b>after</b> removing from the cache,
     *                        the first argument is current cache and second is the key.
     * @param <K>             type of keys
     * @param <V>             type of values
     * @return a new {@link GekCache}
     */
    static <K, V> GekCache<K, V> softCache(RemovalListener<? super K, ? super V> removalListener) {
        return newBuilder().softValues().removeListener(removalListener).build();
    }

    /**
     * Creates a new {@link GekCache} based on {@link WeakReference}.
     *
     * @param <K> type of keys
     * @param <V> type of values
     * @return a new {@link GekCache}
     */
    static <K, V> GekCache<K, V> weakCache() {
        return newBuilder().weakValues().build();
    }

    /**
     * Creates a new {@link GekCache} based on {@link WeakReference} with removal listener,
     * the listener will be called <b>after</b> removing from the cache.
     *
     * @param removalListener removal listener called <b>after</b> removing from the cache,
     *                        the first argument is current cache and second is the key.
     * @param <K>             type of keys
     * @param <V>             type of values
     * @return a new {@link GekCache}
     */
    static <K, V> GekCache<K, V> weakCache(RemovalListener<? super K, ? super V> removalListener) {
        return newBuilder().weakValues().removeListener(removalListener).build();
    }

    /**
     * Returns value associating with specified key.
     * The value will be null if there is no entry for specified key or is expired or itself is null.
     * <p>
     * This is a {@code get} operation.
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
     * <p>
     * This is a {@code compute} operation if the computation is occurred, otherwise this is a {@code get} operation.
     *
     * @param key    specified key
     * @param loader given loader
     * @return value associating with specified key, may be computed from given loader
     */
    @Nullable
    V get(K key, Function<? super K, @Nullable ? extends V> loader);

    /**
     * Returns value wrapped by {@link Geko} associating with specified key.
     * The returned {@link Geko} will be null if there is no entry for specified key.
     * <p>
     * This is a {@code get} operation.
     *
     * @param key specified key
     * @return value wrapped by {@link Geko} associating with specified key, or null if not found
     */
    @Nullable
    Geko<V> getWrapper(K key);

    /**
     * Returns value wrapped by {@link Geko} associating with specified key, and it performs like
     * {@link ConcurrentHashMap#computeIfAbsent(Object, Function)}:
     * <p>
     * If the specified key is not already associated with a value, attempts to compute its value as {@link Value}
     * using the given loader and enters it into this cache. If the loader returns null, no mapping will be recorded and
     * this method will return null.
     * <p>
     * This cache is thread-safe so the loader function is applied at most once per key, and blocks other thread.
     * <p>
     * This is a {@code compute} operation if the computation is occurred, otherwise this is a {@code get} operation.
     *
     * @param key    specified key
     * @param loader given loader
     * @return value associating with specified key, may be computed from given loader
     */
    @Nullable
    Geko<V> getWrapper(K key, Function<? super K, @Nullable ? extends Value<? extends V>> loader);

    /**
     * Returns whether this cache contains the value associating with specified key and the value is valid.
     * <p>
     * This is a {@code contains} operation.
     *
     * @param key specified key
     * @return whether this cache contains the value associating with specified key and the value is valid
     */
    boolean contains(K key);

    /**
     * Puts or overrides the value associated with specified key.
     * <p>
     * This is a {@code put} operation.
     *
     * @param key   specified key
     * @param value the value
     */
    void put(K key, V value);

    /**
     * Puts or overrides the value associated with specified key and expiration in milliseconds of values which are
     * written after once compute/put operation. The expiration can be -1 if set to default rule.
     * <p>
     * This is a {@code put} operation.
     *
     * @param key                    specified key
     * @param value                  the value
     * @param expireAfterWriteMillis expiration in milliseconds after once writing
     */
    void put(K key, V value, long expireAfterWriteMillis);

    /**
     * Puts or overrides the value wrapped by {@link Value} associated with specified key.
     * <p>
     * This is a {@code put} operation.
     *
     * @param key   specified key
     * @param value the value wrapped by {@link Value}
     */
    void put(K key, Value<? extends V> value);

    /**
     * Sets expiration in milliseconds of values which are written after once compute/put operation for value
     * associated with specified key, can be -1 if set to default rule.
     * Concept of this expiration are same with {@link Value#expireAfterWriteMillis()}.
     * <p>
     * This is a {@code put} operation.
     *
     * @param key                    specified key
     * @param expireAfterWriteMillis expiration in milliseconds after once writing
     */
    void expire(K key, long expireAfterWriteMillis);

    /**
     * Sets expiration in milliseconds for value associated with specified key, can be -1 if set to default rule.
     * Concept of these expirations are same with
     * {@link Value#expireAfterWriteMillis()} and {@link Value#expireAfterAccessMillis()}.
     * <p>
     * This is a {@code put} operation.
     *
     * @param key                     specified key
     * @param expireAfterWriteMillis  expiration in milliseconds after once writing
     * @param expireAfterAccessMillis expiration in milliseconds after once access
     */
    void expire(K key, long expireAfterWriteMillis, long expireAfterAccessMillis);

    /**
     * Sets expiration for value associated with specified key, can be null if set to default rule.
     * Concept of these expirations are same with
     * {@link Value#expireAfterWrite()} and {@link Value#expireAfterAccess()}.
     * <p>
     * This is a {@code put} operation.
     *
     * @param key               specified key
     * @param expireAfterWrite  expiration in milliseconds after once writing
     * @param expireAfterAccess expiration in milliseconds after once access
     */
    void expire(K key, @Nullable Duration expireAfterWrite, @Nullable Duration expireAfterAccess);

    /**
     * Removes the value associated with specified key.
     * <p>
     * This is a {@code remove} operation.
     *
     * @param key specified key
     */
    void remove(K key);

    /**
     * Removes values of which key and value (first and second param) pass given predicate.
     * <p>
     * This is a {@code remove} operation.
     *
     * @param predicate given predicate
     */
    void removeIf(BiPredicate<? super K, ? super V> predicate);

    /**
     * Removes values of which key and value info (first and second param) pass given predicate.
     * This method is {@link Value} version of {@link #removeIf(BiPredicate)}.
     * <p>
     * This is a {@code remove} operation.
     *
     * @param predicate given predicate
     */
    void removeEntry(BiPredicate<? super K, ? super Value<? super V>> predicate);

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
     * Listener interface that can receive a notification when an entry is removed from a cache, should be thread-safe.
     *
     * @param <K> type of keys
     * @param <V> type of values
     */
    @ThreadSafe
    interface RemovalListener<K, V> {

        /**
         * This method will be called after a key and its value are removed.
         *
         * @param key   key of removed value
         * @param value removed value, may be null if the GC has already cleared the value
         * @param cause reason why a cached entry was removed
         */
        void onRemoval(K key, @Nullable V value, Cause cause);

        /**
         * The reason why a cached entry was removed.
         */
        enum Cause {

            /**
             * The entry was manually removed by the user.
             */
            EXPLICIT {
                @Override
                public boolean wasEvicted() {
                    return false;
                }
            },

            /**
             * The entry itself was not actually removed, but its value was replaced by the user.
             */
            REPLACED {
                @Override
                public boolean wasEvicted() {
                    return false;
                }
            },

            /**
             * The entry was removed automatically because its key or value was garbage-collected.
             */
            COLLECTED {
                @Override
                public boolean wasEvicted() {
                    return true;
                }
            },

            /**
             * The entry's expiration timestamp has passed.
             */
            EXPIRED {
                @Override
                public boolean wasEvicted() {
                    return true;
                }
            },

            /**
             * The entry was evicted due to size constraints.
             */
            SIZE {
                @Override
                public boolean wasEvicted() {
                    return true;
                }
            };

            /**
             * Returns {@code true} if there was an automatic removal due to eviction (the cause is neither
             * {@link #EXPLICIT} nor {@link #REPLACED}).
             *
             * @return if the entry was automatically removed due to eviction
             */
            public abstract boolean wasEvicted();
        }
    }

    /**
     * Cache value info with expiration.
     *
     * @param <V> type of values
     */
    interface Value<V> {

        /**
         * Returns a new instance with given value and specified expiration in milliseconds.
         * See {@link #expireAfterWriteMillis()} and {@link #expireAfterWriteMillis()}.
         *
         * @param value                   given value
         * @param expireAfterWriteMillis  expiration in milliseconds after once writing, may be -1
         * @param expireAfterAccessMillis expiration in milliseconds after once access, may be -1
         * @param <V>                     type of values
         * @return a new instance with given value and specified expiration in milliseconds
         */
        static <V> Value<V> of(@Nullable V value, long expireAfterWriteMillis, long expireAfterAccessMillis) {
            Duration expireAfterWrite = expireAfterWriteMillis < 0 ? null : Duration.ofMillis(expireAfterWriteMillis);
            Duration expireAfterAccess = expireAfterAccessMillis < 0 ? null : Duration.ofMillis(expireAfterAccessMillis);
            return new Value<V>() {

                @Override
                public @Nullable V get() {
                    return value;
                }

                @Override
                public long expireAfterWriteMillis() {
                    return expireAfterWriteMillis;
                }

                @Override
                public @Nullable Duration expireAfterWrite() {
                    return expireAfterWrite;
                }

                @Override
                public long expireAfterAccessMillis() {
                    return expireAfterAccessMillis;
                }

                @Override
                public @Nullable Duration expireAfterAccess() {
                    return expireAfterAccess;
                }
            };
        }

        /**
         * Returns a new instance with given value and specified expiration.
         * See {@link #expireAfterWrite()} and {@link #expireAfterWrite()}.
         *
         * @param value             given value
         * @param expireAfterWrite  expiration after once writing, may be null
         * @param expireAfterAccess expiration after once access, may be null
         * @param <V>               type of values
         * @return a new instance with given value and specified expiration
         */
        static <V> Value<V> of(@Nullable V value, @Nullable Duration expireAfterWrite, @Nullable Duration expireAfterAccess) {
            return new Value<V>() {

                @Override
                public @Nullable V get() {
                    return value;
                }

                @Override
                public long expireAfterWriteMillis() {
                    return expireAfterWrite == null ? -1 : expireAfterWrite.toMillis();
                }

                @Override
                public @Nullable Duration expireAfterWrite() {
                    return expireAfterWrite;
                }

                @Override
                public long expireAfterAccessMillis() {
                    return expireAfterAccess == null ? -1 : expireAfterAccess.toMillis();
                }

                @Override
                public @Nullable Duration expireAfterAccess() {
                    return expireAfterAccess;
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
         * Returns expiration in milliseconds of values which are written after once compute/put operation.
         * It can be -1 if set to default rule.
         *
         * @return expiration in milliseconds after once writing
         */
        long expireAfterWriteMillis();

        /**
         * Returns expiration of values which are written after once compute/put operation.
         * It can be null if set to default rule.
         *
         * @return expiration after once writing
         */
        @Nullable
        Duration expireAfterWrite();

        /**
         * Returns expiration in milliseconds of values which are accessed after once get/compute/put operation,
         * but not includes contains operation.
         * It can be -1 if set to default rule.
         *
         * @return expiration in milliseconds after once access
         */
        long expireAfterAccessMillis();

        /**
         * Returns expiration of values which are accessed after once get/compute/put operation,
         * but not includes contains methods.
         * It can be null if set to default rule.
         *
         * @return expiration after once access
         */
        @Nullable
        Duration expireAfterAccess();
    }

    /**
     * Builder for {@link GekCache}.
     *
     * @param <K> type of keys
     * @param <V> type of values
     */
    class Builder<K, V> {

        private boolean useSoft = true;
        private int initialCapacity = -1;
        private RemovalListener<? super K, ? super V> removalListener;
        private long expireAfterWrite = -1;
        private long expireAfterAccess = -1;

        /**
         * Sets whether based on {@link SoftReference}.
         *
         * @param <K1> type of keys
         * @param <V1> type of values
         * @return this builder
         */
        public <K1 extends K, V1 extends V> Builder<K1, V1> softValues() {
            this.useSoft = true;
            return Gek.as(this);
        }

        /**
         * Returns whether based on {@link SoftReference}.
         *
         * @return whether based on {@link SoftReference}
         */
        public boolean isSoftValues() {
            return useSoft;
        }

        /**
         * Sets whether based on {@link WeakReference}.
         *
         * @param <K1> type of keys
         * @param <V1> type of values
         * @return this builder
         */
        public <K1 extends K, V1 extends V> Builder<K1, V1> weakValues() {
            this.useSoft = false;
            return Gek.as(this);
        }

        /**
         * Returns whether based on {@link WeakReference}.
         *
         * @return whether based on {@link WeakReference}
         */
        public boolean isWeakValues() {
            return !useSoft;
        }

        /**
         * Sets initial capacity.
         *
         * @param initialCapacity initial capacity
         * @param <K1>            type of keys
         * @param <V1>            type of values
         * @return this builder
         */
        public <K1 extends K, V1 extends V> Builder<K1, V1> initialCapacity(int initialCapacity) {
            this.initialCapacity = initialCapacity;
            return Gek.as(this);
        }

        /**
         * Returns initial capacity.
         *
         * @return initial capacity
         */
        public int initialCapacity() {
            return initialCapacity;
        }

        /**
         * Sets removing event listener.
         *
         * @param removalListener removing event listener
         * @param <K1>            type of keys
         * @param <V1>            type of values
         * @return this builder
         */
        public <K1 extends K, V1 extends V> Builder<K1, V1> removeListener(
            RemovalListener<? super K1, ? super V1> removalListener) {
            this.removalListener = Gek.as(removalListener);
            return Gek.as(this);
        }

        /**
         * Returns removing event listener.
         *
         * @return removing event listener
         */
        @Nullable
        public <K1 extends K, V1 extends V> GekCache.RemovalListener<? super K, ? super V> removeListener() {
            return removalListener;
        }

        /**
         * Sets default expiration in milliseconds of values which are written after once compute/put operation.
         * It can be -1 if set to expired by GC.
         *
         * @param expireAfterWriteMillis expiration in milliseconds after once writing
         * @param <K1>                   type of keys
         * @param <V1>                   type of values
         * @return this builder
         */
        public <K1 extends K, V1 extends V> Builder<K1, V1> expireAfterWrite(long expireAfterWriteMillis) {
            this.expireAfterWrite = expireAfterWriteMillis;
            return Gek.as(this);
        }

        /**
         * Returns default expiration in milliseconds of values which are written after once compute/put operation.
         * It can be -1 if set to expired by GC.
         *
         * @return expiration in milliseconds after once writing
         */
        public long expireAfterWriteMillis() {
            return expireAfterWrite;
        }

        /**
         * Sets default expiration of values which are written after once compute/put operation.
         * It can be null if set to expired by GC.
         *
         * @param expireAfterWrite expiration after once writing
         * @param <K1>             type of keys
         * @param <V1>             type of values
         * @return this builder
         */
        public <K1 extends K, V1 extends V> Builder<K1, V1> expireAfterWrite(@Nullable Duration expireAfterWrite) {
            this.expireAfterWrite = expireAfterWrite == null ? -1 : expireAfterWrite.toMillis();
            return Gek.as(this);
        }

        /**
         * Returns default expiration of values which are written after once compute/put operation.
         * It can be null if set to expired by GC.
         *
         * @return expiration after once writing
         */
        @Nullable
        public Duration expireAfterWrite() {
            return expireAfterWrite < 0 ? null : Duration.ofMillis(expireAfterWrite);
        }

        /**
         * Sets default expiration in milliseconds of values which are accessed after once get/compute/put operation,
         * but not includes contains methods.
         * It can be -1 if set to expired by GC.
         *
         * @param expireAfterAccessMillis expiration in milliseconds after once access
         * @param <K1>                    type of keys
         * @param <V1>                    type of values
         * @return this builder
         */
        public <K1 extends K, V1 extends V> Builder<K1, V1> expireAfterAccess(long expireAfterAccessMillis) {
            this.expireAfterAccess = expireAfterAccessMillis;
            return Gek.as(this);
        }

        /**
         * Returns default expiration in milliseconds of values which are accessed after once get/compute/put operation,
         * but not includes contains methods.
         * It can be -1 if set to expired by GC.
         *
         * @return expiration in milliseconds after once access
         */
        public long expireAfterAccessMillis() {
            return expireAfterAccess;
        }

        /**
         * Sets default expiration of values which are accessed after once get/compute/put operation,
         * but not includes contains methods.
         * It can be null if set to expired by GC.
         *
         * @param expireAfterAccess expiration after once access
         * @param <K1>              type of keys
         * @param <V1>              type of values
         * @return this builder
         */
        public <K1 extends K, V1 extends V> Builder<K1, V1> expireAfterAccess(@Nullable Duration expireAfterAccess) {
            this.expireAfterAccess = expireAfterAccess == null ? -1 : expireAfterAccess.toMillis();
            return Gek.as(this);
        }

        /**
         * Returns default expiration of values which are accessed after once get/compute/put operation,
         * but not includes contains methods.
         * It can be null if set to expired by GC.
         *
         * @return expiration after once access
         */
        @Nullable
        public Duration expireAfterAccess() {
            return expireAfterAccess < 0 ? null : Duration.ofMillis(expireAfterAccess);
        }

        /**
         * Builds {@link GekCache}.
         *
         * @return built {@link GekCache}
         */
        public <K1 extends K, V1 extends V> GekCache<K1, V1> build() {
            return Gek.as(new CacheImpl<>(this));
        }
    }
}
