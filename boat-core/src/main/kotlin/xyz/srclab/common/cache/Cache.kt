package xyz.srclab.common.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.common.cache.RemovalListener
import com.google.common.collect.MapMaker
import xyz.srclab.annotations.concurrent.ThreadSafe
import xyz.srclab.common.base.Val
import xyz.srclab.common.base.asTyped
import xyz.srclab.common.base.availableProcessors
import xyz.srclab.common.cache.Cache.*
import java.time.Duration
import java.util.function.Function
import com.github.benmanes.caffeine.cache.RemovalCause as caffeineRemovalCause
import com.google.common.cache.RemovalCause as guavaRemovalCause

/**
 * Cache interface, thread-safe, default implemented by:
 *
 * * [guava](https://github.com/google/guava)
 * * [caffeine](https://github.com/ben-manes/caffeine).
 *
 * A cache may have a default [Loader], when use `get` methods,
 * the default loader will be called automatically if the value doesn't exist.
 *
 * Null keys and values are unsupported.
 *
 * @see Loader
 * @see Listener
 * @see Builder
 */
@ThreadSafe
interface Cache<K : Any, V : Any> {

    /**
     * Returns the value associated with the [key] in this cache.
     * If not found and this cache has a default [Loader], try to load, cache and return.
     * If the default loader still can't get the value, a [NoSuchElementException] will be thrown.
     */
    @Throws(CacheException::class, NoSuchElementException::class)
    operator fun get(key: K): V {
        return getOrNull(key) ?: throw NoSuchElementException(key.toString())
    }

    /**
     * Returns the value associated with the [key] in this cache.
     * If not found and this cache has a default [Loader], try to load, cache and return.
     * If the default loader still can't get the value, return null.
     */
    @Throws(CacheException::class)
    fun getOrNull(key: K): V?

    /**
     * Returns the value associated with the [key] in this cache.
     * If not found, use [loader] to load, cache and return.
     * If the [loader] returns null, a [NoSuchElementException] will be thrown.
     */
    @Throws(CacheException::class, NoSuchElementException::class)
    fun get(key: K, loader: Function<in K, V?>): V {
        return getOrNull(key, loader) ?: throw NoSuchElementException(key.toString())
    }

    /**
     * Returns the value associated with the [key] in this cache.
     * If not found, use [loader] to load, cache and return.
     * If the [loader] returns null, return null.
     */
    @Throws(CacheException::class)
    fun getOrNull(key: K, loader: Function<in K, V?>): V?

    /**
     * Returns the value associated with the [key] in this cache.
     * If not found, a [NoSuchElementException] will be thrown.
     *
     * Note whatever this cache has a default loader, it never loads the new value.
     */
    @Throws(CacheException::class, NoSuchElementException::class)
    fun getPresent(key: K): V {
        return getPresentOrNull(key) ?: throw NoSuchElementException(key.toString())
    }

    /**
     * Returns the value associated with the [key] in this cache.
     * If not found, return null.
     *
     * Note whatever this cache has a default loader, it never loads the new value.
     */
    @Throws(CacheException::class)
    fun getPresentOrNull(key: K): V?

    /**
     * Returns all values associated with the [keys].
     * If some values are not cached and there exists a default loader, try to load new values then cache.
     * The returned map contains present cached and newly loaded values.
     *
     * Note returned map may not contain all values, some of them may be still not found after loading.
     */
    fun getAll(keys: Iterable<K>): Map<K, V>

    /**
     * Returns all values associated with the [keys].
     * If some values are not cached, the [loader] will try to load new values then cache.
     * The returned map contains present cached and newly loaded values.
     *
     * Note returned map may not contain all values, some of them may be still not found after loading.
     */
    fun getAll(keys: Iterable<K>, loader: Function<in Iterable<K>, Map<K, V>>): Map<K, V>

    /**
     * Returns all present values associated with the [keys] in this cache.
     *
     * Note whatever this cache has a default loader, it never loads the new value.
     */
    fun getAllPresent(keys: Iterable<K>): Map<K, V>

