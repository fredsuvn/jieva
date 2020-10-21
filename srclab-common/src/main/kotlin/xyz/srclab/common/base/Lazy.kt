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
        fun <T> of(supplier: () -> T): Lazy<T> {
            return OnceLazy(supplier)
        }

        @JvmStatic
        fun <T> periodOf(period: Duration, supplier: () -> T): Lazy<T> {
            return FixedPeriodRefreshableLazy(period, supplier)
        }

        @JvmStatic
        fun <T> periodOf(supplier: () -> Pair<T, Duration>): Lazy<T> {
            return DynamicPeriodRefreshableLazy(supplier)
        }
    }
}

fun <T> lazyOf(supplier: () -> T): Lazy<T> {
    return Lazy.of(supplier)
}

fun <T> periodLazyOf(period: Duration, supplier: () -> T): Lazy<T> {
    return Lazy.periodOf(period, supplier)
}

fun <T> periodLazyOf(supplier: () -> Pair<T, Duration>): Lazy<T> {
    return Lazy.periodOf(supplier)
}

private class OnceLazy<T>(private val supplier: () -> T) : Lazy<T> {

    @Volatile
    private var hasGet = false

    @Volatile
    private var lastValue: T? = null

    override fun get(): T {
        if (hasGet) {
            return lastValue.asNotNull()
        }
        return synchronized(this) {
            if (hasGet) {
                lastValue
            }
            lastValue = supplier()
            hasGet = true
            lastValue.asNotNull()
        }
    }

    override fun refresh() {
        synchronized(this) {
            hasGet = false
        }
    }

    override fun refreshAndGet(): T {
        return synchronized(this) {
            lastValue = supplier()
            hasGet = true
            lastValue.asNotNull()
        }
    }
}

private class FixedPeriodRefreshableLazy<T>(private val period: Duration, private val supplier: () -> T) : Lazy<T> {

    @Volatile
    private var lastGetTime: LocalDateTime = LocalDateTime.MIN

    @Volatile
    private var lastValue: T? = null

    override fun get(): T {
        if (!needRefresh()) {
            return lastValue.asNotNull()
        }
        return synchronized(this) {
            if (!needRefresh()) {
                lastValue
            }
            lastValue = supplier()
            lastGetTime = LocalDateTime.now()
            lastValue.asNotNull()
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
            lastValue.asNotNull()
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
            return lastValue.asNotNull()
        }
        return synchronized(this) {
            if (!needRefresh()) {
                lastValue
            }
            val result = supplier()
            lastValue = result.first
            lastPeriod = result.second
            lastGetTime = LocalDateTime.now()
            lastValue.asNotNull()
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
            lastValue.asNotNull()
        }
    }
}