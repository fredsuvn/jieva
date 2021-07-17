package xyz.srclab.common.cache

import java.time.Duration

open class CaffeineCache<K : Any, V : Any>(
    private val caffeine: com.github.benmanes.caffeine.cache.Cache<K, V>
) :
    Cache<K, V> {

    override fun getOrNull(key: K): V? {
        return caffeine.getIfPresent(key)
    }

    override fun getOrElse(key: K, defaultValue: V): V {
        val value = getOrNull(key)
        return value ?: defaultValue
    }

    override fun getOrElse(key: K, defaultValue: (K) -> V): V {
        val value = getOrNull(key)
        return value ?: defaultValue(key)
    }

    override fun getOrLoad(key: K, loader: (K) -> V): V {
        return caffeine.get(key, loader)!!
    }

    override fun getPresent(keys: Iterable<K>): Map<K, V> {
        return caffeine.getAllPresent(keys)
    }

    override fun getAll(keys: Iterable<K>, loader: (Iterable<K>) -> Map<K, V>): Map<K, V> {
        return caffeine.getAll(keys, loader)
    }

    override fun put(key: K, value: V) {
        caffeine.put(key, value)
    }

    override fun put(key: K, value: V, expirySeconds: Long) {
        put(key, value)
    }

    override fun put(key: K, value: V, expiry: Duration) {
        put(key, value)
    }

    override fun putAll(entries: Map<out K, V>) {
        caffeine.putAll(entries)
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
        caffeine.invalidate(key)
    }

    override fun invalidateAll(keys: Iterable<K>) {
        caffeine.invalidateAll(keys)
    }

    override fun invalidateAll() {
        caffeine.invalidateAll()
    }

    override fun cleanUp() {
        caffeine.cleanUp()
    }
}

class CaffeineLoadingCache<K : Any, V : Any>(
    private val caffeine: com.github.benmanes.caffeine.cache.LoadingCache<K, V>
) :
    CaffeineCache<K, V>(caffeine) {

    override fun get(key: K): V {
        return caffeine.get(key)!!
    }
}