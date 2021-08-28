package xyz.srclab.common.lang

/**
 * Accessor for any type.
 *
 * @see AnyGetter
 * @see AnySetter
 * @see GenericAccessor
 */
interface AnyAccessor : AnyGetter, AnySetter

/**
 * Getter for any type.
 *
 * @see AnyAccessor
 */
interface AnyGetter {

    val isPresent: Boolean
        get() {
            return getOrNull<Any>() !== null
        }

    @Throws(NullPointerException::class)
    fun <T : Any> get(): T {
        return getOrNull() ?: throw NullPointerException()
    }

    fun <T : Any> getOrNull(): T?

    fun <T : Any> getOrElse(value: T): T {
        return getOrNull() ?: value
    }

    fun <T : Any> getOrElse(supplier: () -> T): T {
        return getOrNull() ?: supplier()
    }

    fun <T : Any> getOrThrow(supplier: () -> Throwable): T {
        return getOrNull() ?: throw supplier()
    }
}

/**
 * Setter for any type.
 *
 * @see AnyAccessor
 */
interface AnySetter {

    fun set(value: Any?)
}

/**
 * Accessor for generic type.
 *
 * @see GenericGetter
 * @see GenericSetter
 * @see AnyAccessor
 */
interface GenericAccessor<T : Any> : GenericGetter<T>, GenericSetter<T>

/**
 * Getter for generic type.
 *
 * @see GenericAccessor
 */
interface GenericGetter<T : Any> {

    val isPresent: Boolean
        get() {
            return getOrNull() !== null
        }

    @Throws(NullPointerException::class)
    fun get(): T {
        return getOrNull() ?: throw NullPointerException()
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
 * Setter for generic type.
 *
 * @see GenericAccessor
 */
interface GenericSetter<T : Any> {

    fun set(value: T?)
}

/**
 * Accessor for map type.
 *
 * @see MapGetter
 * @see MapSetter
 * @see GenericMapAccessor
 */
interface MapAccessor : MapGetter, MapSetter

/**
 * Getter for map type.
 *
 * @see MapAccessor
 */
interface MapGetter {

    fun isPresent(key: Any): Boolean {
        return getOrNull<Any>(key) !== null
    }

    @Throws(NullPointerException::class)
    fun <T : Any> get(key: Any): T {
        return getOrNull(key) ?: throw NullPointerException()
    }

    fun <T : Any> getOrNull(key: Any): T? {
        return asMap()[key].asAny()
    }

    fun <T : Any> getOrElse(key: Any, value: T): T {
        return getOrNull(key) ?: value
    }

    fun <T : Any> getOrElse(key: Any, supplier: (key: Any) -> T): T {
        return getOrNull(key) ?: supplier(key)
    }

    fun <T : Any> getOrThrow(key: Any, supplier: (key: Any) -> Throwable): T {
        return getOrNull(key) ?: throw supplier(key)
    }

    /**
     * Returns a [Map] associated with current object,
     * any modification will reflect the returned map, and vice versa.
     */
    fun asMap(): Map<Any, Any?>
}

/**
 * Setter for map type.
 *
 * @see MapAccessor
 */
interface MapSetter {

    fun set(key: Any, value: Any?) {
        asMap()[key] = value
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
 * Accessor for generic map type.
 *
 * @see GenericMapGetter
 * @see GenericMapSetter
 * @see MapAccessor
 */
interface GenericMapAccessor<K : Any, V : Any> : GenericMapGetter<K, V>, GenericMapSetter<K, V>

/**
 * Getter for generic map type.
 *
 * @see GenericMapAccessor
 */
interface GenericMapGetter<K : Any, V : Any> {

    fun isPresent(key: K): Boolean {
        return getOrNull(key) !== null
    }

    @Throws(NullPointerException::class)
    fun get(key: K): V {
        return getOrNull(key) ?: throw NullPointerException()
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
 * Setter for generic map type.
 *
 * @see GenericMapAccessor
 */
interface GenericMapSetter<K : Any, V : Any> {

    fun set(key: K, value: V?) {
        asMap()[key] = value
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