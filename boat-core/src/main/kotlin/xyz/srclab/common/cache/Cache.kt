package xyz.srclab.common.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.common.cache.RemovalListener
import com.google.common.collect.MapMaker
import xyz.srclab.common.base.CacheableBuilder
import xyz.srclab.common.base.asAny
import xyz.srclab.common.base.availableProcessors
import java.time.Duration
import com.github.benmanes.caffeine.cache.RemovalCause as caffeineRemovalCause
import com.google.common.cache.RemovalCause as guavaRemovalCause

/**
 * Cache interface.
 *
 * Note [V] doesn't support `null` value.
 *
 * @see GuavaCache
 * @see GuavaLoadingCache
 * @see CaffeineCache
 * @see CaffeineLoadingCache
 * @see MapCache
 */
interface Cache<K : Any, V : Any> {

    /**
     * Gets or throws from cache.
     */
    @Throws(NoSuchElementException::class)
    fun get(key: K): V {
        return getOrNull(key) ?: throw NoSuchElementException(key.toString())
    }

    /**
     * Gets or returns null if not found from cache.
     */
    fun getOrNull(key: K): V?

    /**
     * Gets or returns [defaultValue] if not found from cache.
     */
    fun getOrElse(key: K, defaultValue: V): V

    /**
     * Gets or returns [defaultValue] if not found from cache.
     */
    fun getOrElse(key: K, defaultValue: (K) -> V): V

    /**
     * Gets or load then returns if not found from cache.
     */
    fun getOrLoad(key: K, loader: (K) -> V): V

    /**
     * Returns a map of the values associated with the present keys from cache.
     */
    fun getPresent(keys: Iterable<K>): Map<K, V>

    /**
     * Returns a map of the values associated with the keys, creating or retrieving those values if necessary.
     */
    fun getAll(keys: Iterable<K>, loader: (Iterable<K>) -> Map<K, V>): Map<K, V>

    fun put(key: K, value: V)

    fun put(key: K, value: V, expirySeconds: Long)

    fun put(key: K, value: V, expiry: Duration)

    fun putAll(entries: Map<out K, V>)

    fun putAll(entries: Map<out K, V>, expirySeconds: Long)

    fun putAll(entries: Map<out K, V>, expiry: Duration)

    fun expiry(key: K, expirySeconds: Long)

    fun expiry(key: K, expiry: Duration)

    fun expiryAll(keys: Iterable<K>, expirySeconds: Long)

    fun expiryAll(keys: Iterable<K>, expiry: Duration)

    fun invalidate(key: K)

    fun invalidateAll(keys: Iterable<K>)

    fun invalidateAll()

    fun cleanUp()

    /**
     * To build a [Cache] instance with [CaffeineCache] or [GuavaCache].
     */
    class Builder<K : Any, V : Any> : CacheableBuilder<Cache<K, V>>() {

        private var initialCapacity: Int? = null
        private var maxSize: Long? = null
        private var concurrencyLevel: Int? = null
        private var expireAfterAccess: Duration? = null
        private var expireAfterWrite: Duration? = null
        private var refreshAfterWrite: Duration? = null
        private var loader: ((K) -> V)? = null
        private var createListener: CacheCreateListener<in K, in V>? = null
        private var readListener: CacheReadListener<in K, in V>? = null
        private var updateListener: CacheUpdateListener<in K, in V>? = null
        private var removeListener: CacheRemoveListener<in K, in V>? = null
        private var useGuava = false

        fun initialCapacity(initialCapacity: Int): Builder<K, V> {
            this.initialCapacity = initialCapacity
            this.commitModification()
            return this
        }

        fun maxSize(maxSize: Long): Builder<K, V> {
            this.maxSize = maxSize
            this.commitModification()
            return this
        }

        fun concurrencyLevel(concurrencyLevel: Int): Builder<K, V> {
            this.concurrencyLevel = concurrencyLevel
            this.commitModification()
            return this
        }

