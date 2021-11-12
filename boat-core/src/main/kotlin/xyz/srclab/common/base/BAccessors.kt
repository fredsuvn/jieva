package xyz.srclab.common.base

import xyz.srclab.common.collect.removeAll

/**
 * Accessor for specified object.
 *
 * @see BGetter
 * @see BSetter
 */
interface BAccessor<T : Any> : BGetter<T>, BSetter<T>

/**
 * Getter for specified object.
 *
 * @see BAccessor
 */
interface BGetter<T : Any> {

    val isPresent: Boolean
        get() {
            return getOrNull() !== null
        }

    @Throws(NullPointerException::class)
    fun get(): T {
        return getOrNull()!!
    }

    fun getOrNull(): T?

    fun getOrElse(value: T): T {
        return getOrNull() ?: value
    }

    fun getOrElse(supplier: () -> T): T {
        return getOrNull() ?: supplier()
    }

    fun elseOrNull(value: T?): T? {
        return getOrNull() ?: value
    }

    fun elseOrNull(supplier: () -> T?): T? {
        return getOrNull() ?: supplier()
    }
}

/**
 * Setter for specified object.
 *
 * @see BAccessor
 */
interface BSetter<T : Any> {

    fun set(value: T?)
}

/**
 * Accessor for map.
 *
 * This is a simple interface of [MutableMap].
 *
 * @see BMapGetter
 * @see BMapSetter
 */
interface BMapAccessor<K : Any, V : Any> : BMapGetter<K, V>, BMapSetter<K, V>

/**
 * Getter for map.
 *
 * This is a simple interface of [Map].
 *
 * @see BMapAccessor
 */
interface BMapGetter<K : Any, V : Any> {

    fun isPresent(key: K): Boolean {
        return asMap().containsKey(key)
    }

    @Throws(NullPointerException::class)
    fun get(key: K): V {
        return getOrNull(key)!!
    }

    fun getOrNull(key: K): V? {
        return asMap()[key]
    }

    fun getOrElse(key: K, value: V): V {
        return getOrNull(key) ?: value
    }

    fun getOrElse(key: K, supplier: () -> V): V {
        return getOrNull(key) ?: supplier()
    }

    fun elseOrNull(key: K, value: V?): V? {
        return getOrNull(key) ?: value
    }

    fun elseOrNull(key: K, supplier: () -> V?): V? {
        return getOrNull(key) ?: supplier()
    }

    /**
     * Returns a [Map] associated with current object,
     * any modification will reflect the returned map, and vice versa.
     */
    fun asMap(): Map<K, V?>
}

/**
 * Setter for map.
 *
 * This is a simple interface of [MutableMap].
 *
 * @see BMapAccessor
 */
interface BMapSetter<K : Any, V : Any> {

    fun set(key: K, value: V?) {
        asMap()[key] = value
    }

    fun remove(key: K) {
        asMap().remove(key)
    }

    fun removeAll(keys: Array<out K>) {
        asMap().removeAll(keys)
    }

    fun removeAll(keys: Iterable<K>) {
        asMap().removeAll(keys)
    }

    fun removeAll(keys: Sequence<K>) {
        asMap().removeAll(keys)
    }

    fun clear() {
        asMap().clear()
    }

    /**
     * Returns a [MutableMap] associated with current object,
     * any modification will reflect the returned map, and vice versa.
     */
    fun asMap(): MutableMap<K, V?>
}