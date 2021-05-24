package xyz.srclab.common.cache

import java.time.Duration

open class GuavaCache<K : Any, V>(private val guava: com.google.common.cache.Cache<K, V>) : Cache<K, V> {

    override fun getOrNull(key: K): V? {
        return guava.getIfPresent(key)
    }

    override fun getOrElse(key: K, defaultValue: V): V {
        val value = getOrNull(key)
        return value ?: defaultValue
    }

    override fun getOrLoad(key: K, loader: (K) -> V): V {
        return guava.get(key) { loader(key) }
    }

    override fun getPresent(keys: Iterable<K>): Map<K, V> {
        return guava.getAllPresent(keys)
    }

    override fun getAll(keys: Iterable<K>, loader: (Iterable<K>) -> Map<K, V>): Map<K, V> {
        val resultMap = guava.getAllPresent(keys).toMutableMap()
        resultMap.putAll(loader(keys.minus(resultMap.keys)))
        return resultMap.toMap()
    }

    override fun put(key: K, value: V) {
        guava.put(key, value)
    }

    override fun put(key: K, value: V, expirySeconds: Long) {
        put(key, value)
    }

    override fun put(key: K, value: V, expiry: Duration) {
        put(key, value)
    }

    override fun putAll(entries: Map<out K, V>) {
        guava.putAll(entries)
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
        guava.invalidate(key)
    }

    override fun invalidateAll(keys: Iterable<K>) {
        guava.invalidateAll(keys)
    }

    override fun invalidateAll() {
        guava.invalidateAll()
    }

    override fun cleanUp() {
        guava.cleanUp()
    }
}

class GuavaLoadingCache<K : Any, V>(private val guava: com.google.common.cache.LoadingCache<K, V>) :
    GuavaCache<K, V>(guava) {

    override fun get(key: K): V {
        return guava.get(key)
    }
}