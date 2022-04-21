package xyz.srclab.common.cache

import java.util.function.Function

/**
 * [Cache] based on [MutableMap].
 */
open class MapCache<K : Any, V>(
    private val map: MutableMap<K, CacheVal<V>>
) : Cache<K, V> {

    override fun getVal(key: K): CacheVal<V>? {
        return getPresentVal(key)
    }

    override fun getVal(key: K, loader: Function<in K, out CacheVal<V>?>): CacheVal<V>? {
        try {
            return map.computeIfAbsent(key, loader)
        } catch (e: Exception) {
            throw CacheException(e)
        }
    }

    override fun getPresentVal(key: K): CacheVal<V>? {
        return map[key]
    }

    override fun put(key: K, value: V) {
        map[key] = CacheVal.of(value)
    }

    override fun remove(key: K) {
        map.remove(key)
    }

    override fun clear() {
        map.clear()
    }

    override fun cleanUp() {}
}

/**
 * Loading [Cache] based on [MutableMap].
 */
open class LoadingMapCache<K : Any, V> constructor(
    private val map: MutableMap<K, CacheVal<V>>,
    private val loader: Function<in K, out CacheVal<V>?>
) : MapCache<K, V>(map) {
    override fun getVal(key: K): CacheVal<V>? {
        return getVal(key, loader)
    }
}