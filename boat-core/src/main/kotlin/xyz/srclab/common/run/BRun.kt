@file:JvmName("BRun")

package xyz.srclab.common.run

import java.time.Duration
import java.util.concurrent.Callable

/**
 * Submits an async [task].
 */
fun <V> submitAsync(task: Callable<V>): RunWork<V> {
    return AsyncRunner.submit(task)
}

/**
 * Submits an async [task].
 */
fun submitAsync(task: Runnable): RunWork<*> {
    return AsyncRunner.submit(task)
}

/**
 * Submits an async [task].
 */
fun <V> submitAsync(task: RunTask<V>): RunWork<V> {
    return AsyncRunner.submit(task)
}

/**
 * Runs an async [task].
 */
fun runAsync(task: Runnable) {
    return AsyncRunner.run(task)
}

/**
 * Runs an async [task].
 */
fun runAsync(task: RunTask<*>) {
    return AsyncRunner.run(task)
}

/**
 * Schedules a [task].
 */
fun <V> schedule(delay: Duration, task: Callable<V>): ScheduleWork<V> {
    return SingleThreadScheduler.schedule(delay, task)
}

/**
 * Schedules a [task].
 */
fun schedule(delay: Duration, task: Runnable): ScheduleWork<*> {
    return SingleThreadScheduler.schedule(delay, task)
}

/**
 * Schedules a [task].
 */
fun <V> schedule(delay: Duration, task: RunTask<V>): ScheduleWork<V> {
    return SingleThreadScheduler.schedule(delay, task)
}

/**
 * Schedules a [task] with fixed rate.
 */
fun scheduleFixedRate(initialDelay: Duration, period: Duration, task: Runnable): ScheduleWork<*> {
    return SingleThreadScheduler.scheduleFixedRate(initialDelay, period, task)
}

/**
 * Schedules a [task] with fixed rate.
 */
fun scheduleFixedRate(initialDelay: Duration, period: Duration, task: RunTask<*>): ScheduleWork<*> {
    return SingleThreadScheduler.scheduleFixedRate(initialDelay, period, task)
}

/**
 * Schedules a [task] with fixed delay.
 */
fun scheduleFixedDelay(initialDelay: Duration, period: Duration, task: Runnable): ScheduleWork<*> {
    return SingleThreadScheduler.scheduleFixedDelay(initialDelay, period, task)
}

/**
 * Schedules a [task] with fixed delay.
 */
fun scheduleFixedDelay(initialDelay: Duration, period: Duration, task: RunTask<*>): ScheduleWork<*> {
    return SingleThreadScheduler.scheduleFixedDelay(initialDelay, period, task)
}