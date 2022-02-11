@file:JvmName("BRun")

package xyz.srclab.common.run

import xyz.srclab.common.base.availableProcessors
import java.time.Duration
import java.util.concurrent.*

fun newSingleThreadRunner(): ExecutorServiceRunner {
    return newExecutorServiceRunner(Executors.newSingleThreadExecutor())
}

fun newCachedThreadPoolRunner(): ExecutorServiceRunner {
    return newExecutorServiceRunner(Executors.newCachedThreadPool())
}

fun newFixedThreadPoolRunner(threadNumber: Int): ExecutorServiceRunner {
    return newExecutorServiceRunner(Executors.newFixedThreadPool(threadNumber))
}

@JvmOverloads
fun newWorkStealingPool(parallelism: Int = availableProcessors()): ExecutorServiceRunner {
    return newExecutorServiceRunner(Executors.newWorkStealingPool(parallelism))
}

fun newExecutorServiceRunner(executorService: ExecutorService): ExecutorServiceRunner {
    return ExecutorServiceRunner(executorService)
}

fun newThreadPoolRunner(threadPoolExecutor: ThreadPoolExecutor): ThreadPoolRunner {
    return ThreadPoolRunner(threadPoolExecutor)
}

fun newThreadPoolRunnerBuilder(): ThreadPoolRunner.Builder {
    return ThreadPoolRunner.Builder()
}

fun newSingleThreadScheduler(): ScheduledExecutorServiceScheduler {
    return newExecutorServiceScheduler(Executors.newSingleThreadScheduledExecutor())
}

fun newExecutorServiceScheduler(
    scheduledExecutorService: ScheduledExecutorService
): ScheduledExecutorServiceScheduler {
    return ScheduledExecutorServiceScheduler(scheduledExecutorService)
}

fun newThreadPoolScheduler(corePoolSize: Int): ScheduledExecutorServiceScheduler {
    return newExecutorServiceScheduler(Executors.newScheduledThreadPool(corePoolSize))
}

fun newThreadPoolScheduler(
    scheduledThreadPoolExecutor: ScheduledThreadPoolExecutor
): ScheduledThreadPoolScheduler {
    return ScheduledThreadPoolScheduler(scheduledThreadPoolExecutor)
}

fun newThreadPoolSchedulerBuilder(): ScheduledThreadPoolScheduler.Builder {
    return ScheduledThreadPoolScheduler.Builder()
}

@JvmSynthetic
fun <V> submitSync(task: () -> V): RunWork<V> {
    return SyncRunner.submit(task)
}

fun <V> submitSync(task: Callable<V>): RunWork<V> {
    return SyncRunner.submit(task)
}

fun submitSync(task: Runnable): RunWork<*> {
    return SyncRunner.submit(task)
}

fun <V> submitSync(task: RunTask<V>): RunWork<V> {
    return SyncRunner.submit(task)
}

@JvmSynthetic
fun <V> submitAsync(task: () -> V): RunWork<V> {
    return AsyncRunner.submit(task)
}

fun <V> submitAsync(task: Callable<V>): RunWork<V> {
    return AsyncRunner.submit(task)
}

fun submitAsync(task: Runnable): RunWork<*> {
    return AsyncRunner.submit(task)
}

fun <V> submitAsync(task: RunTask<V>): RunWork<V> {
    return AsyncRunner.submit(task)
}

@JvmSynthetic
fun runSync(task: () -> Any?) {
    return SyncRunner.run(task)
}

fun runSync(task: Runnable) {
    return SyncRunner.run(task)
}

fun runSync(task: RunTask<*>) {
    return SyncRunner.run(task)
}

@JvmSynthetic
fun runAsync(task: () -> Any?) {
    return AsyncRunner.run(task)
}

fun runAsync(task: Runnable) {
    return AsyncRunner.run(task)
}

fun runAsync(task: RunTask<*>) {
    return AsyncRunner.run(task)
}

@JvmSynthetic
fun <V> schedule(delay: Duration, task: () -> V): ScheduleWork<V> {
    return SingleThreadScheduler.schedule(delay, task)
}

fun <V> schedule(delay: Duration, task: Callable<V>): ScheduleWork<V> {
    return SingleThreadScheduler.schedule(delay, task)
}

fun schedule(delay: Duration, task: Runnable): ScheduleWork<*> {
    return SingleThreadScheduler.schedule(delay, task)
}

fun <V> schedule(delay: Duration, task: RunTask<V>): ScheduleWork<V> {
    return SingleThreadScheduler.schedule(delay, task)
}

@JvmSynthetic
fun scheduleFixedRate(initialDelay: Duration, period: Duration, task: () -> Any?): ScheduleWork<*> {
    return SingleThreadScheduler.scheduleFixedRate(initialDelay, period, task)
}

fun scheduleFixedRate(initialDelay: Duration, period: Duration, task: Runnable): ScheduleWork<*> {
    return SingleThreadScheduler.scheduleFixedRate(initialDelay, period, task)
}

fun scheduleFixedRate(initialDelay: Duration, period: Duration, task: RunTask<*>): ScheduleWork<*> {
    return SingleThreadScheduler.scheduleFixedRate(initialDelay, period, task)
}

@JvmSynthetic
fun scheduleFixedDelay(initialDelay: Duration, period: Duration, task: () -> Any?): ScheduleWork<*> {
    return SingleThreadScheduler.scheduleFixedDelay(initialDelay, period, task)
}

fun scheduleFixedDelay(initialDelay: Duration, period: Duration, task: Runnable): ScheduleWork<*> {
    return SingleThreadScheduler.scheduleFixedDelay(initialDelay, period, task)
}

fun scheduleFixedDelay(initialDelay: Duration, period: Duration, task: RunTask<*>): ScheduleWork<*> {
    return SingleThreadScheduler.scheduleFixedDelay(initialDelay, period, task)
}