    /**
     * Puts the key with associated value.
     */
    fun put(key: K, value: V)

    /**
     * Puts the key value entries.
     */
    fun putAll(entries: Map<out K, V>) {
        for (entry in entries) {
            put(entry.key, entry.value)
        }
    }

    /**
     * Removes value associated with [key].
     */
    fun remove(key: K)

    /**
     * Removes values associated with [keys].
     */
    fun removeAll(keys: Iterable<K>) {
        for (key in keys) {
            remove(key)
        }
    }

    /**
     * Removes all entries of this cache.
     */
    fun removeAll() {
        asMap().clear()
    }

    /**
     * Cleans up the cache, may remove the expired data.
     */
    fun cleanUp()

    /**
     * Returns this cache as [MutableMap], any operation for this cache or the returned map will reflect each other.
     */
    fun asMap(): MutableMap<K, V>

    /**
     * Loader for [Cache], used to load new value if target value is not found in the cache.
     */
    interface Loader<K : Any, V : Any> {

        /**
         * Loads value wrapped by [Val] associated by [key],
         * if target value cannot be load, return null.
         */
        fun load(key: K): V?

        /**
         * Loads all values associated by [keys].
         * The default implementation is: calling [load] for each key.
         *
         * Note returned map may not contain all target values, some of them may be loading failed.
         */
        fun loadAll(keys: Iterable<K>): Map<K, V> {
            val result = LinkedHashMap<K, V>(keys.count())
            for (key in keys) {
                val newValue = load(key)
                if (newValue !== null) {
                    result[key] = newValue
                }
            }
            return result
        }
    }

    /**
     * Listener for [Cache], used to listen on each cache event.
     * All methods' default implementations are empty.
     *
     * Whether the listening action execute depends on the implementation.
     * For `guava` and `caffeine`, only [afterRemove] is valid.
     *
     * @see RemoveCause
     */
    interface Listener<K : Any, V : Any> {

        /**
         * Callback before creating new entry.
         */
        fun beforeCreate(key: K) {}

        /**
         * Callback after creating new entry.
         */
        fun afterCreate(key: K, value: V) {}

        /**
         * Callback before reading.
         */
        fun beforeRead(key: K) {}

        /**
         * Callback on hitting the key.
         */
        fun onHit(key: K, value: V) {}

        /**
         * Callback on missing the key.
         */
        fun onMiss(key: K) {}

        /**
         * Callback before updating the entry.
         */
        fun beforeUpdate(key: K, oldValue: V) {}

        /**
         * Callback after updating the entry.
         */
        fun afterUpdate(key: K, oldValue: V, newValue: V) {}

        /**
         * Callback before removing the entry.
         */
        fun beforeRemove(key: K, value: V, removeCause: RemoveCause) {}

        /**
         * Callback after removing the entry.
         */
        fun afterRemove(key: K, value: V, removeCause: RemoveCause) {}
    }

    /**
     * The cause why the entry was removed.
     */
    enum class RemoveCause {

        /**
         * The entry was manually removed by the user.
         */
        EXPLICIT {
            override val isEvicted: Boolean
                get() {
                    return false
                }
        },

        /**
         * The entry itself was not actually removed, but its value was replaced by the user.
         */
        REPLACED {
            override val isEvicted: Boolean
                get() {
                    return false
                }
        },

        /**
         * The entry was removed automatically because its key or value was garbage-collected.
         */
        COLLECTED {
            override val isEvicted: Boolean
                get() {
                    return true
                }
        },

        /**
         * The entry is expired.
         */
        EXPIRED {
            override val isEvicted: Boolean
                get() {
                    return true
                }
        },

        /**
         * The entry was evicted due to size constraints.
         */
        SIZE {
            override val isEvicted: Boolean
                get() {
                    return true
                }
        };

        /**
         * Returns `true` if there was an automatic removal due to eviction (the cause is neither
         * [EXPLICIT] nor [REPLACED]).
         *
         * @return if the entry was automatically removed due to eviction
         */
        abstract val isEvicted: Boolean
    }

