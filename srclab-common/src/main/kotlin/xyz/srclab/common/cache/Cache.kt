package xyz.srclab.common.cache

import com.github.benmanes.caffeine.cache.Caffeine
import xyz.srclab.common.base.ABSENT_VALUE
import xyz.srclab.common.base.BaseCachingProductBuilder
import xyz.srclab.common.base.Defaults
import xyz.srclab.common.base.asAny
import xyz.srclab.common.cache.listener.CacheCreateListener
import xyz.srclab.common.cache.listener.CacheReadListener
import xyz.srclab.common.cache.listener.CacheRemoveListener
import xyz.srclab.common.cache.listener.CacheUpdateListener
import java.time.Duration

interface Cache<K : Any, V> {

    @Throws(NoSuchElementException::class)
    fun get(key: K): V {
        val thisAs = this.asAny<Cache<K, Any>>()
        val value = thisAs.getOrElse(key, ABSENT_VALUE)
        if (value === ABSENT_VALUE) {
            throw NoSuchElementException(key.toString())
        }
        return value.asAny()
    }

    fun getOrNull(key: K): V? {
        val thisAs = this.asAny<Cache<K, Any>>()
        val value = thisAs.getOrElse(key, ABSENT_VALUE)
        if (value === ABSENT_VALUE) {
            return null
        }
        return value.asAny()
    }

    fun getOrElse(key: K, defaultValue: V): V

    fun getOrElse(key: K, defaultValue: (K) -> V): V {
        val thisAs = this.asAny<Cache<K, Any>>()
        val value = thisAs.getOrElse(key, ABSENT_VALUE)
        if (value === ABSENT_VALUE) {
            return defaultValue(key)
        }
        return value.asAny()
    }

    fun getOrLoad(key: K, loader: (K) -> V): V

    fun getPresent(keys: Iterable<K>): Map<K, V> {
        val resultMap = mutableMapOf<K, V>()
        val thisAs = this.asAny<Cache<K, Any>>()
        for (key in keys) {
            val value = thisAs.getOrElse(key, ABSENT_VALUE)
            if (value !== ABSENT_VALUE) {
                resultMap[key] = value.asAny()
            }
        }
        return resultMap.toMap()
    }

    fun getAll(keys: Iterable<K>, loader: (Iterable<K>) -> Map<K, V>): Map<K, V> {
        val resultMap = mutableMapOf<K, V>()
        val thisAs = this.asAny<Cache<K, Any>>()
        for (key in keys) {
            val value = thisAs.getOrElse(key, ABSENT_VALUE)
            if (value !== ABSENT_VALUE) {
                resultMap[key] = value.asAny()
            }
        }
        resultMap.putAll(loader(keys.minus(resultMap.keys)))
        return resultMap.toMap()
    }

    fun put(key: K, value: V)

    fun put(key: K, value: V, expirySeconds: Long)

    fun put(key: K, value: V, expiry: Duration)

    fun putAll(entries: Map<out K, V>)

    fun putAll(entries: Map<out K, V>, expirySeconds: Long)

    fun putAll(entries: Map<out K, V>, expiry: Duration)

    fun expiry(key: K, expirySeconds: Long)

    fun expiry(key: K, expirySeconds: Duration)

    fun expiryAll(keys: Iterable<K>, expirySeconds: Long)

    fun expiryAll(keys: Iterable<K>, expirySeconds: Duration)

    fun invalidate(key: K)

    fun invalidateAll(keys: Iterable<K>)

    fun invalidateAll()

    fun cleanUp()

    class Builder<K : Any, V> : BaseCachingProductBuilder<Cache<K, V>>() {

        private var maxSize = Long.MAX_VALUE
        private var concurrencyLevel: Int = Defaults.concurrencyLevel
        private var expiry: CacheExpiry = CacheExpiry.ZERO
        private var loader: ((K) -> V)? = null
        private var createListener: CacheCreateListener<in K, in V>? = null
        private var readListener: CacheReadListener<in K, in V>? = null
        private var updateListener: CacheUpdateListener<in K, in V>? = null
        private var removeListener: CacheRemoveListener<in K, in V>? = null
        private var useGuava = false

        fun maxSize(maxSize: Long): Builder<K, V> {
            this.maxSize = maxSize
            this.commitChange()
            return this
        }

        fun concurrencyLevel(concurrencyLevel: Int): Builder<K, V> {
            this.concurrencyLevel = concurrencyLevel
            this.commitChange()
            return this
        }

        fun expiry(expiry: CacheExpiry): Builder<K, V> {
            this.expiry = expiry
            this.commitChange()
            return this
        }

        fun loader(loader: ((K) -> V)): Builder<K, V> {
            this.loader = loader
            this.commitChange()
            return this
        }

        fun createListener(createListener: CacheCreateListener<in K, in V>): Builder<K, V> {
            this.createListener = createListener
            this.commitChange()
            return this
        }

        fun readListener(readListener: CacheReadListener<in K, in V>): Builder<K, V> {
            this.readListener = readListener
            this.commitChange()
            return this
        }

        fun updateListener(updateListener: CacheUpdateListener<in K, in V>): Builder<K, V> {
            this.updateListener = updateListener
            this.commitChange()
            return this
        }

        fun removeListener(removeListener: CacheRemoveListener<in K, in V>): Builder<K, V> {
            this.removeListener = removeListener
            this.commitChange()
            return this
        }

        fun useGuava(useGuava: Boolean): Builder<K, V> {
            this.useGuava = useGuava
            this.commitChange()
            return this
        }

        override fun buildNew(): Cache<K, V> {
            return if (useGuava) {
                val guavaBuilder: com.google.common.cache.Builder<K, Any> =
                    GuavaCacheSupport.toGuavaBuilder(this)
                if (loader == null) {
                    val guavaCache: com.google.common.cache.Cache<K, Any> = guavaBuilder.build()
                    Cache.guavaCache(guavaCache)
                } else {
                    val loadingGuavaCache = guavaBuilder.build(GuavaCacheSupport.toGuavaCacheLoader(loader))
                    Cache.loadingGuavaCache(loadingGuavaCache)
                }
            } else {
                val caffeine: Caffeine<K, Any> = CaffeineCacheSupport.toCaffeineBuilder(this)
                if (loader == null) {
                    val caffeineCache: com.github.benmanes.caffeine.cache.Cache<K, Any> = caffeine.build()
                    Cache.caffeineCache(caffeineCache)
                } else {
                    val loadingCaffeineCache = caffeine.build(CaffeineCacheSupport.toCaffeineCacheLoader(loader))
                    Cache.loadingCaffeineCache(loadingCaffeineCache)
                }
            }
        }

        companion object {

            @JvmStatic
            fun <K : Any, V> newBuilder(): Builder<K, V> {
                return Builder()
            }
        }
    }
}