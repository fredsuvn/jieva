package xyz.srclab.common.lang

import java.time.Duration
import java.time.LocalDateTime

/**
 * Lazy to create return value.
 *
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
        fun <T> of(period: Duration, supplier: () -> T): Lazy<T> {
            return FixedPeriodRefreshableLazy(period, supplier)
        }

        @JvmStatic
        fun <T> of(period: (T) -> Duration, supplier: () -> T): Lazy<T> {
            return DynamicPeriodRefreshableLazy(period, supplier)
        }
    }
}

/**
 * A special type of [Lazy] which overrides [toString] method by [get]. This class can be used in log message of
 * which [toString] is expensive.
 */
open class LazyToString<T>(delegate: Lazy<T>) : Lazy<T> by delegate {

    override fun toString(): String {
        return get().toString()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun <T> Lazy<T>.lazyToString(): LazyToString<T> {
            return LazyToString(this)
        }

        @JvmStatic
        fun <T> of(supplier: () -> T): LazyToString<T> {
            return OnceLazy(supplier).lazyToString()
        }
    }
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

private class FixedPeriodRefreshableLazy<T>(
    private val period: Duration,
    private val supplier: () -> T,
) : Lazy<T> {

    @Volatile
    private var lastGetTime: LocalDateTime = LocalDateTime.MIN

    @Volatile
    private var lastValue: T? = null

    override fun get(): T {
        val now = LocalDateTime.now()
        if (!needRefresh(now)) {
            return lastValue.asNotNull()
        }
        return synchronized(this) {
            if (!needRefresh(now)) {
                lastValue
            }
            lastValue = supplier()
            lastGetTime = LocalDateTime.now()
            lastValue.asNotNull()
        }
    }

    private fun needRefresh(now: LocalDateTime): Boolean {
        return period < Duration.between(lastGetTime, now)
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

private class DynamicPeriodRefreshableLazy<T>(
    private val period: (T) -> Duration,
    private val supplier: () -> T,
) : Lazy<T> {

    @Volatile
    private var lastGetTime: LocalDateTime = LocalDateTime.MIN

    @Volatile
    private var lastPeriod: Duration = Duration.ZERO

    @Volatile
    private var lastValue: T? = null

    override fun get(): T {
        val now = LocalDateTime.now()
        if (!needRefresh(now)) {
            return lastValue.asNotNull()
        }
        return synchronized(this) {
            if (!needRefresh(now)) {
                lastValue
            }
            val result = supplier()
            lastValue = result
            lastPeriod = period(result)
            lastGetTime = LocalDateTime.now()
            lastValue.asNotNull()
        }
    }

    private fun needRefresh(now: LocalDateTime): Boolean {
        return lastPeriod < Duration.between(lastGetTime, now)
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
            lastValue = result
            lastPeriod = period(result)
            lastGetTime = LocalDateTime.now()
            lastValue.asNotNull()
        }
    }
}