    /**
     * Builder for [Cache].
     */
    class Builder<K : Any, V : Any> {

        var initialCapacity: Int? = null
        var maxCapacity: Long? = null
        var concurrencyLevel: Int? = null
        var expireAfterAccess: Duration? = null
        var expireAfterWrite: Duration? = null
        var refreshAfterWrite: Duration? = null
        var loader: Loader<in K, V>? = null
        var listener: Listener<in K, in V>? = null
        var useGuava = false

        /**
         * Sets initial capacity.
         */
        fun initialCapacity(initialCapacity: Int): Builder<K, V> = apply {
            this.initialCapacity = initialCapacity
        }

        /**
         * Sets max capacity.
         */
        fun maxCapacity(maxCapacity: Long): Builder<K, V> = apply {
            this.maxCapacity = maxCapacity
        }

        /**
         * Sets concurrency level.
         */
        fun concurrencyLevel(concurrencyLevel: Int): Builder<K, V> = apply {
            this.concurrencyLevel = concurrencyLevel
        }

        /**
         * Sets expire duration after each creating, replacing or reading.
         */
        fun expireAfterAccess(expireAfterAccess: Duration): Builder<K, V> = apply {
            this.expireAfterAccess = expireAfterAccess
        }

        /**
         * Sets expire duration after each creating or replacing.
         */
        fun expireAfterWrite(expireAfterWrite: Duration): Builder<K, V> = apply {
            this.expireAfterWrite = expireAfterWrite
        }

        /**
         * Sets refresh duration after each creating or replacing,
         * the refresh action is specified by [loader].
         */
        fun refreshAfterWrite(refreshAfterWrite: Duration): Builder<K, V> = apply {
            this.refreshAfterWrite = refreshAfterWrite
        }

        /**
         * Sets loader used to automatically load the value if the entry is miss.
         */
        fun loader(loader: Loader<in K, V>): Builder<K, V> = apply {
            this.loader = loader
        }

        /**
         * Sets cache listener.
         */
        fun listener(listener: Listener<in K, in V>): Builder<K, V> = apply {
            this.listener = listener
        }

        /**
         * Let this builder use guava implementation [GuavaCache] to build the [Cache].
         * By default, this builder will use [CaffeineCache].
         */
        fun useGuava(useGuava: Boolean): Builder<K, V> = apply {
            this.useGuava = useGuava
        }

        /**
         * Builds the cache.
         */
        fun build(): Cache<K, V> {
            val params = Params(
                initialCapacity,
                maxCapacity,
                concurrencyLevel,
                expireAfterAccess,
                expireAfterWrite,
                refreshAfterWrite,
                loader,
                listener,
            )
            return if (useGuava) {
                buildGuavaCache(params)
            } else {
                buildCaffeineCache(params)
            }
        }

        private fun buildGuavaCache(params: Params<K, V>): Cache<K, V> {
            val guavaBuilder = com.google.common.cache.CacheBuilder.newBuilder()
            if (params.initialCapacity !== null) {
                guavaBuilder.initialCapacity(params.initialCapacity)
            }
            if (params.maxCapacity !== null) {
                guavaBuilder.maximumSize(params.maxCapacity)
            }
            if (params.concurrencyLevel !== null) {
                guavaBuilder.concurrencyLevel(params.concurrencyLevel)
            }
            if (params.expireAfterAccess !== null) {
                guavaBuilder.expireAfterAccess(params.expireAfterAccess)
            }
            if (params.expireAfterWrite !== null) {
                guavaBuilder.expireAfterWrite(params.expireAfterWrite)
            }
            if (params.refreshAfterWrite !== null) {
                guavaBuilder.refreshAfterWrite(params.refreshAfterWrite)
            }
            if (params.listener !== null) {
                guavaBuilder.removalListener(RemovalListener<K, V> { notification ->
                    val removeCause: RemoveCause = when (notification.cause!!) {
                        guavaRemovalCause.EXPLICIT -> RemoveCause.EXPLICIT
                        guavaRemovalCause.REPLACED -> RemoveCause.REPLACED
                        guavaRemovalCause.COLLECTED -> RemoveCause.COLLECTED
                        guavaRemovalCause.EXPIRED -> RemoveCause.EXPIRED
                        guavaRemovalCause.SIZE -> RemoveCause.SIZE
                    }
                    params.listener.afterRemove(notification.key, notification.value, removeCause)
                })
            }
            val loader = params.loader
            return if (loader === null) {
                val guavaCache = guavaBuilder.build<K, V>()
                GuavaCache(guavaCache)
            } else {
                val loadingGuavaCache =
                    guavaBuilder.build(object : com.google.common.cache.CacheLoader<K, V>() {
                        override fun load(key: K): V {
                            val wrapper = loader.load(key)
                        }
                    })
                GuavaLoadingCache(loadingGuavaCache)
            }
        }

