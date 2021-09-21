package xyz.srclab.common.cache

import xyz.srclab.common.collect.removeAll
import java.time.Duration

class MapCache<K : Any, V : Any>(private val map: MutableMap<K, V>) : Cache<K, V> {

    override fun getOrNull(key: K): V? {
        return map[key]
    }

    override fun getOrElse(key: K, defaultValue: V): V {
        return map.getOrDefault(key, defaultValue)
    }

    override fun getOrElse(key: K, defaultValue: (K) -> V): V {
        return map.getOrElse(key) { defaultValue(key) }
    }

    override fun getOrLoad(key: K, loader: (K) -> V): V {
        return map.computeIfAbsent(key, loader)
    }

    override fun getPresent(keys: Iterable<K>): Map<K, V> {
        val result = LinkedHashMap<K, V>()
        for (key in keys) {
            val value = map[key]
            if (value !== null) {
                result[key] = value
            }
        }
        return result
    }

    override fun getAll(keys: Iterable<K>, loader: (Iterable<K>) -> Map<K, V>): Map<K, V> {
        val result = LinkedHashMap<K, V>()
        for (key in keys) {
            val value = map[key]
            if (value !== null) {
                result[key] = value
            }
        }
        val restKeys = keys.minus(result.keys)
        val newValues = loader(restKeys)
        for (restKey in restKeys) {
            result[restKey] = map.computeIfAbsent(restKey) { newValues[restKey]!! }
        }
        return result
    }

    override fun put(key: K, value: V) {
        map[key] = value
    }

    override fun put(key: K, value: V, expirySeconds: Long) {
        put(key, value)
    }

    override fun put(key: K, value: V, expiry: Duration) {
        put(key, value)
    }

    override fun putAll(entries: Map<out K, V>) {
        map.putAll(entries)
    }

    override fun putAll(entries: Map<out K, V>, expirySeconds: Long) {
        putAll(entries)
    }

    override fun putAll(entries: Map<out K, V>, expiry: Duration) {
        putAll(entries)
    }

    override fun expiry(key: K, expirySeconds: Long) {
    }

    override fun expiry(key: K, expiry: Duration) {
    }

    override fun expiryAll(keys: Iterable<K>, expirySeconds: Long) {
    }

    override fun expiryAll(keys: Iterable<K>, expiry: Duration) {
    }

    override fun invalidate(key: K) {
        map.remove(key)
    }

    override fun invalidateAll(keys: Iterable<K>) {
        map.removeAll(keys)
    }

    override fun invalidateAll() {
        map.clear()
    }

    override fun cleanUp() {
    }
}