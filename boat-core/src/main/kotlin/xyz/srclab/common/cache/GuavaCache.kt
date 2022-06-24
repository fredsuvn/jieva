package xyz.srclab.common.cache

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import xyz.srclab.common.base.asType
import java.util.concurrent.ExecutionException
import java.util.function.Function

/**
 * Base [Cache] implementation using [guava](https://github.com/google/guava).
 */
abstract class BaseGuavaCache<K : Any, V>(
    protected open val cache: com.google.common.cache.Cache<K, CacheVal<V>>
) : Cache<K, V> {

    override fun getVal(key: K, loader: Function<in K, out V>): CacheVal<V>? {
        try {
            return cache.get(key) {
                CacheVal.of(loader.apply(key))
            }
        } catch (e: ExecutionException) {
            val cause = e.cause
            if (cause is NoSuchElementException) {
                return null
            }
            throw CacheException(e)
        } catch (e: Exception) {
            throw CacheException(e)
        }
    }

    override fun getPresentVal(key: K): CacheVal<V>? {
        try {
            return cache.getIfPresent(key)
        } catch (e: Exception) {
            throw CacheException(e)
        }
    }

    override fun put(key: K, value: V) {
        try {
            cache.put(key, CacheVal.of(value))
        } catch (e: Exception) {
            throw CacheException(e)
        }
    }

    override fun remove(key: K) {
        try {
            cache.invalidate(key)
        } catch (e: Exception) {
            throw CacheException(e)
        }
    }

    override fun clear() {
        try {
            cache.invalidateAll()
        } catch (e: Exception) {
            throw CacheException(e)
        }
    }

    override fun cleanUp() {
        try {
            cache.cleanUp()
        } catch (e: Exception) {
            throw CacheException(e)
        }
    }
}

/**
 * [Cache] implementation using [guava](https://github.com/google/guava).
 */
open class GuavaCache<K : Any, V>(
    cache: com.google.common.cache.Cache<K, CacheVal<V>>
) : BaseGuavaCache<K, V>(cache) {

    /**
     * Constructs with cache builder.
     */
    constructor(builder: Cache.Builder<K, V>) : this(buildGuavaBuilder(builder).build<K, CacheVal<V>>())

    override fun getVal(key: K): CacheVal<V>? {
        return getPresentVal(key)
    }
}

/**
 * Loading [Cache] implementation using [guava](https://github.com/google/guava).
 */
open class LoadingGuavaCache<K : Any, V>(
    override val cache: com.google.common.cache.LoadingCache<K, CacheVal<V>>
) : BaseGuavaCache<K, V>(cache) {

    /**
     * Constructs with cache builder.
     */
    constructor(builder: Cache.Builder<K, V>) : this(
        buildGuavaBuilder(builder).build<K, CacheVal<V>>(buildGuavaLoader(builder.loader)))

    override fun getVal(key: K): CacheVal<V>? {
        try {
            return cache.get(key)
        } catch (e: ExecutionException) {
            val cause = e.cause
            if (cause is NoSuchElementException) {
                return null
            }
            throw CacheException(e)
        } catch (e: Exception) {
            throw CacheException(e)
        }
    }
}

private fun <K : Any, V> buildGuavaBuilder(builder: Cache.Builder<K, V>): CacheBuilder<K, CacheVal<V>> {
    val guavaBuilder = CacheBuilder.newBuilder()
    val initialCapacity = builder.initialCapacity
    if (initialCapacity !== null) {
        guavaBuilder.initialCapacity(initialCapacity)
    }
    val maxCapacity = builder.maxCapacity
    if (maxCapacity !== null) {
        guavaBuilder.maximumSize(maxCapacity)
    }
    val concurrencyLevel = builder.concurrencyLevel
    if (concurrencyLevel !== null) {
        guavaBuilder.concurrencyLevel(concurrencyLevel)
    }
    val expireAfterAccess = builder.expireAfterAccess
    if (expireAfterAccess !== null) {
        guavaBuilder.expireAfterAccess(expireAfterAccess)
    }
    val expireAfterWrite = builder.expireAfterWrite
    if (expireAfterWrite !== null) {
        guavaBuilder.expireAfterWrite(expireAfterWrite)
    }
    val refreshAfterWrite = builder.refreshAfterWrite
    if (refreshAfterWrite !== null) {
        guavaBuilder.refreshAfterWrite(refreshAfterWrite)
    }
    return guavaBuilder.asType()
}

private fun <K : Any, V> buildGuavaLoader(loader: Function<in K, out V>?): CacheLoader<K, CacheVal<V>> {
    if (loader === null) {
        throw IllegalArgumentException("Loader must not be null!")
    }
    return object : CacheLoader<K, CacheVal<V>>() {
        override fun load(key: K): CacheVal<V> {
            try {
                return CacheVal.of(loader.apply(key))
            } catch (e: NoSuchElementException) {
                throw e
            }
        }
    }
}