        private fun buildCaffeineCache(params: Params<K, V>): Cache<K, V> {
            val caffeineBuilder = Caffeine.newBuilder()
            if (params.initialCapacity !== null) {
                caffeineBuilder.initialCapacity(params.initialCapacity)
            }
            if (params.maxCapacity !== null) {
                caffeineBuilder.maximumSize(params.maxCapacity)
            }
            if (params.expireAfterAccess !== null) {
                caffeineBuilder.expireAfterAccess(params.expireAfterAccess)
            }
            if (params.expireAfterWrite !== null) {
                caffeineBuilder.expireAfterWrite(params.expireAfterWrite)
            }
            if (params.refreshAfterWrite !== null) {
                caffeineBuilder.refreshAfterWrite(params.refreshAfterWrite)
            }
            if (params.removeListener !== null) {
                caffeineBuilder.removalListener<K, V> { key, value, cause ->
                    val removeCause: CacheRemoveListener.Cause = when (cause) {
                        caffeineRemovalCause.EXPLICIT -> CacheRemoveListener.Cause.EXPLICIT
                        caffeineRemovalCause.REPLACED -> CacheRemoveListener.Cause.REPLACED
                        caffeineRemovalCause.COLLECTED -> CacheRemoveListener.Cause.COLLECTED
                        caffeineRemovalCause.EXPIRED -> CacheRemoveListener.Cause.EXPIRED
                        caffeineRemovalCause.SIZE -> CacheRemoveListener.Cause.SIZE
                    }
                    params.removeListener.afterRemove(key.asTyped(), value.asTyped(), removeCause)
                }
            }
            val loader = params.loader
            return if (loader === null) {
                val guavaCache = caffeineBuilder.build<K, V>()
                CaffeineCache(guavaCache)
            } else {
                val loadingGuavaCache = caffeineBuilder.build<K, V> { k -> loader.apply(k) }
                CaffeineLoadingCache(loadingGuavaCache)
            }
        }

        private data class Params<K : Any, V>(
            val initialCapacity: Int? = null,
            val maxCapacity: Long? = null,
            val concurrencyLevel: Int? = null,
            val expireAfterAccess: Duration? = null,
            val expireAfterWrite: Duration? = null,
            val refreshAfterWrite: Duration? = null,
            val loader: Loader<in K, V>? = null,
            val listener: Listener<in K, in V>? = null,
        )
    }

    companion object {

        /**
         * Returns new builder for Cache interface.
         */
        @JvmStatic
        fun <K : Any, V : Any> newBuilder(): Builder<K, V> {
            return Builder()
        }

        /**
         * Returns a Cache implemented by Map.
         *
         * Note whether the returned cache is thread-safe depends on given [this] map.
         */
        @JvmName("ofMap")
        @JvmStatic
        fun <K : Any, V : Any> MutableMap<K, V>.toCache(): Cache<K, V> {
            return MapCache(this)
        }

        /**
         * Return a Cache implemented by `Weak Reference Map`.
         */
        @JvmOverloads
        @JvmStatic
        fun <K : Any, V : Any> ofWeak(concurrencyLevel: Int = availableProcessors() * 2): Cache<K, V> {
            return MapMaker().concurrencyLevel(concurrencyLevel).weakValues().makeMap<K, V>().toCache()
        }
    }
}