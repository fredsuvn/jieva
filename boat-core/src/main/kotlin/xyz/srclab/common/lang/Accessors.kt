package xyz.srclab.common.lang

/**
 * Single object accessor for any type.
 *
 * @see SingleGetter
 * @see SingleSetter
 * @see GenericSingleAccessor
 * @see MapAccessor
 */
interface SingleAccessor : SingleGetter, SingleSetter

/**
 * Single object getter for any type.
 *
 * @see SingleAccessor
 */
interface SingleGetter {

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val isPresent: Boolean
        @JvmName("isPresent") get() {
            return getOrNull<Any>() !== null
        }

    @Throws(NullPointerException::class)
    @JvmDefault
    fun <T : Any> get(): T {
        return getOrNull() ?: throw NullPointerException()
    }

    fun <T : Any> getOrNull(): T?

    @JvmDefault
    fun <T : Any> getOrElse(value: T): T {
        return getOrNull() ?: value
    }

    @JvmDefault
    fun <T : Any> getOrElse(supplier: () -> T): T {
        return getOrNull() ?: supplier()
    }

    @JvmDefault
    fun <T : Any> getOrThrow(supplier: () -> Throwable): T {
        return getOrNull() ?: throw supplier()
    }
}

/**
 * Single object setter for any type.
 *
 * @see SingleAccessor
 */
interface SingleSetter {

    fun set(value: Any?)
}

/**
 * Map accessor for any type.
 *
 * @see MapGetter
 * @see MapSetter
 * @see GenericMapAccessor
 * @see SingleAccessor
 */
interface MapAccessor : MapGetter, MapSetter

/**
 * Map getter for any type.
 *
 * @see MapAccessor
 */
interface MapGetter {

    @get:JvmName("contents")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val contents: Map<Any, Any?>

    @Throws(NullPointerException::class)
    @JvmDefault
    fun <T : Any> get(key: Any): T {
        return contents[key]?.asAny() ?: throw NullPointerException()
    }

    @JvmDefault
    fun <T : Any> getOrNull(key: Any): T? {
        return contents[key]?.asAny()
    }

    @JvmDefault
    fun <T : Any> getOrElse(key: Any, elseValue: T): T {
        val result = contents.getOrDefault(key, elseValue)
        if (result === null) {
            return elseValue
        }
        return result.asAny()
    }

    @JvmDefault
    fun <T : Any> getOrElse(key: Any, supplier: (key: Any) -> T): T {
        val result = contents.getOrDefault(key, ELSE_VALUE)
        if (result === null || result === ELSE_VALUE) {
            return supplier(key)
        }
        return result.asAny()
    }

    @JvmDefault
    fun <T : Any> getOrThrow(key: Any, supplier: (key: Any) -> Throwable): T {
        val result = contents.getOrDefault(key, ELSE_VALUE)
        if (result === null || result === ELSE_VALUE) {
            throw supplier(key)
        }
        return result.asAny()
    }

    companion object {

        private const val ELSE_VALUE = "ELSE_VALUE"
    }
}

/**
 * Map setter for any type.
 *
 * @see MapAccessor
 */
interface MapSetter {

    @get:JvmName("contents")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val contents: MutableMap<Any, Any?>

    @JvmDefault
    fun set(key: Any, value: Any?) {
        contents[key] = value
    }

    @JvmDefault
    fun clear() {
        contents.clear()
    }
}

/**
 * Single object accessor for generic type.
 *
 * @see GenericSingleGetter
 * @see GenericSingleSetter
 * @see SingleAccessor
 * @see GenericMapAccessor
 */
interface GenericSingleAccessor<T : Any> : GenericSingleGetter<T>, GenericSingleSetter<T>

/**
 * Single object getter for generic type.
 *
 * @see GenericSingleAccessor
 */
interface GenericSingleGetter<T : Any> {

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val isPresent: Boolean
        @JvmName("isPresent") get() {
            return getOrNull() !== null
        }

    @Throws(NullPointerException::class)
    @JvmDefault
    fun get(): T {
        return getOrNull() ?: throw NullPointerException()
    }

    fun getOrNull(): T?

    @JvmDefault
    fun getOrElse(value: T): T {
        return getOrNull() ?: value
    }

    @JvmDefault
    fun getOrElse(supplier: () -> T): T {
        return getOrNull() ?: supplier()
    }

    @JvmDefault
    fun getOrThrow(supplier: () -> Throwable): T {
        return getOrNull() ?: throw supplier()
    }
}

/**
 * Single object setter for generic type.
 *
 * @see GenericSingleAccessor
 */
interface GenericSingleSetter<T : Any> {

    fun set(value: T?)
}

/**
 * Map accessor for generic type.
 *
 * @see GenericMapGetter
 * @see GenericMapSetter
 * @see MapAccessor
 * @see GenericSingleAccessor
 */
interface GenericMapAccessor<K : Any, V : Any> : GenericMapGetter<K, V>, GenericMapSetter<K, V>

/**
 * Map getter for generic type.
 *
 * @see GenericMapAccessor
 */
interface GenericMapGetter<K : Any, V : Any> {

    @get:JvmName("contents")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val contents: Map<K, V?>

    @Throws(NullPointerException::class)
    @JvmDefault
    fun get(key: K): V {
        return contents[key]?.asAny() ?: throw NullPointerException()
    }

    @JvmDefault
    fun getOrNull(key: K): V? {
        return contents[key]?.asAny()
    }

    @JvmDefault
    fun getOrElse(key: K, elseValue: V): V {
        val result = contents.getOrDefault(key, elseValue)
        if (result === null) {
            return elseValue
        }
        return result.asAny()
    }

    @JvmDefault
    fun getOrElse(key: K, supplier: (key: K) -> V): V {
        val result = contents.getOrDefault(key, ELSE_VALUE)
        if (result === null || result === ELSE_VALUE) {
            return supplier(key)
        }
        return result.asAny()
    }

    @JvmDefault
    fun getOrThrow(key: K, supplier: (key: K) -> Throwable): V {
        val result = contents.getOrDefault(key, ELSE_VALUE)
        if (result === null || result === ELSE_VALUE) {
            throw supplier(key)
        }
        return result.asAny()
    }

    companion object {

        private const val ELSE_VALUE = "ELSE_VALUE"
    }
}

/**
 * Map setter for generic type.
 *
 * @see GenericMapAccessor
 */
interface GenericMapSetter<K : Any, V : Any> {

    @get:JvmName("contents")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val contents: MutableMap<K, V?>

    @JvmDefault
    fun set(key: K, value: V?) {
        contents[key] = value
    }

    @JvmDefault
    fun clear() {
        contents.clear()
    }
}