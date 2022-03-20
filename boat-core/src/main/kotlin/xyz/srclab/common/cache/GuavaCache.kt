package xyz.srclab.common.cache

import java.util.function.Function

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





































