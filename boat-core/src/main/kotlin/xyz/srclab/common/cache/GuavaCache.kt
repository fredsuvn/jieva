package xyz.srclab.common.cache

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.RemovalCause
import com.google.common.cache.RemovalListener
import xyz.srclab.common.base.asTyped
import java.util.concurrent.ExecutionException
import java.util.function.Function

/**
 * Base [Cache] implementation using [guava](https://github.com/google/guava).
 */
abstract class BaseGuavaCache<K : Any, V : Any> : Cache<K, V> {

    protected abstract val cache: com.google.common.cache.Cache<K, V>

    override fun getOrNull(key: K, loader: Function<in K, V?>): V? {
        try {
            return cache.get(key) { loader.apply(key) ?: throw GuavaLoadingNullException() }
        } catch (e: ExecutionException) {
            val cause = e.cause
            if (cause is GuavaLoadingNullException) {
                return null
            }
            throw CacheException(e)
        } catch (e: Exception) {
            throw CacheException(e)
        }
    }

    override fun getPresentOrNull(key: K): V? {
        return cache.getIfPresent(key)
    }

    override fun getAll(keys: Iterable<K>): Map<K, V> {
        throw CacheException(UnsupportedOperationException())
    }

    override fun getAll(keys: Iterable<K>, loader: Function<in Iterable<K>, Map<K, V>>): Map<K, V> {
        throw CacheException(UnsupportedOperationException())
    }

    override fun getAllPresent(keys: Iterable<K>): Map<K, V> {
        return cache.getAllPresent(keys)
    }

    override fun put(key: K, value: V) {
        cache.put(key, value)
    }

    override fun putAll(entries: Map<out K, V>) {
        cache.putAll(entries)
    }

    override fun remove(key: K) {
        cache.invalidate(key)
    }

    override fun removeAll(keys: Iterable<K>) {
        cache.invalidateAll(keys)
    }

    override fun removeAll() {
        cache.invalidateAll()
    }

    override fun cleanUp() {
        cache.cleanUp()
    }

    override fun asMap(): MutableMap<K, V> {
        return cache.asMap()
    }
}

/**
 * [Cache] implementation using [guava](https://github.com/google/guava).
 */
open class GuavaCache<K : Any, V : Any>(builder: Cache.Builder<K, V>) : BaseGuavaCache<K, V>() {

    override val cache: com.google.common.cache.Cache<K, V> = run {
        val guavaBuilder: CacheBuilder<K, V> = buildGuavaBuilder(builder)
        val loader = builder.loader
        if (loader === null) {
            guavaBuilder.build()
        } else {
            guavaBuilder.build(buildGuavaLoader(loader))
        }
    }

    override fun getOrNull(key: K): V? {
        return getPresentOrNull(key)
    }
}

/**
 * [Cache] implementation using [guava](https://github.com/google/guava).
 */
open class GuavaLoadingCache<K : Any, V : Any>(builder: Cache.Builder<K, V>) : BaseGuavaCache<K, V>() {

    override val cache: com.google.common.cache.LoadingCache<K, V> = run {
        val loader = builder.loader
        if (loader === null) {
            throw IllegalArgumentException("Loader cannot be null!")
        }
        val guavaBuilder: CacheBuilder<K, V> = buildGuavaBuilder(builder)
        guavaBuilder.build(buildGuavaLoader(loader))
    }

    override fun getOrNull(key: K): V? {
        try {
            return cache.get(key)
        } catch (e: ExecutionException) {
            val cause = e.cause
            if (cause is GuavaLoadingNullException) {
                return null
            }
            throw CacheException(e)
        } catch (e: Exception) {
            throw CacheException(e)
        }
    }

    override fun getAll(keys: Iterable<K>): Map<K, V> {
        try {
            return cache.getAll(keys)
        } catch (e: Exception) {
            throw CacheException(e)
        }
    }

    override fun getAll(keys: Iterable<K>, loader: Function<in Iterable<K>, Map<K, V>>): Map<K, V> {
        throw CacheException(UnsupportedOperationException())
    }
}

private fun <K : Any, V : Any> buildGuavaBuilder(builder: Cache.Builder<K, V>): CacheBuilder<K, V> {
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
    val listener = builder.listener
    if (listener !== null) {
        guavaBuilder.removalListener(RemovalListener<Any, Any> { notification ->
            val removeCause: Cache.RemoveCause = when (notification.cause!!) {
                RemovalCause.EXPLICIT -> Cache.RemoveCause.EXPLICIT
                RemovalCause.REPLACED -> Cache.RemoveCause.REPLACED
                RemovalCause.COLLECTED -> Cache.RemoveCause.COLLECTED
                RemovalCause.EXPIRED -> Cache.RemoveCause.EXPIRED
                RemovalCause.SIZE -> Cache.RemoveCause.SIZE
            }
            listener.afterRemove(notification.key.asTyped(), notification.value.asTyped(), removeCause)
        })
    }
    return guavaBuilder.asTyped()
}

private fun <K : Any, V : Any> buildGuavaLoader(loader: Cache.Loader<K, V>): CacheLoader<K, V> {
    return object : CacheLoader<K, V>() {

        override fun load(key: K): V {
            val result = loader.load(key)
            if (result === null) {
                throw GuavaLoadingNullException()
            }
            return result
        }

        override fun loadAll(keys: Iterable<K>): Map<K, V> {
            return loader.loadAll(keys)
        }
    }
}

private class GuavaLoadingNullException : RuntimeException()





































