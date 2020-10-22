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

    fun orElse(value: T): T {
        return getOrNull() ?: value
    }

    fun orElseGet(supplier: () -> T): T {
        return getOrNull() ?: supplier()
    }

    fun orElseThrow(supplier: () -> Throwable): T {
        return getOrNull() ?: throw supplier()
    }
}

interface SimpleSetter<T> {

    fun set(value: T?)
}