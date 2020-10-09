package xyz.srclab.common.base

import java.time.Duration
import java.util.function.Supplier

/**
 * Caches a computed value, particularly in cases the value is optional and has a heavy process to get.
 *
 * @param <T> computed value
 */
interface Computed<T> {

    fun get(): T

    fun refresh()

    fun refreshAndGet(): T

    interface Result<T> {
        fun value(): T
        fun expiry(): Duration?
    }

    companion object {
        fun <T> onceOf(computation: Supplier<T>?): Computed<T>? {
            return ComputedSupport.newOnceComputed(computation)
        }

        fun <T> multiOf(computation: Supplier<Result<T>?>?): Computed<T>? {
            return ComputedSupport.newMultiComputed(computation)
        }

        fun <T> refreshableOf(timeoutNanos: Long, computation: Supplier<T>?): Computed<T>? {
            return ComputedSupport.newRefreshableComputed(timeoutNanos, computation)
        }

        fun <T> refreshableOf(duration: Duration?, computation: Supplier<T>?): Computed<T>? {
            return ComputedSupport.newRefreshableComputed(duration, computation)
        }
    }
}