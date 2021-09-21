@file:JvmName("Runs")
@file:JvmMultifileClass

package xyz.srclab.common.run

import java.time.Duration

fun <V> runSync(task: () -> V): Running<V> {
    return Runner.runSync(task)
}

fun <V> runAsync(task: () -> V): Running<V> {
    return Runner.runAsync(task)
}

fun <V> fastRunSync(task: () -> V) {
    Runner.fastRunSync(task)
}

fun <V> fastRunAsync(task: () -> V) {
    Runner.fastRunSync(task)
}

fun <V> scheduleDefaultThread(delay: Duration, task: () -> V): Scheduling<V> {
    return Scheduler.scheduleDefaultThread(delay, task)
}

fun <V> scheduleDefaultThreadFixedRate(initialDelay: Duration, period: Duration, task: () -> V): Scheduling<V> {
    return Scheduler.scheduleDefaultThreadFixedRate(initialDelay, period, task)
}

fun <V> scheduleDefaultThreadFixedDelay(
    initialDelay: Duration,
    period: Duration,
    task: () -> V,
): Scheduling<V> {
    return Scheduler.scheduleDefaultThreadFixedDelay(initialDelay, period, task)
}