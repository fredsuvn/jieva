package xyz.srclab.common.base

import xyz.srclab.common.collect.removeAll

/**
 * Accessor for specified object.
 *
 * @see ObjectGetter
 * @see ObjectSetter
 * @see TypedAccessor
 */
interface ObjectAccessor : ObjectGetter, ObjectSetter

/**
 * Getter for specified object.
 *
 * @see ObjectAccessor
 */
interface ObjectGetter {

    val isPresent: Boolean
        get() {
            return getOrNull() !== null
        }

    @Throws(NullPointerException::class)
    fun get(): Any {
        return getOrNull()!!
    }

    fun getOrNull(): Any?

    @Throws(NullPointerException::class)
    fun <T : Any> getTyped(): T {
        return getTypedOrNull()!!
    }

    fun <T : Any> getTypedOrNull(): T? {
        return getOrNull().asTyped()
    }

    fun <T : Any> getOrElse(value: T): T {
        return getTypedOrNull() ?: value
    }

    fun <T : Any> getOrElse(supplier: () -> T): T {
        return getTypedOrNull() ?: supplier()
    }

    fun <T : Any> getOrThrow(supplier: () -> Throwable): T {
        return getTypedOrNull() ?: throw supplier()
    }
}

/**
 * Setter for specified object.
 *
 * @see ObjectAccessor
 */
interface ObjectSetter {

    fun set(value: Any?)
}

/**
 * Accessor for specified object as typed.
 *
 * @see TypedGetter
 * @see TypedSetter
 * @see ObjectAccessor
 */
interface TypedAccessor<T : Any> : TypedGetter<T>, TypedSetter<T>

/**
 * Getter for specified object as typed.
 *
 * @see TypedAccessor
 */
interface TypedGetter<T : Any> {

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

    fun getOrThrow(supplier: () -> Throwable): T {
        return getOrNull() ?: throw supplier()
    }
}

/**
 * Setter for specified object as typed.
 *
 * @see TypedAccessor
 */
interface TypedSetter<T : Any> {

    fun set(value: T?)
}

/**
 * Accessor for map.
 *
 * This is a simple interface of [MutableMap].
 *
 * @see MapGetter
 * @see MapSetter
 * @see TypedMapAccessor
 */
interface MapAccessor : MapGetter, MapSetter

/**
 * Getter for map.
 *
 * This is a simple interface of [Map].
 *
 * @see MapAccessor
 */
interface MapGetter {

    fun isPresent(key: Any): Boolean {
        return asMap().containsKey(key)
    }

    @Throws(NullPointerException::class)
    fun get(key: Any): Any {
        return getOrNull(key)!!
    }

    fun getOrNull(key: Any): Any? {
        return asMap()[key]
    }

    @Throws(NullPointerException::class)
    fun <T : Any> getTyped(key: Any): T {
        return getTypedOrNull(key)!!
    }

    fun <T : Any> getTypedOrNull(key: Any): T? {
        return asMap()[key].asTyped()
    }

    fun <T : Any> getOrElse(key: Any, value: T): T {
        return getTypedOrNull(key) ?: value
    }

    fun <T : Any> getOrElse(key: Any, supplier: (key: Any) -> T): T {
        return getTypedOrNull(key) ?: supplier(key)
    }

    fun <T : Any> getOrThrow(key: Any, supplier: (key: Any) -> Throwable): T {
        return getTypedOrNull(key) ?: throw supplier(key)
    }

    /**
     * Returns a [Map] associated with current object,
     * any modification will reflect the returned map, and vice versa.
     */
    fun asMap(): Map<Any, Any?>
}

/**
 * Setter for map.
 *
 * This is a simple interface of [MutableMap].
 *
 * @see MapAccessor
 */
interface MapSetter {

    fun set(key: Any, value: Any?): Any? {
        return asMap().put(key, value)
    }

    fun remove(key: Any): Any? {
        return asMap().remove(key)
    }

    fun removeAll(keys: Array<out Any>) {
        asMap().removeAll(keys)
    }

    fun removeAll(keys: Iterable<Any>) {
        asMap().removeAll(keys)
    }

    fun removeAll(keys: Sequence<Any>) {
        asMap().removeAll(keys)
    }

    fun clear() {
        asMap().clear()
    }

    /**
     * Returns a [MutableMap] associated with current object,
     * any modification will reflect the returned map, and vice versa.
     */
    fun asMap(): MutableMap<Any, Any?>
}

/**
 * Accessor for typed map.
 *
 * This is a simple interface of [MutableMap].
 *
 * @see TypedMapGetter
 * @see TypedMapSetter
 * @see MapAccessor
 */
interface TypedMapAccessor<K : Any, V : Any> : TypedMapGetter<K, V>, TypedMapSetter<K, V>

/**
 * Getter for typed map.
 *
 * This is a simple interface of [Map].
 *
 * @see TypedMapAccessor
 */
interface TypedMapGetter<K : Any, V : Any> {

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

    fun getOrElse(key: K, supplier: (key: K) -> V): V {
        return getOrNull(key) ?: supplier(key)
    }

    fun getOrThrow(key: K, supplier: (key: K) -> Throwable): V {
        return getOrNull(key) ?: throw supplier(key)
    }

    /**
     * Returns a [Map] associated with current object,
     * any modification will reflect the returned map, and vice versa.
     */
    fun asMap(): Map<K, V?>
}

/**
 * Setter for typed map.
 *
 * This is a simple interface of [MutableMap].
 *
 * @see TypedMapAccessor
 */
interface TypedMapSetter<K : Any, V : Any> {

    fun set(key: K, value: V?): V? {
        return asMap().put(key, value)
    }

    fun remove(key: K): V? {
        return asMap().remove(key)
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