package xyz.srclab.common.base

/**
 * @author sunqian
 */
interface Lazy<T> {

    fun get(): T

    companion object {

        @JvmStatic
        fun <T> of(initializer: () -> T): Lazy<T> {
            return LazyImpl(lazy(initializer))
        }

        private class LazyImpl<T>(private val kotlinLazy: kotlin.Lazy<T>) : Lazy<T> {

            override fun get(): T {
                return kotlinLazy.value
            }
        }
    }
}