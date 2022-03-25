package xyz.srclab.common.cache

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.RemovalCause
import com.google.common.cache.RemovalListener
import xyz.srclab.common.base.Val
import xyz.srclab.common.base.Val.Companion.toVal
import xyz.srclab.common.base.asTyped
import java.util.concurrent.ExecutionException
import java.util.function.Function

open class GuavaCache<K : Any, V>(builder: Cache.Builder<K, V>) : Cache<K, V> {

    protected val cache: com.google.common.cache.Cache<Any, Any> = run {
        val guavaBuilder = buildGuavaBuilder(builder)
        val loader = builder.loader
        if (loader === null) {
            guavaBuilder.build()
        } else {
            guavaBuilder.build(buildGuavaLoader(NULL, loader))
        }
    }

    override fun get(key: K): V {
        val result = cache.getIfPresent(key)
        if (result === null) {
            throw NoSuchElementException(key.toString())
        }
        return if (result === NULL) {
            null.asTyped()
        } else {
            result.asTyped()
        }
    }

    override fun get(key: K, loader: Function<in K, Val<V>?>): V {
        val result = try {
            cache.get(key) {
                val result = loader.apply(key)
                if (result === null) {
                    throw GuavaLoadingFailedException()
                }
                result.value ?: NULL
            }
        } catch (e: ExecutionException) {
            val cause = e.cause
            if (cause === null || cause !is GuavaLoadingFailedException) {
                throw e
            }
            NOT_FOUND
        }
        if (result === NOT_FOUND) {
            throw NoSuchElementException(key.toString())
        }
        return if (result === NULL) {
            null.asTyped()
        } else {
            result.asTyped()
        }
    }

    override fun getVal(key: K): Val<V>? {
        val result = cache.getIfPresent(key)
        if (result === null) {
            return null
        }
        return if (result === NULL) {
            null.toVal().asTyped()
        } else {
            result.toVal().asTyped()
        }
    }

    override fun getVal(key: K, loader: Function<in K, Val<V>?>): Val<V>? {
        TODO("Not yet implemented")
    }

    override fun getPresent(key: K): V {
        TODO("Not yet implemented")
    }

    override fun getPresentVal(key: K): Val<V>? {
        TODO("Not yet implemented")
    }

    override fun getAll(keys: Iterable<K>): Map<K, V> {
        TODO("Not yet implemented")
    }

    override fun getAll(keys: Iterable<K>, loader: Function<in Iterable<K>, Map<K, V>>): Map<K, V> {
        TODO("Not yet implemented")
    }

    override fun getAllPresent(keys: Iterable<K>): Map<K, V> {
        TODO("Not yet implemented")
    }

    override fun put(key: K, value: V) {
        TODO("Not yet implemented")
    }

    override fun putAll(entries: Map<out K, V>) {
        TODO("Not yet implemented")
    }

    override fun remove(key: K) {
        TODO("Not yet implemented")
    }

    override fun removeAll(keys: Iterable<K>) {
        TODO("Not yet implemented")
    }

    override fun removeAll() {
        TODO("Not yet implemented")
    }

    override fun cleanUp() {
        TODO("Not yet implemented")
    }

    override fun asMap(): MutableMap<K, V> {
        TODO("Not yet implemented")
    }

    companion object {
        private const val NULL = "GuavaCache.NULL"
        private const val NOT_FOUND = "GuavaCache.NOT_FOUND"
    }
}

/**
 * [Cache] based on [google guava](https://github.com/google/guava).
 */
open class GuavaCache2<K : Any, V : Any>(
    private val guava: com.google.common.cache.Cache<K, V>
) : Cache<K, V> {

    override fun getOrNull(key: K): V? {
        return guava.getIfPresent(key)
    }

    override fun getOrLoad(key: K, loader: Function<in K, V>): V {
        return guava.get(key) { loader.apply(key) } ?: throw NoSuchElementException(key.toString())
    }

    override fun getAllPresent(keys: Iterable<K>): Map<K, V> {
        return guava.getAllPresent(keys)
    }

    override fun getOrLoadAll(keys: Iterable<K>, loader: Function<in Iterable<K>, Map<K, V>>): Map<K, V> {
        val present = guava.getAllPresent(keys)
        val remaining = keys.minus(present.keys)
        if (remaining.isEmpty()) {
            return present
        }
        val loaded = loader.apply(remaining)
        return present.plus(loaded)
    }

    override fun put(key: K, value: V) {
        guava.put(key, value)
    }

    override fun cleanUp() {
        guava.cleanUp()
    }

    override fun asMap(): MutableMap<K, V> {
        return guava.asMap()
    }
}

/**
 * [Cache] based on loading [google guava](https://github.com/google/guava).
 */
open class GuavaLoadingCache<K : Any, V : Any>(
    private val guava: com.google.common.cache.LoadingCache<K, V>
) : GuavaCache<K, V>(guava) {

    override fun get(key: K): V {
        return guava.get(key)
    }
}

private fun <K : Any, V> buildGuavaBuilder(builder: Cache.Builder<K, V>): CacheBuilder<Any, Any> {
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
    return guavaBuilder
}

private fun <K : Any, V> buildGuavaLoader(nullValue: Any, loader: Cache.Loader<K, V>): CacheLoader<Any, Any> {
    return object : CacheLoader<Any, Any>() {

        override fun load(key: Any): Any {
            val result = loader.load(key.asTyped())
            if (result === null) {
                throw Exception(GuavaLoadingFailedException())
            }
            return result.value ?: nullValue
        }

        override fun loadAll(keys: Iterable<Any>): Map<Any, Any> {
            return loader.loadAll(keys.asTyped()).asTyped()
        }
    }
}

private class GuavaLoadingFailedException : RuntimeException()





