        fun expireAfterAccess(expireAfterAccess: Duration): Builder<K, V> {
            this.expireAfterAccess = expireAfterAccess
            this.commitModification()
            return this
        }

        fun expireAfterWrite(expireAfterWrite: Duration): Builder<K, V> {
            this.expireAfterWrite = expireAfterWrite
            this.commitModification()
            return this
        }

        fun refreshAfterWrite(refreshAfterWrite: Duration): Builder<K, V> {
            this.refreshAfterWrite = refreshAfterWrite
            this.commitModification()
            return this
        }

        fun loader(loader: ((K) -> V)): Builder<K, V> {
            this.loader = loader
            this.commitModification()
            return this
        }

        fun createListener(createListener: CacheCreateListener<in K, in V>): Builder<K, V> {
            this.createListener = createListener
            this.commitModification()
            return this
        }

        fun readListener(readListener: CacheReadListener<in K, in V>): Builder<K, V> {
            this.readListener = readListener
            this.commitModification()
            return this
        }

        fun updateListener(updateListener: CacheUpdateListener<in K, in V>): Builder<K, V> {
            this.updateListener = updateListener
            this.commitModification()
            return this
        }

        fun removeListener(removeListener: CacheRemoveListener<in K, in V>): Builder<K, V> {
            this.removeListener = removeListener
            this.commitModification()
            return this
        }

        /**
         * By default, this builder will use [CaffeineCache]. If set [useGuava] to true, use [GuavaCache].
         */
        fun useGuava(useGuava: Boolean): Builder<K, V> {
            this.useGuava = useGuava
            this.commitModification()
            return this
        }

        override fun buildNew(): Cache<K, V> {
            val params = Params(
                initialCapacity,
                maxSize,
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
            if (params.maxSize !== null) {
                guavaBuilder.maximumSize(params.maxSize)
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
                            return loader(key)
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
            if (params.maxSize !== null) {
                caffeineBuilder.maximumSize(params.maxSize)
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
                    params.removeListener.afterRemove(key.asAny(), value.asAny(), removeCause)
                }
            }
            val loader = params.loader
            return if (loader === null) {
                val guavaCache = caffeineBuilder.build<K, V>()
                CaffeineCache(guavaCache)
            } else {
                val loadingGuavaCache = caffeineBuilder.build<K, V> { k -> loader(k) }
                CaffeineLoadingCache(loadingGuavaCache)
            }
        }

        private data class Params<K, V>(
            val initialCapacity: Int? = null,
            val maxSize: Long? = null,
            val concurrencyLevel: Int? = null,
            val expireAfterAccess: Duration? = null,
            val expireAfterWrite: Duration? = null,
            val refreshAfterWrite: Duration? = null,
            val loader: ((K) -> V)? = null,
            val createListener: CacheCreateListener<in K, in V>? = null,
            val readListener: CacheReadListener<in K, in V>? = null,
            val updateListener: CacheUpdateListener<in K, in V>? = null,
            val removeListener: CacheRemoveListener<in K, in V>? = null,
        )
    }

    companion object {

        @JvmStatic
        fun <K : Any, V : Any> newBuilder(): Builder<K, V> {
            return Builder()
        }

        /**
         * Return a Cache by `Weak Reference Map`.
         *
         * Note:
         *
         * * Does not support to set expiry time;
         * * Do not update value object -- it should be seen as immutable;
         * * It is thread-safe;
         */
        @JvmOverloads
        @JvmStatic
        fun <K : Any, V : Any> weakCache(concurrencyLevel: Int = availableProcessors()): Cache<K, V> {
            return MapCache(MapMaker().concurrencyLevel(concurrencyLevel).weakValues().makeMap())
        }

        @JvmName("ofMap")
        @JvmStatic
        fun <K : Any, V : Any> MutableMap<K, V>.toCache(): Cache<K, V> {
            return MapCache(this)
        }
    }
}