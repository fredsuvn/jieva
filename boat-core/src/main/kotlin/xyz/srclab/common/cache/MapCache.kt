package xyz.srclab.common.cache

import java.util.function.Function

/**
 * [Cache] based on [MutableMap].
 */
open class MapCache<K : Any, V : Any>(
    private val map: MutableMap<K, V>
) : Cache<K, V> {

    override fun getOrNull(key: K): V? {
        return map[key]
    }

    override fun getOrLoad(key: K, loader: Function<in K, V>): V {
        return map.computeIfAbsent(key, loader)
    }

    override fun put(key: K, value: V) {
        map[key] = value
    }

    override fun cleanUp() {
    }

    override fun asMap(): MutableMap<K, V> {
        return map
    }
}