package xyz.srclab.common.cache

import java.util.function.Function

/**
 * [Cache] based on [caffeine](https://github.com/ben-manes/caffeine).
 *
 * `null` value is not permitted.
 */
open class CaffeineCache<K : Any, V : Any>(
    private val caffeine: com.github.benmanes.caffeine.cache.Cache<K, V>
) : Cache<K, V> {

    override fun getOrNull(key: K): V? {
        return caffeine.getIfPresent(key)
    }

    override fun getAllPresent(keys: Iterable<K>): Map<K, V> {
        return caffeine.getAllPresent(keys)
    }

    override fun getOrLoadAll(keys: Iterable<K>, loader: Function<in Iterable<K>, Map<K, V>>): Map<K, V> {
        return caffeine.getAll(keys) { loader.apply(it) }
    }

    override fun getOrLoadAll(keys: Iterable<K>, loader: (Iterable<K>) -> Map<K, V>): Map<K, V> {
        return caffeine.getAll(keys, loader)
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
 *
 * `null` value is not permitted.
 */
open class CaffeineLoadingCache<K : Any, V : Any>(
    private val caffeine: com.github.benmanes.caffeine.cache.LoadingCache<K, V>
) : CaffeineCache<K, V>(caffeine) {

    override fun get(key: K): V {
        return caffeine.get(key)!!
    }
}