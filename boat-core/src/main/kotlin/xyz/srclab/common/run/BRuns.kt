@file:JvmName("BRuns")

package xyz.srclab.common.run

import xyz.srclab.common.base.availableProcessors
import java.time.Duration
import java.util.concurrent.*

fun singleThreadRunner(): ExecutorServiceRunner {
    return executorServiceRunner(Executors.newSingleThreadExecutor())
}

fun cachedThreadPoolRunner(): ExecutorServiceRunner {
    return executorServiceRunner(Executors.newCachedThreadPool())
}

fun fixedThreadPoolRunner(threadNumber: Int): ExecutorServiceRunner {
    return executorServiceRunner(Executors.newFixedThreadPool(threadNumber))
}

@JvmOverloads
fun workStealingPool(parallelism: Int = availableProcessors()): ExecutorServiceRunner {
    return executorServiceRunner(Executors.newWorkStealingPool(parallelism))
}

fun executorServiceRunner(executorService: ExecutorService): ExecutorServiceRunner {
    return ExecutorServiceRunner(executorService)
}

fun threadPoolRunner(threadPoolExecutor: ThreadPoolExecutor): ThreadPoolRunner {
    return ThreadPoolRunner(threadPoolExecutor)
}

fun threadPoolRunnerBuilder(): ThreadPoolRunner.Builder {
    return ThreadPoolRunner.Builder()
}

fun singleThreadScheduler(): ScheduledExecutorServiceScheduler {
    return executorServiceScheduler(Executors.newSingleThreadScheduledExecutor())
}

fun executorServiceScheduler(
    scheduledExecutorService: ScheduledExecutorService
): ScheduledExecutorServiceScheduler {
    return ScheduledExecutorServiceScheduler(scheduledExecutorService)
}

fun threadPoolScheduler(corePoolSize: Int): ScheduledExecutorServiceScheduler {
    return executorServiceScheduler(Executors.newScheduledThreadPool(corePoolSize))
}

fun threadPoolScheduler(
    scheduledThreadPoolExecutor: ScheduledThreadPoolExecutor
): ScheduledThreadPoolScheduler {
    return ScheduledThreadPoolScheduler(scheduledThreadPoolExecutor)
}

fun threadPoolSchedulerBuilder(): ScheduledThreadPoolScheduler.Builder {
    return ScheduledThreadPoolScheduler.Builder()
}

fun <V> runSync(task: () -> V): RunWork<V> {
    return SyncRunner.submit(task)
}

fun <V> runSync(task: Callable<V>): RunWork<V> {
    return SyncRunner.submit(task)
}

fun runSync(task: Runnable): RunWork<*> {
    return SyncRunner.submit(task)
}

fun <V> runSync(task: RunTask<V>): RunWork<V> {
    return SyncRunner.submit(task)
}

fun <V> runAsync(task: () -> V): RunWork<V> {
    return AsyncRunner.submit(task)
}

fun <V> runAsync(task: Callable<V>): RunWork<V> {
    return AsyncRunner.submit(task)
}

fun runAsync(task: Runnable): RunWork<*> {
    return AsyncRunner.submit(task)
}

fun <V> runAsync(task: RunTask<V>): RunWork<V> {
    return AsyncRunner.submit(task)
}

fun executeSync(task: () -> Any?) {
    return SyncRunner.run(task)
}

fun executeSync(task: Runnable) {
    return SyncRunner.run(task)
}

fun executeSync(task: RunTask<*>) {
    return SyncRunner.run(task)
}

fun executeAsync(task: () -> Any?) {
    return AsyncRunner.run(task)
}

fun executeAsync(task: Runnable) {
    return AsyncRunner.run(task)
}

fun executeAsync(task: RunTask<*>) {
    return AsyncRunner.run(task)
}

fun <V> schedule(delay: Duration, task: () -> V): ScheduleWork<V> {
    return SingleThreadScheduler.schedule(delay, task)
}

fun schedule(delay: Duration, task: Runnable): ScheduleWork<*> {
    return SingleThreadScheduler.schedule(delay, task)
}

fun <V> schedule(delay: Duration, task: RunTask<V>): ScheduleWork<V> {
    return SingleThreadScheduler.schedule(delay, task)
}

fun <V> scheduleFixedRate(initialDelay: Duration, period: Duration, task: () -> V): ScheduleWork<V> {
    return SingleThreadScheduler.scheduleFixedRate(initialDelay, period, task)
}

fun scheduleFixedRate(initialDelay: Duration, period: Duration, task: Runnable): ScheduleWork<*> {
    return SingleThreadScheduler.scheduleFixedRate(initialDelay, period, task)
}

fun <V> scheduleFixedRate(initialDelay: Duration, period: Duration, task: RunTask<V>): ScheduleWork<V> {
    return SingleThreadScheduler.scheduleFixedRate(initialDelay, period, task)
}

fun <V> scheduleFixedDelay(initialDelay: Duration, period: Duration, task: () -> V): ScheduleWork<V> {
    return SingleThreadScheduler.scheduleFixedDelay(initialDelay, period, task)
}

fun scheduleFixedDelay(initialDelay: Duration, period: Duration, task: Runnable): ScheduleWork<*> {
    return SingleThreadScheduler.scheduleFixedDelay(initialDelay, period, task)
}

fun <V> scheduleFixedDelay(initialDelay: Duration, period: Duration, task: RunTask<V>): ScheduleWork<V> {
    return SingleThreadScheduler.scheduleFixedDelay(initialDelay, period, task)
}