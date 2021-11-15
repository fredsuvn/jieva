@file:JvmName("BLazys")

package xyz.srclab.common.base

import java.time.Duration
import java.time.Instant

@JvmName("getter")
fun <T : Any> lazyGetter(supplier: () -> T): BLazyGetter<T> {
    return SimpleLazyGetter(supplier)
}

@JvmName("getter")
fun <T : Any> lazyGetter(period: Duration, supplier: () -> T): BLazyGetter<T> {
    return FixedPeriodLazyGetter(period, supplier)
}

@JvmName("getter")
fun <T : Any> lazyGetter(initPeriod: Duration, period: (T) -> Duration, supplier: () -> T): BLazyGetter<T> {
    return DynamicPeriodLazyGetter(initPeriod, period, supplier)
}

fun <T : Any> lazyString(lazyGetter: BLazyGetter<T>): BLazyString<T> {
    return BLazyString(lazyGetter)
}

/**
 * Lazy to create return value.
 *
 * @author sunqian
 */
interface BLazyGetter<T : Any> : BGetter<T> {

    fun refresh()

    fun refreshAndGet(): T {
        return refreshAndGetOrNull()!!
    }

    fun refreshAndGetOrNull(): T?
}

private class SimpleLazyGetter<T : Any>(private val supplier: () -> T) : BLazyGetter<T> {

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

private class FixedPeriodLazyGetter<T : Any>(
    private val period: Duration,
    private val supplier: () -> T,
) : BLazyGetter<T> {

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

private class DynamicPeriodLazyGetter<T : Any>(
    initPeriod: Duration,
    private val period: (T) -> Duration,
    private val supplier: () -> T,
) : BLazyGetter<T> {

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

/**
 * A special type of [BLazyGetter] of which [toString] method is overridden by result of [get].
 * It is used in where [toString] operation is expensive.
 */
open class BLazyString<T : Any>(delegate: BLazyGetter<T>) : BLazyGetter<T> by delegate {

    override fun toString(): String {
        return get().toString()
    }
}