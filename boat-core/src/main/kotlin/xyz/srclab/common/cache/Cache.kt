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
 * A simple cache interface, thread-safe, default implemented by:
 *
 * * [guava](https://github.com/google/guava)
 * * [caffeine](https://github.com/ben-manes/caffeine).
 *
 * A cache may have a default loader, when use `get` methods,
 * the default loader will be called automatically if the value doesn't exist.
 * The loader uses [Val] to wrap loaded value, or -- if loading failed, return null.
 *
 * The key must not null but null value is permitted.
 *
 * @see Builder
 */
@ThreadSafe
interface Cache<K : Any, V> {

    /**
     * Returns the value associated with the [key] in this cache.
     * If the value is not found in this cache, and this cache has a default [Loader], try to load, cache and return.
     * If the default loader still can't get the value, or this cache doesn't have a default loader,
     * a [NoSuchElementException] will be thrown.
     */
    @Throws(CacheException::class, NoSuchElementException::class)
    operator fun get(key: K): V

    /**
     * Returns the value associated with the [key] in this cache, wrapped by [Val].
     * If the value is not found in this cache, and this cache has a default [Loader], try to load, cache and return.
     * If the default loader still can't get the value, or this cache doesn't have a default loader, return null.
     */
    @Throws(CacheException::class)
    fun getVal(key: K): Val<V>?

    /**
     * Returns the value associated with the [key] in this cache.
     * If the value is not found in this cache, try to load by [loader], cache and return.
     * If the [loader] still can't get the value, a [NoSuchElementException] will be thrown.
     *
     * The [loader] should use [Val] to wrap the loaded value, or return null if it can't load the value.
     */
    @Throws(CacheException::class, NoSuchElementException::class)
    fun get(key: K, loader: Function<in K, Val<V>?>): V

    /**
     * Returns the value associated with the [key] in this cache, wrapped by [Val].
     * If the value is not found in this cache, try to load by [loader], cache and return.
     * If the [loader] still can't get the value, return null.
     *
     * The [loader] should use [Val] to wrap the loaded value, or return null if it can't load the value.
     */
    @Throws(CacheException::class)
    fun getVal(key: K, loader: Function<in K, Val<V>?>): Val<V>?

    /**
     * Returns the value associated with the [key] in this cache.
     * If the value is not found in this cache, a [NoSuchElementException] will be thrown.
     *
     * Note whatever this cache has a default loader, it never loads the new value.
     */
    @Throws(CacheException::class, NoSuchElementException::class)
    fun getPresent(key: K): V

    /**
     * Returns the value associated with the [key] in this cache, wrapped by [Val].
     * If the value is not found in this cache, return null.
     *
     * Note whatever this cache has a default loader, it never loads the new value.
     */
    @Throws(CacheException::class)
    fun getPresentVal(key: K): Val<V>?

    /**
     * Puts the key-value pair into this cache.
     */
    @Throws(CacheException::class)
    fun put(key: K, value: V)

    /**
     * Removes the key-value pair from this cache
     */
    @Throws(CacheException::class)
    fun remove(key: K)

    /**
     * Removes all entries from this cache.
     */
    @Throws(CacheException::class)
    fun clear()

    /**
     * Cleans up the cache, may remove the expired data.
     */
    @Throws(CacheException::class)
    fun cleanUp()

    /**
     * Builder for [Cache].
     */
    class Builder<K : Any, V> {

        var initialCapacity: Int? = null
        var maxCapacity: Long? = null
        var concurrencyLevel: Int? = null
        var expireAfterAccess: Duration? = null
        var expireAfterWrite: Duration? = null
        var refreshAfterWrite: Duration? = null
        var loader: Function<in K, Val<V>?>? = null
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
         * The loader should use [Val] to wrap loaded value, or -- if loading failed, return null.
         */
        fun loader(loader: Function<in K, Val<V>?>): Builder<K, V> = apply {
            this.loader = loader
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