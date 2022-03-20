package xyz.srclab.common.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.common.cache.RemovalListener
import com.google.common.collect.MapMaker
import xyz.srclab.annotations.concurrent.ThreadSafe
import xyz.srclab.common.base.asTyped
import xyz.srclab.common.base.availableProcessors
import java.time.Duration
import java.util.*
import java.util.function.Function
import java.util.function.Supplier
import com.github.benmanes.caffeine.cache.RemovalCause as caffeineRemovalCause
import com.google.common.cache.RemovalCause as guavaRemovalCause

/**
 * Cache interface, which doesn't support null key or value. Note:
 *
 * * All cache implementation should be thread-safe;
 * * Setting expire time for each entry may unsupported for default implementations,
 * there is no effect for those entries' expiry time;
 *
 * @see GuavaCache
 * @see GuavaLoadingCache
 * @see CaffeineCache
 * @see CaffeineLoadingCache
 * @see MapCache
 */
@ThreadSafe
interface Cache<K : Any, V : Any> {

    /**
     * Gets or loads value associated by given [key], or throw [NoSuchElementException] if failed.
     */
    @Throws(NoSuchElementException::class)
    operator fun get(key: K): V {
        return getOrNull(key) ?: throw NoSuchElementException(key.toString())
    }

    /**
     * Gets or loads value associated by given [key], or null if failed.
     */
    fun getOrNull(key: K): V?

    /**
     * Gets or loads value associated by given [key], or [defaultValue] if failed.
     */
    fun getOrDefault(key: K, defaultValue: V): V {
        return getOrNull(key) ?: defaultValue
    }

    /**
     * Gets or loads value associated by given [key], or [elseVale] if failed.
     */
    fun getOrElse(key: K, elseVale: Supplier<V>): V {
        return getOrNull(key) ?: elseVale.get()
    }

    /**
     * Gets or loads value associated by given [key].
     */
    fun getOrLoad(key: K, loader: Function<in K, V>): V

    /**
     * Gets all present values associated by [keys].
     */
    fun getAllPresent(keys: Iterable<K>): Map<K, V> {
        val result = LinkedHashMap<K, V>()
        for (key in keys) {
            val v = getOrNull(key)
            if (v !== null) {
                result[key] = v
            }
        }
        return result
    }

    /**
     * Gets or loads all present values associated by [keys].
     */
    fun getOrLoadAll(keys: Iterable<K>, loader: Function<in Iterable<K>, Map<K, V>>): Map<K, V> {
        val result = LinkedHashMap<K, V>()
        val notFound = LinkedList<K>()
        for (key in keys) {
            val v = getOrNull(key)
            if (v !== null) {
                result[key] = v
            } else {
                notFound.add(key)
            }
        }
        if (notFound.isNotEmpty()) {
            result.putAll(loader.apply(notFound))
        }
        return result
    }

    /**
     * Puts the key with associated value.
     */
    fun put(key: K, value: V)

    /**
     * Puts the key with associated value, and sets the expiry time in millis.
     */
    fun put(key: K, value: V, expiryMillis: Long) {
        put(key, value)
    }

    /**
     * Puts the key with associated value, and sets the expiry time.
     */
    fun put(key: K, value: V, expiry: Duration) {
        put(key, value)
    }

    /**
     * Puts the key value entries.
     */
    fun putAll(entries: Map<out K, V>) {
        for (entry in entries) {
            put(entry.key, entry.value)
        }
    }

    /**
     * Puts the key value entries, and sets the expiry time in millis.
     */
    fun putAll(entries: Map<out K, V>, expiryMillis: Long) {
        putAll(entries)
    }

    /**
     * Puts the key value entries, and sets the expiry time.
     */
    fun putAll(entries: Map<out K, V>, expiry: Duration) {
        putAll(entries)
    }

    /**
     * Sets new expiry time in millis for [key], the expiry time is valid starts from current time.
     */
    fun expire(key: K, expiryMillis: Long) {}

    /**
     * Sets new expiry time for [key], the expiry time is valid starts from current time.
     */
    fun expire(key: K, expiry: Duration) {}

