package xyz.srclab.common.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.common.cache.RemovalListener
import com.google.common.collect.MapMaker
import xyz.srclab.annotations.concurrent.ThreadSafe
import xyz.srclab.common.base.asTyped
import xyz.srclab.common.base.availableProcessors
import xyz.srclab.common.base.asJavaFun
import xyz.srclab.common.base.asKotlinFun
import java.time.Duration
import java.util.*
import java.util.function.Function
import java.util.function.Supplier
import com.github.benmanes.caffeine.cache.RemovalCause as caffeineRemovalCause
import com.google.common.cache.RemovalCause as guavaRemovalCause

/**
 * Cache interface.
 *
 * Note whether [V] permit `null` is depend on implementation.
 *
 * @see GuavaCache
 * @see GuavaLoadingCache
 * @see CaffeineCache
 * @see CaffeineLoadingCache
 * @see MapCache
 */
@ThreadSafe
interface Cache<K : Any, V> {

    @Throws(NullPointerException::class)
    operator fun get(key: K): V {
        return getOrNull(key) ?: throw NullPointerException("Value of key is not found or null: $key")
    }

    fun getOrNull(key: K): V? {
        return asMap()[key]
    }

    fun getOrDefault(key: K, defaultValue: V): V {
        return asMap().getOrDefault(key, defaultValue)
    }

    fun getOrElse(key: K, value: Supplier<V>): V {
        return asMap().getOrElse(key) { value.get() }
    }

    @JvmSynthetic
    fun getOrElse(key: K, value: () -> V): V {
        return asMap().getOrElse(key, value)
    }

    /**
     * If value is not found, [loader] will be called and result will be put.
     *
     * Note `null` may be a valid value.
     */
    fun getOrLoad(key: K, loader: Function<in K, V>): V {
        return asMap().computeIfAbsent(key, loader)
    }

    /**
     * If value is not found, [loader] will be called and result will be put.
     *
     * Note `null` may be a valid value.
     */
    @JvmSynthetic
    fun getOrLoad(key: K, loader: (K) -> V): V {
        return asMap().computeIfAbsent(key, loader)
    }

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
     * Returns a map of the values associated with the keys, creating or retrieving those values if necessary.
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
     * Returns a map of the values associated with the keys, creating or retrieving those values if necessary.
     */
    @JvmSynthetic
    fun getOrLoadAll(keys: Iterable<K>, loader: (Iterable<K>) -> Map<K, V>): Map<K, V> {
        return getOrLoadAll(keys, loader.asJavaFun())
    }

    fun put(key: K, value: V) {
        asMap()[key] = value
    }

    fun put(key: K, value: V, expirySecond: Long) {
        put(key, value)
    }

    fun put(key: K, value: V, expiry: Duration) {
        put(key, value)
    }

    fun putAll(entries: Map<out K, V>) {
        for (entry in entries) {
            put(entry.key, entry.value)
        }
    }

    fun putAll(entries: Map<out K, V>, expirySecond: Long) {
        putAll(entries)
    }

    fun putAll(entries: Map<out K, V>, expiry: Duration) {
        putAll(entries)
    }

    fun expire(key: K, expirySecond: Long) {}

    fun expire(key: K, expiry: Duration) {}

    fun expireAll(keys: Iterable<K>, expirySecond: Long) {}

    fun expireAll(keys: Iterable<K>, expiry: Duration) {}

    fun remove(key: K) {
        asMap().remove(key)
    }

    fun removeAll(keys: Iterable<K>) {
        for (key in keys) {
            remove(key)
        }
    }

    fun removeAll() {
        asMap().clear()
    }

    fun cleanUp()

    fun asMap(): MutableMap<K, V>

    /**
     * To build a [Cache] instance with [CaffeineCache] or [GuavaCache].
     */
    class Builder<K : Any, V : Any> {

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

        fun initialCapacity(initialCapacity: Int): Builder<K, V> = apply {
            this.initialCapacity = initialCapacity
        }

        fun maxSize(maxSize: Long): Builder<K, V> = apply {
            this.maxSize = maxSize
        }

        fun concurrencyLevel(concurrencyLevel: Int): Builder<K, V> = apply {
            this.concurrencyLevel = concurrencyLevel
        }

        fun expireAfterAccess(expireAfterAccess: Duration): Builder<K, V> = apply {
            this.expireAfterAccess = expireAfterAccess
        }

        fun expireAfterWrite(expireAfterWrite: Duration): Builder<K, V> = apply {
            this.expireAfterWrite = expireAfterWrite
        }

        fun refreshAfterWrite(refreshAfterWrite: Duration): Builder<K, V> = apply {
            this.refreshAfterWrite = refreshAfterWrite
        }

        fun loader(loader: Function<in K, V>): Builder<K, V> = apply {
            this.loader = loader.asKotlinFun()
        }

        @JvmSynthetic
        fun loader(loader: ((K) -> V)): Builder<K, V> = apply {
            this.loader = loader
        }

        fun createListener(createListener: CacheCreateListener<in K, in V>): Builder<K, V> = apply {
            this.createListener = createListener
        }

        fun readListener(readListener: CacheReadListener<in K, in V>): Builder<K, V> = apply {
            this.readListener = readListener
        }

        fun updateListener(updateListener: CacheUpdateListener<in K, in V>): Builder<K, V> = apply {
            this.updateListener = updateListener
        }

        fun removeListener(removeListener: CacheRemoveListener<in K, in V>): Builder<K, V> = apply {
            this.removeListener = removeListener
        }

        /**
         * By default, this builder will use [CaffeineCache]. If set [useGuava] to true, use [GuavaCache].
         */
        fun useGuava(useGuava: Boolean): Builder<K, V> = apply {
            this.useGuava = useGuava
        }

        fun build(): Cache<K, V> {
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
                    params.removeListener.afterRemove(key.asTyped(), value.asTyped(), removeCause)
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

        @JvmName("ofMap")
        @JvmStatic
        fun <K : Any, V> MutableMap<K, V>.toCache(): Cache<K, V> {
            return MapCache(this)
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
        fun <K : Any, V> ofWeak(concurrencyLevel: Int = availableProcessors() * 2): Cache<K, V> {
            return MapMaker().concurrencyLevel(concurrencyLevel).weakValues().makeMap<K, V>().toCache()
        }
    }
}