package xyz.srclab.common.base

/**
 * @see SimpleGetter
 * @see SimpleSetter
 */
interface SimpleAccessor<T> : SimpleGetter<T>, SimpleSetter<T>

/**
 * @see SimpleAccessor
 */
interface SimpleGetter<T> {

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
 * @see SimpleAccessor
 */
interface SimpleSetter<T> {

    fun set(value: T?)
}