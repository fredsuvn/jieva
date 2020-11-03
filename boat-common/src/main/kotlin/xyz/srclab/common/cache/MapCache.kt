package xyz.srclab.common.cache

import xyz.srclab.common.collection.MapOps.Companion.removeAll
import java.time.Duration

class MapCache<K : Any, V>(private val map: MutableMap<K, V>) : Cache<K, V> {

    override fun getOrNull(key: K): V? {
        return map[key]
    }

    override fun getOrElse(key: K, defaultValue: V): V {
        return map.getOrDefault(key, defaultValue)
    }

    override fun getOrLoad(key: K, loader: (K) -> V): V {
        return map.computeIfAbsent(key, loader)
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

    override fun expiry(key: K, expirySeconds: Duration) {
    }

    override fun expiryAll(keys: Iterable<K>, expirySeconds: Long) {
    }

    override fun expiryAll(keys: Iterable<K>, expirySeconds: Duration) {
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