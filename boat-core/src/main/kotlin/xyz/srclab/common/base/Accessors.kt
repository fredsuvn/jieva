package xyz.srclab.common.base

/**
 * Accessor for any type.
 *
 * @see Getter
 * @see Setter
 * @see GenericAccessor
 */
interface Accessor : Getter, Setter

/**
 * Getter for any type.
 *
 * @see Accessor
 * @see GenericGetter
 */
interface Getter {

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
 * Setter for any type.
 *
 * @see Accessor
 * @see GenericSetter
 */
interface Setter {

    fun set(value: Any?)
}

/**
 * Accessor for generic type.
 *
 * @see GenericGetter
 * @see GenericSetter
 */
interface GenericAccessor<T : Any> : GenericGetter<T>, GenericSetter<T>

/**
 * Getter for generic type.
 *
 * @see GenericAccessor
 */
interface GenericGetter<T : Any> {

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
 * Setter for generic type.
 *
 * @see GenericAccessor
 */
interface GenericSetter<T : Any> {

    fun set(value: T?)
}