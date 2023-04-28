package xyz.srclab.common.cache

import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import xyz.srclab.common.base.asType
import java.util.function.Function

/**
 * Base [Cache] implementation using [caffeine](https://github.com/ben-manes/caffeine).
 */
abstract class BaseCaffeineCache<K : Any, V : Any>(
    protected open val cache: com.github.benmanes.caffeine.cache.Cache<K, CacheVal<V>>
) : Cache<K, V> {

    override fun getVal(key: K, loader: Function<in K, out V>): CacheVal<V>? {
        try {
            return cache.get(key) {
                try {
                    CacheVal.of(loader.apply(it))
                } catch (e: NoSuchElementException) {
                    null
                }
            }
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
 * [Cache] implementation using [caffeine](https://github.com/ben-manes/caffeine).
 */
open class CaffeineCache<K : Any, V : Any>(
    cache: com.github.benmanes.caffeine.cache.Cache<K, CacheVal<V>>
) : BaseCaffeineCache<K, V>(cache) {

    /**
     * Constructs with cache builder.
     */
    constructor(builder: Cache.Builder<K, V>) : this(buildCaffeine(builder).build<K, CacheVal<V>>())

    override fun getVal(key: K): CacheVal<V>? {
        return getPresentVal(key)
    }
}

/**
 * Loading [Cache] implementation using [caffeine](https://github.com/ben-manes/caffeine).
 */
open class LoadingCaffeineCache<K : Any, V : Any>(
    override val cache: LoadingCache<K, CacheVal<V>>
) : BaseCaffeineCache<K, V>(cache) {

    /**
     * Constructs with cache builder.
     */
    constructor(builder: Cache.Builder<K, V>) : this(
        buildLoadingCaffeine(builder)
    )

    override fun getVal(key: K): CacheVal<V>? {
        try {
            return cache.get(key)
        } catch (e: Exception) {
            throw CacheException(e)
        }
    }
}

private fun <K : Any, V : Any> buildCaffeine(builder: Cache.Builder<K, V>): Caffeine<K, CacheVal<V>> {
    val caffeine = Caffeine.newBuilder()
    val initialCapacity = builder.initialCapacity
    if (initialCapacity !== null) {
        caffeine.initialCapacity(initialCapacity)
    }
    val maxCapacity = builder.maxCapacity
    if (maxCapacity !== null) {
        caffeine.maximumSize(maxCapacity)
    }
    val expireAfterAccess = builder.expireAfterAccess
    if (expireAfterAccess !== null) {
        caffeine.expireAfterAccess(expireAfterAccess)
    }
    val expireAfterWrite = builder.expireAfterWrite
    if (expireAfterWrite !== null) {
        caffeine.expireAfterWrite(expireAfterWrite)
    }
    val refreshAfterWrite = builder.refreshAfterWrite
    if (refreshAfterWrite !== null) {
        caffeine.refreshAfterWrite(refreshAfterWrite)
    }
    return caffeine.asType()
}

private fun <K : Any, V : Any> buildCaffeineLoader(loader: Function<in K, out V>?): CacheLoader<in K, CacheVal<V>> {
    if (loader === null) {
        throw IllegalArgumentException("Loader must not be null!")
    }
    return CacheLoader {
        try {
            CacheVal.of(loader.apply(it))
        } catch (e: NoSuchElementException) {
            null
        }
    }
}

private fun <K : Any, V : Any> buildLoadingCaffeine(builder: Cache.Builder<K, V>): LoadingCache<K, CacheVal<V>> {
    val c: Caffeine<K, CacheVal<V>> = buildCaffeine(builder)
    val l: CacheLoader<in K, CacheVal<V>> = buildCaffeineLoader(builder.loader)
    return c.build(l)
}