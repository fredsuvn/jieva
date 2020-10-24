package xyz.srclab.common.base

interface SimpleAccess<T> : SimpleGetter<T>, SimpleSetter<T>

interface SimpleGetter<T> {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val isPresent: Boolean
        @JvmName("isPresent") get() {
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

interface SimpleSetter<T> {

    fun set(value: T?)
}