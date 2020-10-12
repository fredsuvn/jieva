package xyz.srclab.common.base

import java.time.Duration
import java.time.LocalDateTime

/**
 * @author sunqian
 */
interface Lazy<T> {

    fun get(): T

    fun refresh()

    fun refreshAndGet(): T

    companion object {

        @JvmStatic
        fun <T> once(supplier: () -> T): Lazy<T> {
            return OnceLazy(supplier)
        }

        @JvmStatic
        fun <T> period(period: Duration, supplier: () -> T): Lazy<T> {
            return FixedPeriodRefreshableLazy(period, supplier)
        }

        @JvmStatic
        fun <T> period(supplier: () -> Pair<T, Duration>): Lazy<T> {
            return DynamicPeriodRefreshableLazy(supplier)
        }
    }
}

fun <T> lazyOf(supplier: () -> T): Lazy<T> {
    return Lazy.once(supplier)
}

fun <T> periodLazyOf(period: Duration, supplier: () -> T): Lazy<T> {
    return Lazy.period(period, supplier)
}

fun <T> periodLazyOf(supplier: () -> Pair<T, Duration>): Lazy<T> {
    return Lazy.period(supplier)
}

private class OnceLazy<T>(supplier: () -> T) : Lazy<T> {

    private val kotlinLazy = lazy(supplier)

    override fun get(): T {
        return kotlinLazy.value
    }

    override fun refresh() {
        throw UnsupportedOperationException()
    }

    override fun refreshAndGet(): T {
        throw UnsupportedOperationException()
    }
}

private class FixedPeriodRefreshableLazy<T>(private val period: Duration, private val supplier: () -> T) : Lazy<T> {

    @Volatile
    private var lastGetTime: LocalDateTime = LocalDateTime.MIN

    @Volatile
    private var lastValue: T? = null

    override fun get(): T {
        if (!needRefresh()) {
            return lastValue.asAny()
        }
        return synchronized(this) {
            if (!needRefresh()) {
                lastValue
            }
            lastValue = supplier()
            lastGetTime = LocalDateTime.now()
            lastValue.asAny()
        }
    }

    private fun needRefresh(): Boolean {
        return period < Duration.between(lastGetTime, LocalDateTime.now())
    }

    override fun refresh() {
        synchronized(this) {
            lastGetTime = LocalDateTime.MIN
        }
    }

    override fun refreshAndGet(): T {
        return synchronized(this) {
            lastValue = supplier()
            lastGetTime = LocalDateTime.now()
            lastValue.asAny()
        }
    }
}

private class DynamicPeriodRefreshableLazy<T>(private val supplier: () -> Pair<T, Duration>) : Lazy<T> {

    @Volatile
    private var lastGetTime: LocalDateTime = LocalDateTime.MIN

    @Volatile
    private var lastPeriod: Duration = Duration.ZERO

    @Volatile
    private var lastValue: T? = null

    override fun get(): T {
        if (!needRefresh()) {
            return lastValue.asAny()
        }
        return synchronized(this) {
            if (!needRefresh()) {
                lastValue
            }
            val result = supplier()
            lastValue = result.first
            lastPeriod = result.second
            lastGetTime = LocalDateTime.now()
            lastValue.asAny()
        }
    }

    private fun needRefresh(): Boolean {
        return lastPeriod < Duration.between(lastGetTime, LocalDateTime.now())
    }

    override fun refresh() {
        synchronized(this) {
            lastGetTime = LocalDateTime.MIN
            lastPeriod = Duration.ZERO
        }
    }

    override fun refreshAndGet(): T {
        return synchronized(this) {
            val result = supplier()
            lastValue = result.first
            lastPeriod = result.second
            lastGetTime = LocalDateTime.now()
            lastValue.asAny()
        }
    }
}