    /**
     * Sets new expiry time in millis for [keys], the expiry time is valid starts from current time.
     */
    fun expireAll(keys: Iterable<K>, expiryMillis: Long) {}

    /**
     * Sets new expiry time for [keys], the expiry time is valid starts from current time.
     */
    fun expireAll(keys: Iterable<K>, expiry: Duration) {}

    /**
     * Removes the [key].
     */
    fun remove(key: K) {
        asMap().remove(key)
    }

    /**
     * Removes the [keys].
     */
    fun removeAll(keys: Iterable<K>) {
        for (key in keys) {
            remove(key)
        }
    }

    /**
     * Removes all.
     */
    fun removeAll() {
        asMap().clear()
    }

    /**
     * Cleans the cache, generally used to clean the invalid data such as expired data, like `GC` of JVM.
     */
    fun cleanUp()

    /**
     * Returns this cache as [MutableMap], any operation for this cache or the returned map will reflect for each other.
     */
    fun asMap(): MutableMap<K, V>

    /**
     * Builder for [Cache].
     */
    class Builder<K : Any, V : Any> {

        private var initialCapacity: Int? = null
        private var maxCapacity: Long? = null
        private var concurrencyLevel: Int? = null
        private var expireAfterAccess: Duration? = null
        private var expireAfterWrite: Duration? = null
        private var refreshAfterWrite: Duration? = null
        private var loader: Function<in K, V>? = null
        private var createListener: CacheCreateListener<in K, in V>? = null
        private var readListener: CacheReadListener<in K, in V>? = null
        private var updateListener: CacheUpdateListener<in K, in V>? = null
        private var removeListener: CacheRemoveListener<in K, in V>? = null
        private var useGuava = false

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
        fun loader(loader: Function<in K, V>): Builder<K, V> = apply {
            this.loader = loader
        }

        /**
         * Sets listener for creating.
         */
        fun createListener(createListener: CacheCreateListener<in K, in V>): Builder<K, V> = apply {
            this.createListener = createListener
        }

        /**
         * Sets listener for reading.
         */
        fun readListener(readListener: CacheReadListener<in K, in V>): Builder<K, V> = apply {
            this.readListener = readListener
        }

        /**
         * Sets listener for updating.
         */
        fun updateListener(updateListener: CacheUpdateListener<in K, in V>): Builder<K, V> = apply {
            this.updateListener = updateListener
        }

        /**
         * Sets listener for removing.
         */
        fun removeListener(removeListener: CacheRemoveListener<in K, in V>): Builder<K, V> = apply {
            this.removeListener = removeListener
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
                createListener,
                readListener,
                updateListener,
                removeListener,
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
            if (params.removeListener !== null) {
                guavaBuilder.removalListener(RemovalListener<K, V> { notification ->
                    val removeCause: CacheRemoveListener.Cause = when (notification.cause!!) {
                        guavaRemovalCause.EXPLICIT -> CacheRemoveListener.Cause.EXPLICIT
                        guavaRemovalCause.REPLACED -> CacheRemoveListener.Cause.REPLACED
                        guavaRemovalCause.COLLECTED -> CacheRemoveListener.Cause.COLLECTED
                        guavaRemovalCause.EXPIRED -> CacheRemoveListener.Cause.EXPIRED
                        guavaRemovalCause.SIZE -> CacheRemoveListener.Cause.SIZE
                    }
                    params.removeListener.afterRemove(notification.key, notification.value, removeCause)
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
                            return loader.apply(key)
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

        private data class Params<K, V>(
            val initialCapacity: Int? = null,
            val maxCapacity: Long? = null,
            val concurrencyLevel: Int? = null,
            val expireAfterAccess: Duration? = null,
            val expireAfterWrite: Duration? = null,
            val refreshAfterWrite: Duration? = null,
            val loader: Function<in K, V>? = null,
            val createListener: CacheCreateListener<in K, in V>? = null,
            val readListener: CacheReadListener<in K, in V>? = null,
            val updateListener: CacheUpdateListener<in K, in V>? = null,
            val removeListener: CacheRemoveListener<in K, in V>? = null,
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