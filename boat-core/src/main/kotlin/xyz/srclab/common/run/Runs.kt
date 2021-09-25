@file:JvmName("Runs")
@file:JvmMultifileClass

package xyz.srclab.common.run

import java.time.Duration


fun <V> runSync(task: () -> V): Running<V> {
    return Runner.runSync(task)
}

fun runSync(task: Runnable): Running<*> {
    return Runner.runSync(task)
}

fun <V> runAsync(task: () -> V): Running<V> {
    return Runner.runAsync(task)
}

fun runAsync(task: Runnable): Running<*> {
    return Runner.runAsync(task)
}

fun <V> runSync(statistics: Boolean, task: () -> V): Running<V> {
    return Runner.runSync(statistics, task)
}

fun runSync(statistics: Boolean, task: Runnable): Running<*> {
    return Runner.runSync(statistics, task)
}

fun <V> runAsync(statistics: Boolean, task: () -> V): Running<V> {
    return Runner.runAsync(statistics, task)
}

fun runAsync(statistics: Boolean, task: Runnable): Running<*> {
    return Runner.runAsync(statistics, task)
}

fun <V> executeSync(task: () -> V) {
    return Runner.executeSync(task)
}

fun executeSync(task: Runnable) {
    return Runner.executeSync(task)
}

fun <V> executeAsync(task: () -> V) {
    return Runner.executeAsync(task)
}

fun executeAsync(task: Runnable) {
    return Runner.executeAsync(task)
}

/**
 * Schedules with [SingleThreadScheduler].
 */
fun <V> scheduleWithSingleThread(delay: Duration, task: () -> V): Scheduling<V> {
    return Scheduler.scheduleWithSingleThread(delay, task)
}

/**
 * Schedules with [SingleThreadScheduler].
 */
fun <V> scheduleFixedRateWithSingleThread(
    initialDelay: Duration,
    period: Duration,
    task: () -> V,
): Scheduling<V> {
    return Scheduler.scheduleFixedRateWithSingleThread(initialDelay, period, task)
}

/**
 * Schedules with [SingleThreadScheduler].
 */
fun <V> scheduleFixedDelayWithSingleThread(
    initialDelay: Duration,
    period: Duration,
    task: () -> V
): Scheduling<V> {
    return Scheduler.scheduleFixedDelayWithSingleThread(initialDelay, period, task)
}

/**
 * Schedules with [SingleThreadScheduler].
 */
fun <V> scheduleWithSingleThread(statistics: Boolean, delay: Duration, task: () -> V): Scheduling<V> {
    return Scheduler.scheduleWithSingleThread(statistics, delay, task)
}

/**
 * Schedules with [SingleThreadScheduler].
 */
fun <V> scheduleFixedRateWithSingleThread(
    statistics: Boolean,
    initialDelay: Duration,
    period: Duration,
    task: () -> V,
): Scheduling<V> {
    return Scheduler.scheduleFixedRateWithSingleThread(statistics, initialDelay, period, task)
}

/**
 * Schedules with [SingleThreadScheduler].
 */
fun <V> scheduleFixedDelayWithSingleThread(
    statistics: Boolean,
    initialDelay: Duration,
    period: Duration,
    task: () -> V
): Scheduling<V> {
    return Scheduler.scheduleFixedDelayWithSingleThread(statistics, initialDelay, period, task)
}

fun Runnable.toFunction(): () -> Any? {
    return {
        this.run()
        null
    }
}

fun (() -> Any?).toRunnable(): Runnable {
    return Runnable { this() }
}