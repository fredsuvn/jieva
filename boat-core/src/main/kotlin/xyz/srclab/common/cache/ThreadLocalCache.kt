package xyz.srclab.common.cache

import java.time.Duration

class ThreadLocalCache<K : Any, V>(cacheSupplier: () -> Cache<K, V>) : Cache<K, V> {

    private val threadLocal: ThreadLocal<Cache<K, V>> = ThreadLocal.withInitial(cacheSupplier)

    override fun get(key: K): V {
        return threadLocal.get().get(key)
    }

    override fun getOrNull(key: K): V? {
        return threadLocal.get().getOrNull(key)
    }

    override fun getOrElse(key: K, defaultValue: (K) -> V): V {
        return threadLocal.get().getOrElse(key, defaultValue)
    }

    override fun getPresent(keys: Iterable<K>): Map<K, V> {
        return threadLocal.get().getPresent(keys)
    }

    override fun getAll(keys: Iterable<K>, loader: (Iterable<K>) -> Map<K, V>): Map<K, V> {
        return threadLocal.get().getAll(keys, loader)
    }

    override fun getOrElse(key: K, defaultValue: V): V {
        return threadLocal.get().getOrElse(key, defaultValue)
    }

    override fun getOrLoad(key: K, loader: (K) -> V): V {
        return threadLocal.get().getOrLoad(key, loader)
    }

    override fun put(key: K, value: V) {
        threadLocal.get().put(key, value)
    }

    override fun put(key: K, value: V, expirySeconds: Long) {
        threadLocal.get().put(key, value, expirySeconds)
    }

    override fun put(key: K, value: V, expiry: Duration) {
        threadLocal.get().put(key, value, expiry)
    }

    override fun putAll(entries: Map<out K, V>) {
        threadLocal.get().putAll(entries)
    }

    override fun putAll(entries: Map<out K, V>, expirySeconds: Long) {
        threadLocal.get().putAll(entries, expirySeconds)
    }

    override fun putAll(entries: Map<out K, V>, expiry: Duration) {
        threadLocal.get().putAll(entries, expiry)
    }

    override fun expiry(key: K, expirySeconds: Long) {
        threadLocal.get().expiry(key, expirySeconds)
    }

    override fun expiry(key: K, expirySeconds: Duration) {
        threadLocal.get().expiry(key, expirySeconds)
    }

    override fun expiryAll(keys: Iterable<K>, expirySeconds: Long) {
        threadLocal.get().expiryAll(keys, expirySeconds)
    }

    override fun expiryAll(keys: Iterable<K>, expirySeconds: Duration) {
        threadLocal.get().expiryAll(keys, expirySeconds)
    }

    override fun invalidate(key: K) {
        threadLocal.get().invalidate(key)
    }

    override fun invalidateAll(keys: Iterable<K>) {
        threadLocal.get().invalidateAll(keys)
    }

    override fun invalidateAll() {
        threadLocal.get().invalidateAll()
    }

    override fun cleanUp() {
        threadLocal.get().cleanUp()
    }
}