package xyz.srclab.common.cache

import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.RemovalCause
import com.github.benmanes.caffeine.cache.RemovalListener
import xyz.srclab.common.base.asTyped
import java.util.concurrent.ExecutionException
import java.util.function.Function

/**
 * [Cache] based on [caffeine](https://github.com/ben-manes/caffeine).
 */
abstract class BaseCaffeineCache<K : Any, V : Any> : Cache<K, V> {

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














open class CaffeineCache<K : Any, V : Any>(
    private val caffeine: com.github.benmanes.caffeine.cache.Cache<K, V>
) : Cache<K, V> {

    override fun getOrNull(key: K): V? {
        return caffeine.getIfPresent(key)
    }

    override fun getOrLoad(key: K, loader: Function<in K, V>): V {
        return caffeine.get(key, loader) ?: throw NoSuchElementException(key.toString())
    }

    override fun getAllPresent(keys: Iterable<K>): Map<K, V> {
        return caffeine.getAllPresent(keys)
    }

    override fun getOrLoadAll(keys: Iterable<K>, loader: Function<in Iterable<K>, Map<K, V>>): Map<K, V> {
        return caffeine.getAll(keys) { loader.apply(it) }
    }

    override fun put(key: K, value: V) {
        caffeine.put(key, value)
    }

    override fun cleanUp() {
        caffeine.cleanUp()
    }

    override fun asMap(): MutableMap<K, V> {
        return caffeine.asMap()
    }
}

/**
 * [Cache] based on loading [caffeine](https://github.com/ben-manes/caffeine).
 */
open class CaffeineLoadingCache<K : Any, V : Any>(
    private val caffeine: com.github.benmanes.caffeine.cache.LoadingCache<K, V>
) : CaffeineCache<K, V>(caffeine) {

    override fun getOrNull(key: K): V? {
        return caffeine.get(key)
    }
}

private fun <K : Any, V : Any> buildCaffeineBuilder(builder: Cache.Builder<K, V>): Caffeine<K, V> {
    val caffeineBuilder = Caffeine.newBuilder()
    val initialCapacity = builder.initialCapacity
    if (initialCapacity !== null) {
        caffeineBuilder.initialCapacity(initialCapacity)
    }
    val maxCapacity = builder.maxCapacity
    if (maxCapacity !== null) {
        caffeineBuilder.maximumSize(maxCapacity)
    }
    val expireAfterAccess = builder.expireAfterAccess
    if (expireAfterAccess !== null) {
        caffeineBuilder.expireAfterAccess(expireAfterAccess)
    }
    val expireAfterWrite = builder.expireAfterWrite
    if (expireAfterWrite !== null) {
        caffeineBuilder.expireAfterWrite(expireAfterWrite)
    }
    val refreshAfterWrite = builder.refreshAfterWrite
    if (refreshAfterWrite !== null) {
        caffeineBuilder.refreshAfterWrite(refreshAfterWrite)
    }
    val listener = builder.listener
    if (listener !== null) {
        caffeineBuilder.removalListener(RemovalListener<Any, Any> { key, value, cause ->
            val removeCause: Cache.RemoveCause = when (cause) {
                RemovalCause.EXPLICIT -> Cache.RemoveCause.EXPLICIT
                RemovalCause.REPLACED -> Cache.RemoveCause.REPLACED
                RemovalCause.COLLECTED -> Cache.RemoveCause.COLLECTED
                RemovalCause.EXPIRED -> Cache.RemoveCause.EXPIRED
                RemovalCause.SIZE -> Cache.RemoveCause.SIZE
            }
            listener.afterRemove(key.asTyped(), value.asTyped(), removeCause)
        })
    }
    return caffeineBuilder.asTyped()
}

private fun <K : Any, V : Any> buildCaffeineLoader(loader: Cache.Loader<K, V>): CacheLoader<K, V> {
    return object : CacheLoader<K, V> {

        override fun load(key: K): V? {
            return loader.load(key)
        }

        override fun loadAll(keys: Iterable<K>): Map<K, V> {
            return loader.loadAll(keys)
        }
    }
}