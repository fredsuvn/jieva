package xyz.srclab.common.cache

import com.google.common.cache.RemovalCause
import xyz.srclab.common.base.Val
import java.util.function.Function

open class GuavaCacheImpl<K : Any, V>(builder: Cache.Builder<K, V>) : Cache<K, V> {

    protected val cache:com.google.common.cache.Cache<Any, Any> = run {
        val guavaBuilder = com.google.common.cache.CacheBuilder.newBuilder()
        if (builder.initialCapacity !== null) {
            guavaBuilder.initialCapacity(builder.initialCapacity)
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
                val removeCause: Cache.RemoveCause = when (notification.cause!!) {
                    RemovalCause.EXPLICIT -> Cache.RemoveCause.EXPLICIT
                    RemovalCause.REPLACED -> Cache.RemoveCause.REPLACED
                    RemovalCause.COLLECTED -> Cache.RemoveCause.COLLECTED
                    RemovalCause.EXPIRED -> Cache.RemoveCause.EXPIRED
                    RemovalCause.SIZE -> Cache.RemoveCause.SIZE
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

    override fun get(key: K): V {
        TODO("Not yet implemented")
    }

    override fun get(key: K, loader: Function<in K, Val<V>?>): V {
        TODO("Not yet implemented")
    }

    override fun getVal(key: K): Val<V>? {
        TODO("Not yet implemented")
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

}

/**
 * [Cache] based on [google guava](https://github.com/google/guava).
 */
open class GuavaCache<K : Any, V : Any>(
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





































