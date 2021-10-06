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

fun <V> runSync(task: RunTask<V>): Running<V> {
    return Runner.runSync(task)
}

fun <V> runAsync(task: () -> V): Running<V> {
    return Runner.runAsync(task)
}

fun runAsync(task: Runnable): Running<*> {
    return Runner.runAsync(task)
}

fun <V> runAsync(task: RunTask<V>): Running<V> {
    return Runner.runAsync(task)
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
fun scheduleWithSingleThread(delay: Duration, task: Runnable): Scheduling<*> {
    return Scheduler.scheduleWithSingleThread(delay, task)
}

/**
 * Schedules with [SingleThreadScheduler].
 */
fun <V> scheduleWithSingleThread(delay: Duration, task: RunTask<V>): Scheduling<V> {
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
fun scheduleFixedRateWithSingleThread(
    initialDelay: Duration,
    period: Duration,
    task: Runnable,
): Scheduling<*> {
    return Scheduler.scheduleFixedRateWithSingleThread(initialDelay, period, task)
}

/**
 * Schedules with [SingleThreadScheduler].
 */
fun <V> scheduleFixedRateWithSingleThread(
    initialDelay: Duration,
    period: Duration,
    task: RunTask<V>,
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
fun scheduleFixedDelayWithSingleThread(
    initialDelay: Duration,
    period: Duration,
    task: Runnable
): Scheduling<*> {
    return Scheduler.scheduleFixedDelayWithSingleThread(initialDelay, period, task)
}

/**
 * Schedules with [SingleThreadScheduler].
 */
fun <V> scheduleFixedDelayWithSingleThread(
    initialDelay: Duration,
    period: Duration,
    task: RunTask<V>
): Scheduling<V> {
    return Scheduler.scheduleFixedDelayWithSingleThread(initialDelay, period, task)
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