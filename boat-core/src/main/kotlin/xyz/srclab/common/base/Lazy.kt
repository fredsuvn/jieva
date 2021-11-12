package xyz.srclab.common.base

import java.time.Duration
import java.time.Instant

/**
 * Lazy to create return value.
 *
 * @author sunqian
 */
interface Lazy<T : Any> : BGetter<T> {

    fun refresh()

    fun refreshAndGet(): T {
        return refreshAndGetOrNull()!!
    }

    fun refreshAndGetOrNull(): T?

    companion object {

        @JvmStatic
        fun <T : Any> of(supplier: () -> T): Lazy<T> {
            return OnceLazy(supplier)
        }

        @JvmStatic
        fun <T : Any> of(period: Duration, supplier: () -> T): Lazy<T> {
            return FixedPeriodRefreshableLazy(period, supplier)
        }

        @JvmStatic
        fun <T : Any> of(initPeriod: Duration, period: (T) -> Duration, supplier: () -> T): Lazy<T> {
            return DynamicPeriodRefreshableLazy(initPeriod, period, supplier)
        }
    }
}

/**
 * A special type of [Lazy] which overrides [toString] method by [get]. This class can be used in log message of
 * which [toString] is expensive.
 */
open class LazyToString<T : Any>(delegate: Lazy<T>) : Lazy<T> by delegate {

    override fun toString(): String {
        return get().toString()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun <T : Any> Lazy<T>.lazyToString(): LazyToString<T> {
            return LazyToString(this)
        }

        @JvmStatic
        fun <T : Any> of(supplier: () -> T): LazyToString<T> {
            return OnceLazy(supplier).lazyToString()
        }
    }
}

private class OnceLazy<T : Any>(private val supplier: () -> T) : Lazy<T> {

    @Volatile
    private var hasGet = false

    @Volatile
    private var lastValue: T? = null

    override fun getOrNull(): T? {
        if (hasGet) {
            return lastValue
        }
        return synchronized(this) {
            if (hasGet) {
                lastValue
            }
            lastValue = supplier()
            hasGet = true
            lastValue
        }
    }

    override fun refresh() {
        synchronized(this) {
            hasGet = false
        }
    }

    override fun refreshAndGetOrNull(): T? {
        return synchronized(this) {
            lastValue = supplier()
            hasGet = true
            lastValue
        }
    }
}

private class FixedPeriodRefreshableLazy<T : Any>(
    private val period: Duration,
    private val supplier: () -> T,
) : Lazy<T> {

    @Volatile
    private var lastGetTime: Instant = Instant.MIN

    @Volatile
    private var lastValue: T? = null

    override fun getOrNull(): T? {
        val now = Instant.now()
        if (!needRefresh(now)) {
            return lastValue
        }
        return synchronized(this) {
            if (!needRefresh(now)) {
                lastValue
            }
            lastValue = supplier()
            lastGetTime = Instant.now()
            lastValue
        }
    }

    override fun refresh() {
        synchronized(this) {
            lastGetTime = Instant.MIN
        }
    }

    override fun refreshAndGetOrNull(): T? {
        return synchronized(this) {
            lastValue = supplier()
            lastGetTime = Instant.now()
            lastValue
        }
    }

    private fun needRefresh(now: Instant): Boolean {
        return period < Duration.between(lastGetTime, now)
    }
}

private class DynamicPeriodRefreshableLazy<T : Any>(
    initPeriod: Duration,
    private val period: (T) -> Duration,
    private val supplier: () -> T,
) : Lazy<T> {

    @Volatile
    private var lastGetTime: Instant = Instant.MIN

    @Volatile
    private var lastPeriod: Duration = initPeriod

    @Volatile
    private var lastValue: T? = null

    override fun getOrNull(): T? {
        val now = Instant.now()
        if (!needRefresh(now)) {
            return lastValue
        }
        return synchronized(this) {
            if (!needRefresh(now)) {
                lastValue
            }
            val result = supplier()
            lastValue = result
            lastPeriod = period(result)
            lastGetTime = Instant.now()
            lastValue
        }
    }

    override fun refresh() {
        synchronized(this) {
            lastGetTime = Instant.MIN
        }
    }

    override fun refreshAndGetOrNull(): T? {
        return synchronized(this) {
            val result = supplier()
            lastValue = result
            lastPeriod = period(result)
            lastGetTime = Instant.now()
            lastValue
        }
    }

    private fun needRefresh(now: Instant): Boolean {
        return lastPeriod < Duration.between(lastGetTime, now)
    }
}